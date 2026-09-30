package com.englishapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GrowthMeasurementResponseDTO(Integer id, LocalDate measuredAt,
        BigDecimal heightCm, BigDecimal weightKg, String note) { }
