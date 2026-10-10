package com.englishapp.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "study_time_event")
public class StudyTimeEvent {
    @Id
    private String id;
    private Integer userId;
    private Integer lessonId;
    private java.time.LocalDate studyDate;
    private Integer seconds;
    public String getId() { return id; }
    public void setId(String value) { id = value; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer value) { userId = value; }
    public Integer getLessonId() { return lessonId; }
    public void setLessonId(Integer value) { lessonId = value; }
    public java.time.LocalDate getStudyDate() { return studyDate; }
    public void setStudyDate(java.time.LocalDate value) { studyDate = value; }
    public Integer getSeconds() { return seconds; }
    public void setSeconds(Integer value) { seconds = value; }
}
