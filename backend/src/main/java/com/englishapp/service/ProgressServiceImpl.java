package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.LearnedLessonDto;
import com.englishapp.dto.LearnedStatsDto;
import com.englishapp.dto.LearnedStatsDto.SubjectCount;
import com.englishapp.dto.LearnedStatsDto.TypeCount;
import com.englishapp.dto.ProgressDto;
import com.englishapp.dto.UnitProgressDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.UserProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 进度业务实现
 * <p>
 * 基于 {@link UserProgressRepository} 与 {@link LessonRepository} 实现课时进度的查询与完成逻辑。
 * 核心设计要点:
 * <ul>
 *     <li>枚举适配:由于 {@link ProgressStatus} 不含 {@code UNLOCKED},统一使用
 *         {@code IN_PROGRESS} 表示"可学习"语义</li>
 *     <li>取最高分:重复完成同一课时时,保留历史最高分与最高星数</li>
 *     <li>已取消锁定机制:无进度记录的课时统一返回 {@code IN_PROGRESS},用户可自由学习</li>
 * </ul>
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@Service
public class ProgressServiceImpl implements ProgressService {

    /** 默认用户 ID(在未传入 userId 时使用) */
    private static final Integer DEFAULT_USER_ID = 1;

    private final UserProgressRepository userProgressRepository;
    private final LessonRepository lessonRepository;

    /**
     * 构造进度业务实现
     *
     * @param userProgressRepository 用户进度仓库
     * @param lessonRepository       课程仓库
     */
    public ProgressServiceImpl(UserProgressRepository userProgressRepository,
                               LessonRepository lessonRepository) {
        this.userProgressRepository = userProgressRepository;
        this.lessonRepository = lessonRepository;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 已取消锁定机制:无进度记录时统一返回 {@code IN_PROGRESS}(可学习),
     * 不再区分首课与其余课时。
     * </p>
     */
    @Override
    public ProgressDto getLessonProgress(Integer lessonId, Integer userId) {
        // 未指定用户时使用默认用户,便于匿名/调试场景
        Integer uid = userId != null ? userId : DEFAULT_USER_ID;
        Optional<UserProgress> progress = userProgressRepository
                .findByUserIdAndLessonId(uid, lessonId);
        if (progress.isPresent()) {
            UserProgress up = progress.get();
            return new ProgressDto(lessonId, up.getStatus().name(),
                    up.getStars(), up.getScore());
        }
        // 已取消锁定机制:无记录时统一返回 IN_PROGRESS(可学习)
        return new ProgressDto(lessonId, ProgressStatus.IN_PROGRESS.name(), 0, 0);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 已取消锁定机制:本方法仅负责记录最高分、标记 COMPLETED 并返回
     * {@code CompleteResponse(null, false)},不再解锁下一课时。
     * </p>
     */
    @Override
    @Transactional
    public CompleteResponse completeLesson(Integer lessonId, CompleteRequest request, Integer userId) {
        // 未指定用户时使用默认用户,便于匿名/调试场景
        Integer uid = userId != null ? userId : DEFAULT_USER_ID;
        UserProgress progress = userProgressRepository
                .findByUserIdAndLessonId(uid, lessonId)
                .orElseGet(() -> {
                    // 首次完成该课时:创建一条新进度记录,初始状态为 IN_PROGRESS
                    UserProgress newUp = new UserProgress();
                    newUp.setUserId(uid);
                    newUp.setLessonId(lessonId);
                    newUp.setStatus(ProgressStatus.IN_PROGRESS);
                    newUp.setStars(0);
                    newUp.setScore(0);
                    return newUp;
                });

        // 取最高分(允许重复挑战):保留历史最佳成绩,避免重做低分覆盖高分
        Integer bestScore = Math.max(progress.getScore(), request.getScore());
        Integer bestStars = Math.max(progress.getStars(), request.getStars());
        progress.setStatus(ProgressStatus.COMPLETED);
        progress.setStars(bestStars);
        progress.setScore(bestScore);
        // 记录完成时间,便于"我学过的"列表按时间排序展示
        progress.setCompletedAt(java.time.LocalDateTime.now());
        userProgressRepository.save(progress);

        // 已取消锁定机制:不再解锁下一课时,统一返回无下一课
        return new CompleteResponse(null, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 已取消锁定机制:无进度记录的课时统一返回 {@code IN_PROGRESS}(可学习),
     * 不再区分首课与其余课时。
     * </p>
     */
    @Override
    public List<UnitProgressDto> getUnitProgress(Integer unitId, Integer userId) {
        // 未指定用户时使用默认用户,便于匿名/调试场景
        Integer uid = userId != null ? userId : DEFAULT_USER_ID;

        // 查询单元下所有课时(按 sortOrder 升序),作为返回顺序的基准
        List<Lesson> unitLessons = lessonRepository.findByUnitIdOrderBySortOrderAsc(unitId);
        if (unitLessons.isEmpty()) {
            return List.of();
        }

        // 一次性查询该用户在该单元下所有有进度记录的课时,避免 N+1
        List<UserProgress> progressList = userProgressRepository
                .findByUserIdAndLessonUnitId(uid, unitId);
        // 构建 lessonId → UserProgress 映射,便于 O(1) 查找
        Map<Integer, UserProgress> progressMap = progressList.stream()
                .collect(Collectors.toMap(UserProgress::getLessonId, Function.identity()));

        // 遍历所有课时,有记录用记录状态,无记录统一返回 IN_PROGRESS(已取消锁定机制)
        return unitLessons.stream()
                .map(lesson -> {
                    UserProgress up = progressMap.get(lesson.getId());
                    if (up != null) {
                        return new UnitProgressDto(lesson.getId(),
                                up.getStatus().name(), up.getStars(), up.getScore());
                    }
                    // 已取消锁定机制:无记录统一返回 IN_PROGRESS(可学习)
                    return new UnitProgressDto(lesson.getId(),
                            ProgressStatus.IN_PROGRESS.name(), 0, 0);
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LearnedLessonDto> getLearnedLessons(Integer userId) {
        // 未指定用户时使用默认用户,便于匿名/调试场景
        Integer uid = userId != null ? userId : DEFAULT_USER_ID;
        return userProgressRepository.findLearnedLessonsByUserId(uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LearnedStatsDto getLearnedStats(Integer userId) {
        // 未指定用户时使用默认用户,便于匿名/调试场景
        Integer uid = userId != null ? userId : DEFAULT_USER_ID;

        // 汇总数据:已学总数、累计星星、平均分数
        Long totalLearned = userProgressRepository.countCompletedByUserId(uid);
        Integer totalStars = userProgressRepository.sumStarsByUserId(uid);
        Double avgScore = userProgressRepository.avgScoreByUserId(uid);
        // 平均分取整
        Integer averageScore = avgScore != null ? avgScore.intValue() : 0;

        // 按学科与课时类型分组统计:从已学课时列表中手动 groupBy
        List<LearnedLessonDto> lessons = userProgressRepository.findLearnedLessonsByUserId(uid);

        // 按学科分组:(subjectId, subjectName) → count
        Map<Integer, List<LearnedLessonDto>> bySubjectMap = lessons.stream()
                .collect(Collectors.groupingBy(LearnedLessonDto::subjectId));
        List<SubjectCount> bySubject = bySubjectMap.entrySet().stream()
                .map(e -> {
                    // 取该学科下第一条记录的 subjectName(同一学科名称相同)
                    String subjectName = e.getValue().get(0).subjectName();
                    return new SubjectCount(e.getKey(), subjectName, (long) e.getValue().size());
                })
                .toList();

        // 按课时类型分组:lessonType.name() → count
        // 使用 .name() 将 LessonType 枚举转为字符串,便于前端展示
        Map<String, Long> byTypeMap = lessons.stream()
                .collect(Collectors.groupingBy(dto -> dto.lessonType().name(), Collectors.counting()));
        List<TypeCount> byType = byTypeMap.entrySet().stream()
                .map(e -> new TypeCount(e.getKey(), e.getValue()))
                .toList();

        return new LearnedStatsDto(totalLearned, totalStars, averageScore, bySubject, byType);
    }
}
