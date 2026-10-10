package com.englishapp.repository;

import com.englishapp.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程仓库
 * <p>
 * 提供对 {@link Lesson} 实体的持久化访问,包含按单元查询课程列表与课程计数等能力。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    @Query("SELECT l FROM Lesson l, Unit u, Theme t, Subject s WHERE l.unitId = u.id AND u.themeId = t.id "
            + "AND t.subjectId = s.id AND s.code = 'ENGLISH' AND l.type IN "
            + "(com.englishapp.domain.enums.LessonType.WORD, com.englishapp.domain.enums.LessonType.SENTENCE, "
            + "com.englishapp.domain.enums.LessonType.PHONICS, com.englishapp.domain.enums.LessonType.DIALOGUE, "
            + "com.englishapp.domain.enums.LessonType.READING, com.englishapp.domain.enums.LessonType.QUIZ) "
            + "AND NOT EXISTS (SELECT p.id FROM UserProgress p WHERE p.userId = :userId AND p.lessonId = l.id "
            + "AND p.status = com.englishapp.domain.enums.ProgressStatus.COMPLETED) "
            + "ORDER BY t.sortOrder, u.sortOrder, l.sortOrder, l.id")
    List<Lesson> findNextUnfinishedEnglishLesson(@Param("userId") Integer userId, org.springframework.data.domain.Pageable pageable);


    /**
     * 按单元 ID 查询课程列表(按排序序号升序)
     *
     * @param unitId 单元 ID
     * @return 课程列表
     */
    List<Lesson> findByUnitIdOrderBySortOrderAsc(Integer unitId);

    /** 按教材单元顺序读取指定主题的词语课。 */
    @Query("SELECT l FROM Lesson l, Unit u WHERE l.unitId = u.id AND u.themeId = :themeId "
            + "AND l.type = :type ORDER BY u.sortOrder, l.sortOrder, l.id")
    List<Lesson> findByThemeAndType(@Param("themeId") Integer themeId,
            @Param("type") com.englishapp.domain.enums.LessonType type);

    /**
     * 统计某单元下的课程总数
     * <p>
     * 通过 JPQL 显式查询,避免依赖派生方法命名约定,语义更清晰。
     * </p>
     *
     * @param unitId 单元 ID
     * @return 课程总数
     */
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.unitId = :unitId")
    Integer countByUnitId(@Param("unitId") Integer unitId);
}
