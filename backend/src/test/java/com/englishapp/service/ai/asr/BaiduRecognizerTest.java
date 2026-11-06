package com.englishapp.service.ai.asr;

import com.englishapp.voice.BaiduVoiceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BaiduRecognizerTest {

    private AsrTestSupport.Stub stub;
    private BaiduVoiceService voice;
    private BaiduRecognizer recognizer;

    @BeforeEach
    void setUp() throws Exception {
        stub = new AsrTestSupport.Stub();
        voice = mock(BaiduVoiceService.class);
        when(voice.isAvailable()).thenReturn(true);
        when(voice.getToken()).thenReturn("tok-1");
        recognizer = new BaiduRecognizer(AsrTestSupport.props(stub.url(), 500), voice);
    }

    @AfterEach
    void tearDown() {
        stub.close();
    }

    @Test
    void should_返回去标点文本_当_识别成功() throws Exception {
        stub.response = "{\"err_no\":0,\"err_msg\":\"success.\",\"result\":[\"  我想学动物。 \"]}";

        AsrResult r = recognizer.recognize(AsrTestSupport.validWav());

        assertThat(r.text()).isEqualTo("我想学动物");
        assertThat(r.provider()).isEqualTo("baidu");
        assertThat(r.latencyMs()).isGreaterThanOrEqualTo(0);
        JsonNode req = new ObjectMapper().readTree(stub.lastBody.get());
        assertThat(req.get("dev_pid").asInt()).isEqualTo(1537);
        assertThat(req.get("rate").asInt()).isEqualTo(16000);
        assertThat(req.get("token").asText()).isEqualTo("tok-1");
        assertThat(req.get("len").asInt()).isEqualTo(AsrTestSupport.validWav().length);
    }

    @Test
    void should_抛UNAVAILABLE_当_err_no非0() {
        stub.response = "{\"err_no\":3301,\"err_msg\":\"speech quality error.\"}";
        assertUnavailable();
    }

    @Test
    void should_抛UNAVAILABLE_当_结果只有标点() {
        stub.response = "{\"err_no\":0,\"result\":[\"。\"]}";
        assertUnavailable();
    }

    @Test
    void should_抛UNAVAILABLE_当_HTTP500() {
        stub.status = 500;
        stub.response = "oops";
        assertUnavailable();
    }

    @Test
    void should_抛UNAVAILABLE_当_读取超时() {
        stub.delayMs = 1500;
        long start = System.currentTimeMillis();
        assertUnavailable();
        assertThat(System.currentTimeMillis() - start).isLessThan(1400);
    }

    @Test
    void should_抛UNAVAILABLE且不请求_当_未配置密钥() {
        when(voice.isAvailable()).thenReturn(false);
        assertUnavailable();
        assertThat(stub.lastBody.get()).isNull();
        verify(voice, never()).getToken();
    }

    @Test
    void should_抛UNAVAILABLE_当_token获取失败() {
        when(voice.getToken()).thenThrow(new RuntimeException("语音服务认证失败"));
        assertUnavailable();
    }

    @Test
    void should_抛INVALID_AUDIO且不请求_当_WAV不合格() {
        assertThatThrownBy(() -> recognizer.recognize(AsrTestSupport.wav(44100, 1, 16, 1000)))
                .isInstanceOf(AsrException.class)
                .extracting(e -> ((AsrException) e).getReason())
                .isEqualTo(AsrException.Reason.INVALID_AUDIO);
        assertThat(stub.lastBody.get()).isNull();
    }

    @Test
    void should_预校验只抛INVALID_AUDIO且不出网_当_WAV超时长() {
        recognizer.validate(AsrTestSupport.validWav());
        assertThatThrownBy(() -> recognizer.validate(AsrTestSupport.wav(16000, 1, 16, 15100)))
                .isInstanceOf(AsrException.class)
                .extracting(e -> ((AsrException) e).getReason())
                .isEqualTo(AsrException.Reason.INVALID_AUDIO);
        assertThat(stub.lastBody.get()).isNull();
        verify(voice, never()).getToken();
    }

    private void assertUnavailable() {
        assertThatThrownBy(() -> recognizer.recognize(AsrTestSupport.validWav()))
                .isInstanceOf(AsrException.class)
                .extracting(e -> ((AsrException) e).getReason())
                .isEqualTo(AsrException.Reason.UNAVAILABLE);
    }
}
