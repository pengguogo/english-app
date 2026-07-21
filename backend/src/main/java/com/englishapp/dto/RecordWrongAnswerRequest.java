package com.englishapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 记录错题请求体
 * <p>
 * 前端在用户答错题目时提交此请求,后端据此创建或更新错题记录。
 * 由于需要参数校验(@Valid),使用 class 而非 record。
 * </p>
 *
 * @author englishapp
 * @since 1.2.0
 */
public class RecordWrongAnswerRequest {

    /** 课时 ID */
    @NotNull
    private Integer lessonId;

    /** 课时名称(冗余字段,便于错题列表展示) */
    private String lessonName;

    /** 题目在课时中的序号(从 0 开始) */
    @NotNull
    @Min(0)
    private Integer questionIndex;

    /** 题目类型(如 WORD/SENTENCE/QUIZ 等) */
    @NotBlank
    private String questionType;

    /** 题目快照(JSON 文本,保存题目完整内容) */
    private String questionSnapshot;

    /** 用户当时的错误答案 */
    @NotBlank
    private String userAnswer;

    /** 正确答案 */
    @NotBlank
    private String correctAnswer;

    public Integer getLessonId() {
        return lessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionSnapshot() {
        return questionSnapshot;
    }

    public void setQuestionSnapshot(String questionSnapshot) {
        this.questionSnapshot = questionSnapshot;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
