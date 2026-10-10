package com.englishapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StudyTimeRequest {
    @NotNull
    private Integer lessonId;
    @jakarta.validation.constraints.Pattern(regexp = "[0-9a-fA-F-]{36}")
    private String eventId;
    public String getEventId() { return eventId; }
    public void setEventId(String value) { eventId = value; }
    @NotNull
    @Min(1)
    @Max(30)
    private Integer seconds;

    public Integer getSeconds() { return seconds; }
    public void setSeconds(Integer seconds) { this.seconds = seconds; }
    public Integer getLessonId() { return lessonId; }
    public void setLessonId(Integer lessonId) { this.lessonId = lessonId; }
}
