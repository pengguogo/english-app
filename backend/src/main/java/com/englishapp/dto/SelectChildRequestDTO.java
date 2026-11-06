package com.englishapp.dto;

import jakarta.validation.constraints.NotNull;

public record SelectChildRequestDTO(@NotNull Integer childId) { }
