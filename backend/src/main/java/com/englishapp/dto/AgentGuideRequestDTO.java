package com.englishapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 课程向导请求。
 *
 * @param text     孩子说的话（识别结果或打字），1–100 字
 * @param lessonId 当前所在课程，可空；阶段 1 只用于排除当前课
 */
public record AgentGuideRequestDTO(@NotBlank @Size(max = 100) String text, Integer lessonId) { }
