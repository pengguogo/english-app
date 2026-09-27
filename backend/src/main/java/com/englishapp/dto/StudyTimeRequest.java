package com.englishapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StudyTimeRequest {
    @NotNull
    @Min(1)
    @Max(30)
    private Integer seconds;

    public Integer getSeconds() { return seconds; }
    public void setSeconds(Integer seconds) { this.seconds = seconds; }
}
