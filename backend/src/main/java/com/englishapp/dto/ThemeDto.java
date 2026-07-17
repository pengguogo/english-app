package com.englishapp.dto;

public record ThemeDto(
    Integer id,
    String name,
    String iconUrl,
    Integer sortOrder,
    Boolean isLocked
) {}
