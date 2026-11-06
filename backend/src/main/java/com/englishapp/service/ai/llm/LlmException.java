package com.englishapp.service.ai.llm;

/** 模型调用失败（超时、HTTP 错误、返回无法解析）。message 不含孩子原话和模型原文。 */
public class LlmException extends RuntimeException {
    public LlmException(String message) { super(message); }
    public LlmException(String message, Throwable cause) { super(message, cause); }
}
