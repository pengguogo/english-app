package com.englishapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/** /agent/answer 响应：答错时 hint 为提示语（可再选） */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AgentAnswerResponseDTO(boolean correct, String speech, String hint) { }
