package com.englishapp.dto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ChildProfileRequestDTO(
        @Size(max = 50) String nickname,
        @PastOrPresent LocalDate birthDate,
        String sex) { }
