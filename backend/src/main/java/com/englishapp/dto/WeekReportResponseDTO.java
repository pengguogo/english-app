package com.englishapp.dto;

import java.time.LocalDate;
import java.util.List;

/** 答题率只基于本次功能上线后采集的选择题和计算题样本。 */
public record WeekReportResponseDTO(LocalDate from, LocalDate to, int totalSeconds, int activeDays,
        int passedLessons, int attempts, int firstCorrect, int assistedCorrect, List<Day> days) {
    public record Day(LocalDate date, int seconds, int passedLessons) {}
}
