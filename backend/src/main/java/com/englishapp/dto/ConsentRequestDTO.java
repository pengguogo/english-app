package com.englishapp.dto;

import jakarta.validation.constraints.NotBlank;

public record ConsentRequestDTO(@NotBlank String version) { }
