package com.englishapp.service.ai.asr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

/**
 * 阿里云百炼 qwen3-asr-flash 同步识别（DashScope 协议，multimodal-generation/generation）。
 * 音频以 base64 Data URI 直传，一次 HTTP 请求同步返回，无需先上传 OSS。
 */
@Component
@ConditionalOnProperty(prefix = "app.agent.asr", name = "provider", havingValue = "dashscope")
public class DashScopeRecognizer implements SpeechRecognizer {

    static final String PROVIDER = "dashscope";
    private static final Logger log = LoggerFactory.getLogger(DashScopeRecognizer.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AsrProperties props;
    private final AsrHttp http;

    public DashScopeRecognizer(AsrProperties props) {
        this.props = props;
        this.http = new AsrHttp(props);
    }

    @Override
    public String provider() {
        return PROVIDER;
    }

    @Override
    public void validate(byte[] wav) {
        WavAudio.validate(wav, props.getMaxDurationMs());
    }

    @Override
    public AsrResult recognize(byte[] wav) {
        validate(wav);
        AsrProperties.Dashscope cfg = props.getDashscope();
        if (cfg.getApiKey() == null || cfg.getApiKey().isBlank()) {
            throw new AsrException(AsrException.Reason.UNAVAILABLE, "DASHSCOPE_API_KEY 未配置");
        }

        long start = System.nanoTime();
        AsrHttp.Response resp = http.postJson(PROVIDER, cfg.getEndpoint(), buildBody(cfg.getModel(), wav),
                Map.of("Authorization", "Bearer " + cfg.getApiKey()));
        long latencyMs = (System.nanoTime() - start) / 1_000_000;

        JsonNode node = readTree(resp.body());
        if (resp.status() != 200) {
            // code/message 为供应商错误描述，不含识别正文
            log.warn("百炼 ASR 失败: status={}, code={}, message={}, latencyMs={}",
                    resp.status(), node.path("code").asText(), node.path("message").asText(), latencyMs);
            throw new AsrException(AsrException.Reason.UNAVAILABLE, "百炼 ASR HTTP " + resp.status());
        }
        JsonNode choice = node.path("output").path("choices").path(0);
        String raw = choice.path("message").path("content").path(0).path("text").asText("");
        String text = AsrText.clean(raw, PROVIDER);
        log.info("百炼 ASR 完成: latencyMs={}, chars={}", latencyMs, text.length());
        return new AsrResult(text, PROVIDER, latencyMs);
    }

    private static String buildBody(String model, byte[] wav) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("model", model);
        ArrayNode content = root.putObject("input").putArray("messages").addObject()
                .put("role", "user").putArray("content");
        content.addObject().put("audio", "data:audio/wav;base64," + Base64.getEncoder().encodeToString(wav));
        ObjectNode asr = root.putObject("parameters").putObject("asr_options");
        asr.put("language", "zh");
        asr.put("enable_itn", true);
        return root.toString();
    }

    private static JsonNode readTree(String body) {
        try {
            return MAPPER.readTree(body == null || body.isEmpty() ? "{}" : body);
        } catch (Exception e) {
            throw new AsrException(AsrException.Reason.UNAVAILABLE, "百炼 ASR 响应不是 JSON", e);
        }
    }
}
