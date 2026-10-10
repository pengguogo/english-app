package com.englishapp.dto;

import jakarta.validation.constraints.*;

public record LearningAttemptRequestDTO(
        @NotNull @Pattern(regexp = "[0-9a-fA-F-]{36}") String eventId,
        @NotNull Integer lessonId, @NotNull @Min(0) Integer questionIndex,
        @NotNull Boolean firstCorrect, @NotNull Boolean assistedCorrect) {}
