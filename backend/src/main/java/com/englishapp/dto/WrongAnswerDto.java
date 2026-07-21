package com.englishapp.dto;

import java.time.LocalDateTime;

/**
 * 错题响应 DTO
 * <p>
 * 用于错题列表接口返回单条错题信息,包含题目内容、用户答案、正确答案、
 * 错误次数与掌握状态等。
 * </p>
 *
 * @param id               错题记录 ID
 * @param lessonId         课时 ID
 * @param lessonName       课时名称
 * @param questionIndex    题目序号
 * @param questionType     题目类型
 * @param questionSnapshot 题目快照(JSON 文本)
 * @param userAnswer       用户错误答案
 * @param correctAnswer    正确答案
 * @param wrongCount       答错次数
 * @param isResolved       是否已掌握
 * @param lastWrongAt      最近答错时间
 * @param createdAt        创建时间
 * @author englishapp
 * @since 1.2.0
 */
public record WrongAnswerDto(
        Integer id,
        Integer lessonId,
        String lessonName,
        Integer questionIndex,
        String questionType,
        String questionSnapshot,
        String userAnswer,
        String correctAnswer,
        Integer wrongCount,
        Boolean isResolved,
        LocalDateTime lastWrongAt,
        LocalDateTime createdAt
) {}
