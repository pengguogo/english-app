package com.englishapp.dto;

import java.time.LocalDate;

public record CharacterProgressResponseDTO(CharacterItemResponseDTO item, String status,
        int independentDays, int wrongCount, int assistedCount, String lastOutcome, LocalDate dueDate) {}
