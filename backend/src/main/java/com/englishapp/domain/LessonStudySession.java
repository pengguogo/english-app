package com.englishapp.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "lesson_study_session")
public class LessonStudySession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer lessonId;
    private LocalDate studyDate;
    private Integer pendingSeconds;
    private Boolean passed;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer value) { userId = value; }
    public Integer getLessonId() { return lessonId; }
    public void setLessonId(Integer value) { lessonId = value; }
    public LocalDate getStudyDate() { return studyDate; }
    public void setStudyDate(LocalDate value) { studyDate = value; }
    public Integer getPendingSeconds() { return pendingSeconds; }
    public void setPendingSeconds(Integer value) { pendingSeconds = value; }
    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean value) { passed = value; }
}
