package com.englishapp.service;

import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.ProgressDto;

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
}
