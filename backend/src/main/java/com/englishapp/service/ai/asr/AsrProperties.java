package com.englishapp.service.ai.asr;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 语音识别配置，前缀 app.agent.asr。百度密钥沿用 voice.baidu.*。 */
@Component
@ConfigurationProperties(prefix = "app.agent.asr")
public class AsrProperties {
    /** baidu | dashscope */
    private String provider = "baidu";
    private int connectTimeoutMs = 3000;
    private int readTimeoutMs = 5000;
    private int maxDurationMs = 15000;
    private final Baidu baidu = new Baidu();
    private final Dashscope dashscope = new Dashscope();

    public static class Baidu {
        private String endpoint = "https://vop.baidu.com/server_api";
        /** 1537 = 普通话（有标点）输入法模型 */
        private int devPid = 1537;

        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getDevPid() { return devPid; }
        public void setDevPid(int devPid) { this.devPid = devPid; }
    }

    public static class Dashscope {
        private String apiKey;
        private String endpoint = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";
        private String model = "qwen3-asr-flash";

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }
    public int getReadTimeoutMs() { return readTimeoutMs; }
    public void setReadTimeoutMs(int readTimeoutMs) { this.readTimeoutMs = readTimeoutMs; }
    public int getMaxDurationMs() { return maxDurationMs; }
    public void setMaxDurationMs(int maxDurationMs) { this.maxDurationMs = maxDurationMs; }
    public Baidu getBaidu() { return baidu; }
    public Dashscope getDashscope() { return dashscope; }
}
