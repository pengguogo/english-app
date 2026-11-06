package com.englishapp.agent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.englishapp.security.IntegrationTestBase;
import com.englishapp.security.QuotaService;
import com.englishapp.service.AuthService;
import com.englishapp.service.ai.asr.AsrException;
import com.englishapp.service.ai.asr.AsrResult;
import com.englishapp.service.ai.asr.SpeechRecognizer;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockHttpSession;

/** /api/v1/agent/**：准入、额度、错误码；ASR 用替身，不出网 */
class AgentControllerTest extends IntegrationTestBase {

    @MockBean
    private SpeechRecognizer recognizer;
    @SpyBean
    private QuotaService quota;
    @Autowired
    private JdbcTemplate jdbc;

    private static final MockMultipartFile WAV =
            new MockMultipartFile("audio", "a.wav", "audio/wav", new byte[2048]);

    @BeforeEach
    void setUp() {
        reset(recognizer, quota);
    }

    private Actor consentedFamily() throws Exception {
        Actor a = newFamilyWithChild("小朋友");
        mockMvc.perform(post("/api/v1/auth/consent").session(a.session()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("version", AuthService.CONSENT_VERSION))))
                .andExpect(status().isOk());
        return a;
    }

    @Test
    void should_返回识别结果_当_已同意且额度允许() throws Exception {
        Actor a = consentedFamily();
        when(recognizer.recognize(any())).thenReturn(new AsrResult("我想看恐龙", "baidu", 321));
        mockMvc.perform(a.with(multipart("/api/v1/agent/asr").file(WAV)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.text").value("我想看恐龙"))
                .andExpect(jsonPath("$.data.provider").value("baidu"))
                .andExpect(jsonPath("$.data.latencyMs").value(321));
    }

    @Test
    void should_返回401_当_游客录音() throws Exception {
        mockMvc.perform(multipart("/api/v1/agent/asr").file(WAV).with(csrf()))
                .andExpect(status().isUnauthorized());
        verify(recognizer, never()).recognize(any());
    }

    @Test
    void should_返回409_当_未选孩子() throws Exception {
        Actor a = consentedFamily();
        MockHttpSession noChild = loginAs(a);
        mockMvc.perform(multipart("/api/v1/agent/asr").file(WAV).session(noChild).with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(4091));
    }

    @Test
    void should_返回403和4033_当_家庭未同意() throws Exception {
        Actor a = newFamilyWithChild("小朋友");
        mockMvc.perform(a.with(multipart("/api/v1/agent/asr").file(WAV)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4033))
                .andExpect(jsonPath("$.message").value("请家长先开启学习伙伴"));
        verify(quota, never()).tryConsumeFamily(anyLong());
        verify(recognizer, never()).recognize(any());
    }

    @Test
    void should_返回429且不调ASR_当_额度被拒() throws Exception {
        Actor a = consentedFamily();
        Map<QuotaService.Decision, Integer> codes = Map.of(
                QuotaService.Decision.MINUTE_LIMIT, 4291,
                QuotaService.Decision.DAILY_LIMIT, 4292,
                QuotaService.Decision.TRIAL_BUDGET_EXHAUSTED, 4293);
        for (var e : codes.entrySet()) {
            org.mockito.Mockito.doReturn(e.getKey()).when(quota).tryConsumeFamily(a.familyId());
            mockMvc.perform(a.with(multipart("/api/v1/agent/asr").file(WAV)))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(jsonPath("$.code").value(e.getValue()));
        }
        verify(recognizer, never()).recognize(any());
    }

    @Test
    void should_返回400和4001_当_音频超过512KB() throws Exception {
        Actor a = consentedFamily();
        MockMultipartFile big = new MockMultipartFile("audio", "a.wav", "audio/wav", new byte[512 * 1024 + 1]);
        mockMvc.perform(a.with(multipart("/api/v1/agent/asr").file(big)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001));
        verify(quota, never()).tryConsumeFamily(anyLong());
        verify(recognizer, never()).recognize(any());
    }

    @Test
    void should_返回400和4001_当_不是multipart或缺audio() throws Exception {
        Actor a = consentedFamily();
        mockMvc.perform(a.with(post("/api/v1/agent/asr").contentType(MediaType.APPLICATION_JSON).content("{}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001));
        MockMultipartFile wrongField = new MockMultipartFile("file", "a.wav", "audio/wav", new byte[10]);
        mockMvc.perform(a.with(multipart("/api/v1/agent/asr").file(wrongField)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001));
    }

    @Test
    void should_返回400且不扣额度_当_WAV头不合法() throws Exception {
        Actor a = consentedFamily();
        org.mockito.Mockito.doThrow(new AsrException(AsrException.Reason.INVALID_AUDIO, "不是 RIFF/WAVE 格式"))
                .when(recognizer).validate(any());
        Integer before = jdbc.queryForObject("SELECT COUNT(*) FROM agent_quota_usage", Integer.class);
        mockMvc.perform(a.with(multipart("/api/v1/agent/asr").file(WAV)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001));
        Integer after = jdbc.queryForObject("SELECT COUNT(*) FROM agent_quota_usage", Integer.class);
        org.assertj.core.api.Assertions.assertThat(after).isEqualTo(before);
        verify(quota, never()).tryConsumeFamily(anyLong());
        verify(recognizer, never()).recognize(any());
    }

    @Test
    void should_扣一次额度_当_WAV合法且识别成功() throws Exception {
        Actor a = consentedFamily();
        when(recognizer.recognize(any())).thenReturn(new AsrResult("恐龙", "baidu", 100));
        Integer before = jdbc.queryForObject("SELECT COUNT(*) FROM agent_quota_usage", Integer.class);
        mockMvc.perform(a.with(multipart("/api/v1/agent/asr").file(WAV))).andExpect(status().isOk());
        Integer after = jdbc.queryForObject("SELECT COUNT(*) FROM agent_quota_usage", Integer.class);
        org.assertj.core.api.Assertions.assertThat(after).isEqualTo(before + 1);
    }

    @Test
    void should_返回400和4001_当_ASR判定音频不合格() throws Exception {
        Actor a = consentedFamily();
        when(recognizer.recognize(any())).thenThrow(new AsrException(AsrException.Reason.INVALID_AUDIO, "x"));
        mockMvc.perform(a.with(multipart("/api/v1/agent/asr").file(WAV)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001));
    }

    @Test
    void should_返回503和5031_当_识别失败() throws Exception {
        Actor a = consentedFamily();
        when(recognizer.recognize(any())).thenThrow(new AsrException(AsrException.Reason.UNAVAILABLE, "timeout"));
        mockMvc.perform(a.with(multipart("/api/v1/agent/asr").file(WAV)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(5031));
    }

    @Test
    void should_游客可检索且不计额度_当_调guide() throws Exception {
        mockMvc.perform(post("/api/v1/agent/guide").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("text", "我想看恐龙"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("RECOMMEND"))
                .andExpect(jsonPath("$.data.candidates[0].themeName").value("恐龙世界"))
                .andExpect(jsonPath("$.data.candidates[0].route").exists())
                .andExpect(jsonPath("$.data.topics").isArray());
        verify(quota, never()).tryConsumeGuest(any());
        verify(quota, never()).tryConsumeFamily(anyLong());
    }

    @Test
    void should_topics带route_当_guide返回NO_MATCH() throws Exception {
        mockMvc.perform(post("/api/v1/agent/guide").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("text", "你好呀"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("NO_MATCH"))
                .andExpect(jsonPath("$.data.topics.length()").value(4))
                .andExpect(jsonPath("$.data.topics[0].route").value("/theme/30?subjectId=4"))
                .andExpect(jsonPath("$.data.topics[3].route").exists());
    }

    @Test
    void should_返回403_当_guide缺CSRF() throws Exception {
        mockMvc.perform(post("/api/v1/agent/guide")
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("text", "恐龙"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));
    }

    @Test
    void should_返回400_当_text为空或超过100字() throws Exception {
        for (String text : new String[]{"", "   ", "恐".repeat(101)}) {
            mockMvc.perform(post("/api/v1/agent/guide").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("text", text))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }
    }

    @Test
    void should_游客可取热门主题_当_调topics() throws Exception {
        mockMvc.perform(get("/api/v1/agent/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(6))
                .andExpect(jsonPath("$.data[0].name").value("恐龙世界"))
                .andExpect(jsonPath("$.data[0].themeId").value(30))
                .andExpect(jsonPath("$.data[0].route").value("/theme/30?subjectId=4"));
    }
}
