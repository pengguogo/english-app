package com.englishapp.voice;

import com.englishapp.config.VoiceProperties;
import com.englishapp.voice.dto.ScoreResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class BaiduScoreTest {
    private ScoreResponse evaluate(String response) {
        BaiduVoiceService service = new BaiduVoiceService(new VoiceProperties());
        ReflectionTestUtils.setField(service, "cachedToken", "test-token");
        ReflectionTestUtils.setField(service, "tokenExpireAt", System.currentTimeMillis() + 3600000);
        RestTemplate client = (RestTemplate) ReflectionTestUtils.getField(service, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(client).build();
        server.expect(requestTo("https://vop.baidu.com/server_api"))
                .andRespond(withSuccess(response, APPLICATION_JSON));
        ScoreResponse result = service.scorePronunciation(new byte[]{1}, "apple");
        server.verify();
        return result;
    }

    @Test
    void should_不计成绩_当_识别服务失败或无结果() {
        assertThat(evaluate("{\"err_no\":3301}").getScore()).isNull();
        assertThat(evaluate("{\"err_no\":0,\"result\":[]}").getScore()).isNull();
    }

    @Test
    void should_保留有效分数_当_识别成功() {
        assertThat(evaluate("{\"err_no\":0,\"result\":[\"apple\"]}").getScore()).isEqualTo(100);
        assertThat(evaluate("{\"err_no\":0,\"result\":[\"pear\"]}").getScore()).isNotNull();
    }

    @Test
    void should_不计成绩_当_供应商响应损坏() {
        assertThat(evaluate("not-json").getScore()).isNull();
    }
}
