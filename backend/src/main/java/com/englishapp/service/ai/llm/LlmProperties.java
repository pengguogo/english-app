package com.englishapp.service.ai.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 对话模型配置（app.agent.llm.*）：百炼 OpenAI 兼容模式，Key 只在服务端 */
@Component
@ConfigurationProperties(prefix = "app.agent.llm")
public class LlmProperties {
    private String apiKey = "";
    private String endpoint = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private String model = "qwen-flash";
    private int connectTimeoutMs = 2000;
    /** 单次请求总超时：设计要求生成 + 审核 ≤4s，超过 6s 放弃 */
    private int timeoutMs = 5000;
    private int maxTokens = 400;
    private double temperature = 0.3;
    /** 单价：分 / 千 token（用于全站试用预算记账） */
    private double inputCentsPerKToken = 0.015;
    private double outputCentsPerKToken = 0.15;

    public boolean enabled() { return apiKey != null && !apiKey.isBlank(); }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(int v) { this.connectTimeoutMs = v; }
    public int getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(int v) { this.timeoutMs = v; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int v) { this.maxTokens = v; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double v) { this.temperature = v; }
    public double getInputCentsPerKToken() { return inputCentsPerKToken; }
    public void setInputCentsPerKToken(double v) { this.inputCentsPerKToken = v; }
    public double getOutputCentsPerKToken() { return outputCentsPerKToken; }
    public void setOutputCentsPerKToken(double v) { this.outputCentsPerKToken = v; }
}
