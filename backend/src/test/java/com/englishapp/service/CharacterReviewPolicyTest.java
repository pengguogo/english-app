package com.englishapp.service;

import com.englishapp.dto.CharacterAttemptRequestDTO.Outcome;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CharacterReviewPolicyTest {
    private final LocalDate day = LocalDate.of(2026, 10, 10);
    @Test
    void should_不累计天数_当_当天反复答对() {
        var first = CharacterReviewPolicy.apply(null, "山", Outcome.INDEPENDENT, day);
        var repeat = CharacterReviewPolicy.apply(first, "山", Outcome.INDEPENDENT, day);
        assertEquals(1, repeat.independentDays());
        assertEquals("REVIEWING", repeat.status());
        assertEquals(day.plusDays(1), repeat.dueDate());
    }
    @Test
    void should_跨三天认对后延长复习间隔_当_独立辨认() {
        var first = CharacterReviewPolicy.apply(null, "山", Outcome.INDEPENDENT, day);
        var second = CharacterReviewPolicy.apply(first, "山", Outcome.INDEPENDENT, day.plusDays(1));
        var third = CharacterReviewPolicy.apply(second, "山", Outcome.INDEPENDENT, day.plusDays(4));
        assertEquals("RECOGNIZED", third.status());
        assertEquals(day.plusDays(11), third.dueDate());
    }
    @Test
    void should_退回巩固且记录次数_当_认错或使用提示() {
        var first = CharacterReviewPolicy.apply(null, "山", Outcome.INDEPENDENT, day);
        var wrong = CharacterReviewPolicy.apply(first, "山", Outcome.WRONG, day);
        var retry = CharacterReviewPolicy.apply(wrong, "山", Outcome.INDEPENDENT, day);
        assertEquals(0, retry.independentDays());
        assertEquals(1, retry.wrongCount());
        var hint = CharacterReviewPolicy.apply(retry, "山", Outcome.ASSISTED, day.plusDays(1));
        assertEquals(1, hint.assistedCount());
        assertEquals(day.plusDays(2), hint.dueDate());
    }
}
