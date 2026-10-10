package com.englishapp.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "learning_attempt")
public class LearningAttempt {
    @Id
    private String id;
    private Integer userId;
    private Integer lessonId;
    private Integer questionIndex;
    private java.time.LocalDate studyDate;
    private Boolean firstCorrect;
    private Boolean assistedCorrect;
    public String getId() { return id; }
    public void setId(String value) { id = value; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer value) { userId = value; }
    public Integer getLessonId() { return lessonId; }
    public void setLessonId(Integer value) { lessonId = value; }
    public Integer getQuestionIndex() { return questionIndex; }
    public void setQuestionIndex(Integer value) { questionIndex = value; }
    public java.time.LocalDate getStudyDate() { return studyDate; }
    public void setStudyDate(java.time.LocalDate value) { studyDate = value; }
    public Boolean getFirstCorrect() { return firstCorrect; }
    public void setFirstCorrect(Boolean value) { firstCorrect = value; }
    public Boolean getAssistedCorrect() { return assistedCorrect; }
    public void setAssistedCorrect(Boolean value) { assistedCorrect = value; }
}
