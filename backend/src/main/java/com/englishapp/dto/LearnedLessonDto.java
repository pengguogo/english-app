package com.englishapp.dto;

import com.englishapp.domain.enums.LessonType;
import java.time.LocalDateTime;

/**
 * 已学课时 DTO
 * <p>
 * 用于"我学过的"列表接口,返回用户已完成的课时信息,包含从课时到学科
 * 的完整层级路径(课时→单元→主题→学科),以及该课时的星星数、分数与完成时间。
 * </p>
 *
 * @param lessonId    课时 ID
 * @param lessonName  课时名称
 * @param lessonType  课时类型(如 WORD/SENTENCE/QUIZ 等)
 * @param unitId      单元 ID
 * @param unitName    单元名称
 * @param themeId     主题 ID
 * @param themeName   主题名称
 * @param subjectId   学科 ID
 * @param subjectName 学科名称
 * @param stars       获得星星数(历史最高)
 * @param score       获得分数(历史最高)
 * @param completedAt 完成时间
 * @author englishapp
 * @since 1.2.0
 */
public record LearnedLessonDto(
        Integer lessonId,
        String lessonName,
        LessonType lessonType,
        Integer unitId,
        String unitName,
        Integer themeId,
        String themeName,
        Integer subjectId,
        String subjectName,
        Integer stars,
        Integer score,
        LocalDateTime completedAt
) {}
