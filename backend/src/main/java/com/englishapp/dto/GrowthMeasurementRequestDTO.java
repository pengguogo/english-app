package com.englishapp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GrowthMeasurementRequestDTO(
        @NotNull @PastOrPresent LocalDate measuredAt,
        @DecimalMin("20.0") @DecimalMax("250.0") BigDecimal heightCm,
        @DecimalMin("0.5") @DecimalMax("300.0") BigDecimal weightKg,
        @Size(max = 200) String note) { }
