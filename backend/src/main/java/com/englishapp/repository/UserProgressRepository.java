package com.englishapp.repository;

import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.LearnedLessonDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户进度仓库
 * <p>
 * 提供对 {@link UserProgress} 实体的持久化访问,支持按用户、课程、状态等维度查询进度。
 * </p>
 * <p>
 * 注意:UserProgress 实体仅持有 {@code lessonId} 外键字段,未建立到 Lesson 的关联,
 * 因此涉及课程属性的查询(如按单元统计进度)需要通过显式 join 实现。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {

    /**
     * 按用户 ID 与课程 ID 查询进度记录
     *
     * @param userId  用户 ID
     * @param lessonId 课程 ID
     * @return 进度记录(可能为空)
     */
    Optional<UserProgress> findByUserIdAndLessonId(Integer userId, Integer lessonId);

    /**
     * 按用户 ID 查询其全部进度记录
     *
     * @param userId 用户 ID
     * @return 进度记录列表
     */
    List<UserProgress> findByUserId(Integer userId);

    /**
     * 统计某用户在某单元下指定状态的课程数量
     * <p>
     * 由于 UserProgress 未关联 Lesson 实体,此处通过隐式 join
     * (up.lessonId = l.id)关联 Lesson,再按 unitId 与 status 过滤。
     * </p>
     *
     * @param userId 用户 ID
     * @param unitId 单元 ID
     * @param status 进度状态
     * @return 符合条件的进度记录数
     */
    @Query("SELECT COUNT(up) FROM UserProgress up, Lesson l " +
            "WHERE up.userId = :userId AND up.lessonId = l.id " +
            "AND l.unitId = :unitId AND up.status = :status")
    Integer countByUserIdAndLessonUnitIdAndStatus(@Param("userId") Integer userId,
                                                  @Param("unitId") Integer unitId,
                                                  @Param("status") ProgressStatus status);

    /**
     * 查询某用户在某单元下所有课时的进度记录
     * <p>
     * 由于 UserProgress 未关联 Lesson 实体,此处通过隐式 join
     * (up.lessonId = l.id)关联 Lesson,再按 unitId 过滤。
     * 用于课时列表页批量获取进度,避免逐课 N+1 查询。
     * </p>
     *
     * @param userId 用户 ID
     * @param unitId 单元 ID
     * @return 该用户在该单元下所有有进度记录的课时进度列表
     */
    @Query("SELECT up FROM UserProgress up, Lesson l " +
            "WHERE up.userId = :userId AND up.lessonId = l.id " +
            "AND l.unitId = :unitId")
    List<UserProgress> findByUserIdAndLessonUnitId(@Param("userId") Integer userId,
                                                    @Param("unitId") Integer unitId);

    /**
     * 查询用户已完成的课时列表(含课时→单元→主题→学科完整层级信息)
     * <p>
     * 通过 JPQL 构造表达式直接映射为 {@link LearnedLessonDto},避免在 Service 层
     * 手动拼接。仅返回状态为 COMPLETED 的课时,按完成时间降序排列。
     * </p>
     *
     * @param userId 用户 ID
     * @return 已学课时 DTO 列表(按完成时间降序)
     */
    @Query("SELECT new com.englishapp.dto.LearnedLessonDto(" +
            "l.id, l.name, l.type, u.id, u.name, t.id, t.name, s.id, s.name, " +
            "up.stars, up.score, up.completedAt) " +
            "FROM UserProgress up, Lesson l, Unit u, Theme t, Subject s " +
            "WHERE up.userId = :userId AND up.status = com.englishapp.domain.enums.ProgressStatus.COMPLETED " +
            "AND up.lessonId = l.id AND l.unitId = u.id AND u.themeId = t.id AND t.subjectId = s.id " +
            "ORDER BY up.completedAt DESC")
    List<LearnedLessonDto> findLearnedLessonsByUserId(@Param("userId") Integer userId);

    /**
     * 统计用户已完成的课时总数
     *
     * @param userId 用户 ID
     * @return 已完成课时数
     */
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.userId = :userId AND up.status = com.englishapp.domain.enums.ProgressStatus.COMPLETED")
    Long countCompletedByUserId(@Param("userId") Integer userId);

    /**
     * 统计用户已完成课时的累计星星总数
     *
     * @param userId 用户 ID
     * @return 累计星星数(无记录时返回 0)
     */
    @Query("SELECT COALESCE(SUM(up.stars), 0) FROM UserProgress up WHERE up.userId = :userId AND up.status = com.englishapp.domain.enums.ProgressStatus.COMPLETED")
    Integer sumStarsByUserId(@Param("userId") Integer userId);

    /**
     * 计算用户已完成课时的平均分数
     *
     * @param userId 用户 ID
     * @return 平均分数(无记录时返回 0)
     */
    @Query("SELECT COALESCE(AVG(up.score), 0) FROM UserProgress up WHERE up.userId = :userId AND up.status = com.englishapp.domain.enums.ProgressStatus.COMPLETED")
    Double avgScoreByUserId(@Param("userId") Integer userId);
}
