package com.englishapp.dto;

public record UnitDto(
    Integer id,
    Integer themeId,
    String name,
    Integer sortOrder,
    Boolean isLocked
) {}
