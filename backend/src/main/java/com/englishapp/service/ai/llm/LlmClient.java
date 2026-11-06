package com.englishapp.service.ai.llm;

/**
 * 对话模型：一次系统提示 + 一次用户输入 → 文本，不带历史。
 * 实现必须有超时；失败抛 {@link LlmException}。
 */
public interface LlmClient {

    /** 调用结果；content 为模型原文（调用方负责校验与审核），token 用于记账 */
    record Completion(String content, int promptTokens, int completionTokens, long latencyMs) { }

    boolean enabled();

    Completion complete(String system, String user);
}
