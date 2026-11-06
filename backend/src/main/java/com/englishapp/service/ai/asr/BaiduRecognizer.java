package com.englishapp.service.ai.asr;

import com.englishapp.voice.BaiduVoiceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 百度短语音识别（标准版 REST，vop.baidu.com/server_api），普通话模型 dev_pid=1537。
 * token 复用 BaiduVoiceService 的缓存。
 */
@Component
@ConditionalOnProperty(prefix = "app.agent.asr", name = "provider", havingValue = "baidu", matchIfMissing = true)
public class BaiduRecognizer implements SpeechRecognizer {

    static final String PROVIDER = "baidu";
    private static final Logger log = LoggerFactory.getLogger(BaiduRecognizer.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AsrProperties props;
    private final BaiduVoiceService baiduVoiceService;
    private final AsrHttp http;

    public BaiduRecognizer(AsrProperties props, BaiduVoiceService baiduVoiceService) {
        this.props = props;
        this.baiduVoiceService = baiduVoiceService;
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
        if (!baiduVoiceService.isAvailable()) {
            throw new AsrException(AsrException.Reason.UNAVAILABLE, "百度语音密钥未配置");
        }
        String token;
        try {
            token = baiduVoiceService.getToken();
        } catch (RuntimeException e) {
            throw new AsrException(AsrException.Reason.UNAVAILABLE, "百度 token 获取失败", e);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("format", "wav");
        body.put("rate", WavAudio.SAMPLE_RATE);
        body.put("channel", 1);
        body.put("cuid", "english-app-agent");
        body.put("token", token);
        body.put("dev_pid", props.getBaidu().getDevPid());
        body.put("speech", Base64.getEncoder().encodeToString(wav));
        body.put("len", wav.length);

        long start = System.nanoTime();
        AsrHttp.Response resp = http.postJson(PROVIDER, props.getBaidu().getEndpoint(), toJson(body), Map.of());
        long latencyMs = (System.nanoTime() - start) / 1_000_000;

        if (resp.status() != 200) {
            log.warn("百度 ASR HTTP 异常: status={}, latencyMs={}", resp.status(), latencyMs);
            throw new AsrException(AsrException.Reason.UNAVAILABLE, "百度 ASR HTTP " + resp.status());
        }
        JsonNode node = readTree(resp.body());
        int errNo = node.path("err_no").asInt(-1);
        if (errNo != 0) {
            // err_msg 是供应商错误描述，不含识别正文
            log.warn("百度 ASR 失败: err_no={}, err_msg={}, latencyMs={}",
                    errNo, node.path("err_msg").asText(), latencyMs);
            throw new AsrException(AsrException.Reason.UNAVAILABLE, "百度 ASR err_no=" + errNo);
        }
        String text = AsrText.clean(node.path("result").path(0).asText(""), PROVIDER);
        log.info("百度 ASR 完成: latencyMs={}, chars={}", latencyMs, text.length());
        return new AsrResult(text, PROVIDER, latencyMs);
    }

    private static String toJson(Object body) {
        try {
            return MAPPER.writeValueAsString(body);
        } catch (Exception e) {
            throw new AsrException(AsrException.Reason.UNAVAILABLE, "百度 ASR 请求序列化失败", e);
        }
    }

    private static JsonNode readTree(String body) {
        try {
            return MAPPER.readTree(body == null ? "" : body);
        } catch (Exception e) {
            throw new AsrException(AsrException.Reason.UNAVAILABLE, "百度 ASR 响应不是 JSON", e);
        }
    }
}
