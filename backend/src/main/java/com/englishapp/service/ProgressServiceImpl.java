package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.ProgressDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.UserProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 进度业务实现
 * <p>
 * 基于 {@link UserProgressRepository} 与 {@link LessonRepository} 实现课时进度的查询与完成逻辑。
 * 核心设计要点:
 * <ul>
 *     <li>枚举适配:由于 {@link ProgressStatus} 不含 {@code UNLOCKED},统一使用
 *         {@code IN_PROGRESS} 表示"已解锁/可学习"语义</li>
 *     <li>取最高分:重复完成同一课时时,保留历史最高分与最高星数</li>
 *     <li>顺序解锁:仅在同单元内按 sortOrder 顺序解锁下一课时</li>
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
        // 无记录时:某单元下 sortOrder 最小的课时返回 IN_PROGRESS(可学习),其余 LOCKED
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("课时不存在: " + lessonId));
        List<Lesson> unitLessons = lessonRepository
                .findByUnitIdOrderBySortOrderAsc(lesson.getUnitId());
        boolean isFirst = unitLessons.stream()
                .mapToInt(Lesson::getSortOrder).min().orElse(0) == lesson.getSortOrder();
        // 注意:枚举无 UNLOCKED,用 IN_PROGRESS 表示"可学习/已解锁"
        String status = isFirst ? ProgressStatus.IN_PROGRESS.name() : ProgressStatus.LOCKED.name();
        return new ProgressDto(lessonId, status, 0, 0);
    }

    /**
     * {@inheritDoc}
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
        userProgressRepository.save(progress);

        // 解锁下一课时(同单元内 sortOrder 更大的下一课)
        Lesson current = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("课时不存在"));
        List<Lesson> unitLessons = lessonRepository
                .findByUnitIdOrderBySortOrderAsc(current.getUnitId());
        Lesson nextLesson = unitLessons.stream()
                .filter(l -> l.getSortOrder() > current.getSortOrder())
                .findFirst()
                .orElse(null);

        if (nextLesson != null) {
            unlockLesson(nextLesson.getId(), uid);
            return new CompleteResponse(nextLesson.getId(), true);
        }
        // 已是本单元最后一课,无下一课可解锁
        return new CompleteResponse(null, false);
    }

    /**
     * 解锁指定课时
     * <p>
     * 解锁语义:若记录不存在则创建为 {@code IN_PROGRESS};若已存在且为 {@code LOCKED} 则改为
     * {@code IN_PROGRESS};若已是 {@code IN_PROGRESS} 或 {@code COMPLETED} 则不重复处理。
     * </p>
     *
     * @param lessonId 课时 ID
     * @param userId   用户 ID
     */
    private void unlockLesson(Integer lessonId, Integer userId) {
        Optional<UserProgress> existing = userProgressRepository
                .findByUserIdAndLessonId(userId, lessonId);
        if (existing.isEmpty()) {
            UserProgress up = new UserProgress();
            up.setUserId(userId);
            up.setLessonId(lessonId);
            // 枚举无 UNLOCKED,用 IN_PROGRESS 表示"已解锁/可学习"
            up.setStatus(ProgressStatus.IN_PROGRESS);
            up.setStars(0);
            up.setScore(0);
            userProgressRepository.save(up);
        } else {
            UserProgress up = existing.get();
            if (up.getStatus() == ProgressStatus.LOCKED) {
                up.setStatus(ProgressStatus.IN_PROGRESS);
                userProgressRepository.save(up);
            }
        }
    }
}
