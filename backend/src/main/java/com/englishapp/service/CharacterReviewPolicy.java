package com.englishapp.service;

import com.englishapp.domain.CharacterProgress;
import com.englishapp.dto.CharacterAttemptRequestDTO.Outcome;
import java.time.LocalDate;

/** 同一天重复答对不增加认字天数，提示或错误后重新巩固。 */
final class CharacterReviewPolicy {
    private CharacterReviewPolicy() {}
    static CharacterProgress apply(CharacterProgress previous, String word, Outcome outcome, LocalDate today) {
        int days = previous == null ? 0 : previous.independentDays();
        LocalDate last = previous == null ? null : previous.lastIndependentDate();
        int wrong = previous == null ? 0 : previous.wrongCount();
        int assisted = previous == null ? 0 : previous.assistedCount();
        if (outcome == Outcome.INDEPENDENT) {
            if (!today.equals(last)) days++;
            last = today;
        } else {
            days = 0;
            if (outcome == Outcome.WRONG) wrong++;
            else assisted++;
        }
        int interval = outcome != Outcome.INDEPENDENT ? 1 : days >= 3 ? 7 : days == 2 ? 3 : 1;
        return new CharacterProgress(word, days, last, wrong, assisted, outcome.name(), today.plusDays(interval));
    }
}
