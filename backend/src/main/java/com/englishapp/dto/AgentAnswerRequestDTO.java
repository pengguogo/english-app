package com.englishapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** /agent/answer 请求：quizToken 来自 /agent/chat 的 quiz，optionId 为 "a"/"b"/"c" */
public record AgentAnswerRequestDTO(@NotBlank @Size(max = 512) String quizToken,
                                    @NotBlank @Size(max = 4) String optionId) { }
