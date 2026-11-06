package com.englishapp.service.ai.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.stereotype.Component;

/**
 * 百炼 OpenAI 兼容模式（chat/completions）。不重试：总预算只有 5s，失败直接降级。
 * 要求 JSON 输出（response_format=json_object），关闭思考（enable_thinking=false）。
 */
@Component
public class DashScopeChatClient implements LlmClient {

    private final LlmProperties props;
    private final ObjectMapper mapper;
    private final HttpClient http;

    public DashScopeChatClient(LlmProperties props, ObjectMapper mapper) {
        this.props = props;
        this.mapper = mapper;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(props.getConnectTimeoutMs())).build();
    }

    @Override
    public boolean enabled() {
        return props.enabled();
    }

    @Override
    public Completion complete(String system, String user) {
        if (!enabled()) throw new LlmException("未配置模型 Key");
        long start = System.nanoTime();
        HttpRequest req = HttpRequest.newBuilder(URI.create(props.getEndpoint()))
                .timeout(Duration.ofMillis(props.getTimeoutMs()))
                .header("Authorization", "Bearer " + props.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body(system, user), StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> resp;
        try {
            resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (HttpTimeoutException e) {
            throw new LlmException("模型调用超时", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlmException("模型调用被中断", e);
        } catch (IOException e) {
            throw new LlmException("模型网络错误", e);
        }
        long latency = (System.nanoTime() - start) / 1_000_000;
        if (resp.statusCode() != 200) {
            throw new LlmException("模型 HTTP " + resp.statusCode() + " " + errorCode(resp.body()));
        }
        try {
            JsonNode root = mapper.readTree(resp.body());
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            if (content.isBlank()) throw new LlmException("模型返回为空");
            JsonNode usage = root.path("usage");
            return new Completion(content, usage.path("prompt_tokens").asInt(0),
                    usage.path("completion_tokens").asInt(0), latency);
        } catch (IOException e) {
            throw new LlmException("模型返回不是 JSON", e);
        }
    }

    private String body(String system, String user) {
        ObjectNode b = mapper.createObjectNode();
        b.put("model", props.getModel());
        b.put("temperature", props.getTemperature());
        b.put("max_tokens", props.getMaxTokens());
        b.put("enable_thinking", false);
        b.putObject("response_format").put("type", "json_object");
        var messages = b.putArray("messages");
        messages.addObject().put("role", "system").put("content", system);
        messages.addObject().put("role", "user").put("content", user);
        return b.toString();
    }

    /** 只取错误码，不回显 body（可能含输入回显） */
    private String errorCode(String body) {
        try {
            JsonNode n = mapper.readTree(body);
            return n.path("error").path("code").asText(n.path("code").asText(""));
        } catch (Exception e) {
            return "";
        }
    }
}
