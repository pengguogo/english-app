package com.englishapp.repository;

import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
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
}
