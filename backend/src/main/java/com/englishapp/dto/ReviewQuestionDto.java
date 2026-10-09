package com.englishapp.dto;

import java.util.List;

public record ReviewQuestionDto(int index, String prompt, String image, List<ReviewOptionDto> options) {}
