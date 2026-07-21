package com.englishapp.service;

import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.LearnedLessonDto;
import com.englishapp.dto.LearnedStatsDto;
import com.englishapp.dto.ProgressDto;
import com.englishapp.dto.UnitProgressDto;

import java.util.List;

/**
 * 进度业务接口
 * <p>
 * 定义课时进度查询与课时完成的核心业务能力,包括:
 * <ul>
 *     <li>查询某用户在某课时的进度(不存在时按规则返回默认状态)</li>
 *     <li>完成课时:记录最高分与最高星数,并解锁同单元内的下一课时</li>
 * </ul>
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
public interface ProgressService {

    /**
     * 获取某课时的用户进度
     * <p>
     * 若数据库中存在该用户在该课时的进度记录,则直接返回;若不存在,则按以下规则返回默认状态:
     * 当该课时是其所属单元中 sortOrder 最小的课时(首课)时返回 {@code IN_PROGRESS}(可学习),
     * 其余课时返回 {@code LOCKED}。
     * </p>
     * <p>
     * 注意:由于 {@link com.englishapp.domain.enums.ProgressStatus} 枚举不包含 {@code UNLOCKED},
     * 此处使用 {@code IN_PROGRESS} 表示"已解锁/可学习"语义。
     * </p>
     *
     * @param lessonId 课时 ID
     * @param userId   用户 ID(为 null 时由实现决定默认值)
     * @return 进度信息
     */
    ProgressDto getLessonProgress(Integer lessonId, Integer userId);

    /**
     * 完成课时:更新进度并解锁下一课时
     * <p>
     * 以"取最高分"策略更新该用户在该课时的进度(允许重复挑战),将状态置为 {@code COMPLETED},
     * 随后在同单元内查找 sortOrder 更大的下一课时并将其解锁(置为 {@code IN_PROGRESS})。
     * </p>
     *
     * @param lessonId 课时 ID
     * @param request  完成请求(含本次星星与分数)
     * @param userId   用户 ID(为 null 时由实现决定默认值)
     * @return 下一课解锁信息(若已是最后一课,nextLessonId 为 null,unlocked 为 false)
     */
    CompleteResponse completeLesson(Integer lessonId, CompleteRequest request, Integer userId);

    /**
     * 批量获取某单元下所有课时的进度
     * <p>
     * 供前端课时列表页一次性渲染整单元的锁定/可学/已完成状态,避免逐课 N+1 查询。
     * 对于无进度记录的课时,按以下规则返回默认状态:
     * <ul>
     *     <li>该单元 sortOrder 最小的课时(首课)返回 {@code IN_PROGRESS}(可学习)</li>
     *     <li>其余课时返回 {@code LOCKED}</li>
     * </ul>
     * </p>
     *
     * @param unitId 单元 ID
     * @param userId 用户 ID(为 null 时由实现决定默认值)
     * @return 该单元所有课时的进度列表(按课时 sortOrder 升序排列)
     */
    List<UnitProgressDto> getUnitProgress(Integer unitId, Integer userId);

    /**
     * 获取用户已学过的课时列表
     * <p>
     * 返回用户所有已完成(COMPLETED)的课时,包含课时→单元→主题→学科的完整层级信息,
     * 以及每个课时的星星数、分数与完成时间,按完成时间降序排列。
     * </p>
     *
     * @param userId 用户 ID(为 null 时使用默认用户)
     * @return 已学课时 DTO 列表(按完成时间降序)
     */
    List<LearnedLessonDto> getLearnedLessons(Integer userId);

    /**
     * 获取用户已学课时的统计信息
     * <p>
     * 返回已学课时总数、累计星星数、平均分数,以及按学科和课时类型的分类统计。
     * </p>
     *
     * @param userId 用户 ID(为 null 时使用默认用户)
     * @return 已学课时统计 DTO
     */
    LearnedStatsDto getLearnedStats(Integer userId);
}
