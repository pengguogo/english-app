package com.englishapp.dto;

import jakarta.validation.constraints.*;

public record CharacterAttemptRequestDTO(
        @NotBlank @Pattern(regexp = "[a-zA-Z0-9-]{1,80}") String eventId,
        @NotNull @Positive Integer lessonId,
        @NotNull @Min(0) Integer itemIndex,
        @NotNull Outcome outcome) {
    public enum Outcome { INDEPENDENT, ASSISTED, WRONG }
}
