package com.englishapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ParentPasswordRequestDTO(@NotNull @Pattern(regexp = "[0-9]{6}") String password) {}
