package com.englishapp.dto;

public record LessonDto(
    Integer id,
    Integer unitId,
    String name,
    String type,
    String content,
    Integer sortOrder,
    Integer starReward
) {}
