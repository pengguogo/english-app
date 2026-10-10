package com.englishapp.domain;

import java.time.LocalDate;

/** 按字保存，课时完成和跟读评分不参与识字状态。 */
public record CharacterProgress(String word, int independentDays, LocalDate lastIndependentDate,
                                int wrongCount, int assistedCount, String lastOutcome, LocalDate dueDate) {
    public String status() {
        return independentDays >= 3 ? "RECOGNIZED" : "REVIEWING";
    }
}
