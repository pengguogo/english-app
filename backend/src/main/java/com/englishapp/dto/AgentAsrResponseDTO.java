package com.englishapp.dto;

/** 语音识别结果；text 已去标点，非空；turnToken 供 /agent/chat 免重复扣额度（60s 内有效） */
public record AgentAsrResponseDTO(String text, String provider, long latencyMs, String turnToken) { }
