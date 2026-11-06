package com.englishapp.dto;

/** 语音识别结果；text 已去标点，非空 */
public record AgentAsrResponseDTO(String text, String provider, long latencyMs) { }
