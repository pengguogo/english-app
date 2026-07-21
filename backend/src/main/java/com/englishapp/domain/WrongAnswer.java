package com.englishapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 错题实体
 * <p>
 * 记录用户在课时练习中答错的题目。同一用户、同一课时、同一题号的错题
 * 只保留一条记录,通过 wrong_count 累加错误次数。用户可在错题回顾中
 * 将其标记为"已掌握"(is_resolved = true)。
 * </p>
 *
 * @author englishapp
 * @since 1.2.0
 */
@Entity
@Table(name = "wrong_answer")
public class WrongAnswer {

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 用户 ID(外键,关联 app_user 表) */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 课时 ID(外键,关联 lesson 表) */
    @Column(name = "lesson_id", nullable = false)
    private Integer lessonId;

    /** 课时名称(冗余字段,便于错题列表直接展示,避免额外关联查询) */
    @Column(name = "lesson_name", length = 100)
    private String lessonName;

    /** 题目在课时中的序号(从 0 开始) */
    @Column(name = "question_index", nullable = false)
    private Integer questionIndex;

    /** 题目类型(如 WORD/SENTENCE/QUIZ 等,对应 LessonType) */
    @Column(name = "question_type", nullable = false, length = 20)
    private String questionType;

    /** 题目快照(JSON 文本,保存答错时的题目完整内容,避免后续课时内容变更导致错题失真) */
    @Column(name = "question_snapshot", columnDefinition = "TEXT")
    private String questionSnapshot;

    /** 用户当时的错误答案 */
    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    /** 正确答案 */
    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    /** 答错次数(同一题重复答错时累加,默认 1) */
    @Column(name = "wrong_count")
    private Integer wrongCount = 1;

    /** 是否已掌握(false 表示未掌握,true 表示用户在错题回顾中已确认掌握) */
    @Column(name = "is_resolved")
    private Boolean isResolved = false;

    /** 最近一次答错时间 */
    @Column(name = "last_wrong_at")
    private LocalDateTime lastWrongAt;

    /** 创建时间 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }

    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }

    public LocalDateTime getLastWrongAt() {
        return lastWrongAt;
    }

    public void setLastWrongAt(LocalDateTime lastWrongAt) {
        this.lastWrongAt = lastWrongAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
