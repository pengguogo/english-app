package com.englishapp.service.ai.asr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DashScopeRecognizerTest {

    private static final String OK = "{\"output\":{\"choices\":[{\"finish_reason\":\"stop\",\"message\":{"
            + "\"role\":\"assistant\",\"content\":[{\"text\":\"我要听恐龙的故事！\"}],"
            + "\"annotations\":[{\"type\":\"audio_info\",\"language\":\"zh\"}]}}]},"
            + "\"usage\":{\"seconds\":1},\"request_id\":\"r1\"}";

    private AsrTestSupport.Stub stub;
    private AsrProperties props;
    private DashScopeRecognizer recognizer;

    @BeforeEach
    void setUp() throws Exception {
        stub = new AsrTestSupport.Stub();
        props = AsrTestSupport.props(stub.url(), 500);
        props.getDashscope().setApiKey("sk-test");
        recognizer = new DashScopeRecognizer(props);
    }

    @AfterEach
    void tearDown() {
        stub.close();
    }

    @Test
    void should_返回去标点文本_当_识别成功() throws Exception {
        stub.response = OK;
        byte[] wav = AsrTestSupport.validWav();

        AsrResult r = recognizer.recognize(wav);

        assertThat(r.text()).isEqualTo("我要听恐龙的故事");
        assertThat(r.provider()).isEqualTo("dashscope");
        assertThat(stub.lastAuth.get()).isEqualTo("Bearer sk-test");
        JsonNode req = new ObjectMapper().readTree(stub.lastBody.get());
        assertThat(req.get("model").asText()).isEqualTo("qwen3-asr-flash");
        assertThat(req.at("/input/messages/0/content/0/audio").asText())
                .isEqualTo("data:audio/wav;base64," + Base64.getEncoder().encodeToString(wav));
        assertThat(req.at("/parameters/asr_options/language").asText()).isEqualTo("zh");
    }

    @Test
    void should_抛UNAVAILABLE_当_HTTP401() {
        stub.status = 401;
        stub.response = "{\"code\":\"InvalidApiKey\",\"message\":\"Invalid API-key provided.\"}";
        assertUnavailable();
    }

    @Test
    void should_抛UNAVAILABLE_当_文本为空() {
        stub.response = "{\"output\":{\"choices\":[{\"message\":{\"content\":[{\"text\":\"\"}]}}]}}";
        assertUnavailable();
    }

    @Test
    void should_抛UNAVAILABLE_当_响应缺字段或非JSON() {
        stub.response = "{\"output\":{}}";
        assertUnavailable();
        stub.response = "<html>";
        assertUnavailable();
    }

    @Test
    void should_抛UNAVAILABLE_当_读取超时() {
        stub.delayMs = 1500;
        assertUnavailable();
    }

    @Test
    void should_抛UNAVAILABLE且不请求_当_未配置密钥() {
        props.getDashscope().setApiKey("");
        assertUnavailable();
        assertThat(stub.lastBody.get()).isNull();
    }

    @Test
    void should_抛INVALID_AUDIO_当_超过15秒() {
        assertThatThrownBy(() -> recognizer.recognize(AsrTestSupport.wav(16000, 1, 16, 16000)))
                .isInstanceOf(AsrException.class)
                .extracting(e -> ((AsrException) e).getReason())
                .isEqualTo(AsrException.Reason.INVALID_AUDIO);
    }

    private void assertUnavailable() {
        assertThatThrownBy(() -> recognizer.recognize(AsrTestSupport.validWav()))
                .isInstanceOf(AsrException.class)
                .extracting(e -> ((AsrException) e).getReason())
                .isEqualTo(AsrException.Reason.UNAVAILABLE);
    }
}
