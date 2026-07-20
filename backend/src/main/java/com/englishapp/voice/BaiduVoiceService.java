package com.englishapp.voice;

import com.englishapp.config.VoiceProperties;
import com.englishapp.voice.dto.ScoreResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度语音服务实现(TTS + 发音评测)
 * <p>
 * 通过百度智能云提供的开放接口实现文字转语音与发音评测能力。
 * 仅在配置项 {@code voice.provider=baidu} 时生效。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(name = "voice.provider", havingValue = "baidu")
public class BaiduVoiceService implements VoiceService {

    private static final Logger log = LoggerFactory.getLogger(BaiduVoiceService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** 百度 OAuth 授权令牌获取地址 */
    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    /** 百度语音合成接口地址 */
    private static final String TTS_URL = "https://tsn.baidu.com/text2audio";
    /** 百度发音评测接口地址 */
    private static final String SCORE_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/brain_assessment/score";

    private final VoiceProperties voiceProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    /** 缓存的 access_token,避免每次调用都重新获取 */
    private String cachedToken;
    /** token 过期时间戳(毫秒) */
    private long tokenExpireAt;

    /**
     * 构造百度语音服务
     *
     * @param voiceProperties 语音相关配置(含百度密钥)
     */
    public BaiduVoiceService(VoiceProperties voiceProperties) {
        this.voiceProperties = voiceProperties;
    }

    /**
     * 获取百度 access_token(带缓存,提前 60 秒过期)
     * <p>
     * 百度 access_token 默认有效期 30 天,缓存后避免重复请求。
     * 为防止临界点失效,提前 60 秒标记为过期。
     * </p>
     *
     * @return 有效的 access_token
     * @throws RuntimeException 当获取 token 失败时抛出
     */
    private String getToken() {
        // 命中缓存则直接返回
        if (cachedToken != null && System.currentTimeMillis() < tokenExpireAt) {
            return cachedToken;
        }
        String url = TOKEN_URL + "?grant_type=client_credentials"
                + "&client_id=" + voiceProperties.getApiKey()
                + "&client_secret=" + voiceProperties.getSecretKey();
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            JsonNode node = MAPPER.readTree(resp.getBody());
            cachedToken = node.get("access_token").asText();
            long expiresIn = node.get("expires_in").asLong();
            // 提前 60 秒过期,留出安全余量
            tokenExpireAt = System.currentTimeMillis() + (expiresIn - 60) * 1000L;
            return cachedToken;
        } catch (Exception e) {
            log.error("获取百度 token 失败", e);
            throw new RuntimeException("语音服务认证失败", e);
        }
    }

    /**
     * 文字转语音
     * <p>
     * 调用百度 TTS 接口合成中文语音,使用度小薇(女声)音色。
     * 调用失败时返回空字节数组,保证接口不抛异常。
     * </p>
     *
     * @param text 待合成的文本
     * @return 音频二进制数据;失败时返回空数组
     */
    @Override
    public byte[] textToSpeech(String text) {
        log.info("TTS 请求: {}", text);
        try {
            String token = getToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("tex", text);
            form.add("tok", token);
            form.add("cuid", "english-app-web");
            form.add("lan", "zh");
            form.add("ctp", "1");
            form.add("per", "4118");   // 度小薇(女声,适合儿童)

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
            ResponseEntity<byte[]> resp = restTemplate.postForEntity(TTS_URL, entity, byte[].class);
            return resp.getBody();
        } catch (Exception e) {
            log.error("TTS 调用失败: {}", text, e);
            return new byte[0];
        }
    }

    /**
     * 语音转文字
     * <p>
     * 当前为占位实现,仅记录日志。
     * </p>
     *
     * @param audioData 音频二进制数据
     * @return 识别出的文本(暂返回空字符串)
     */
    @Override
    public String speechToText(byte[] audioData) {
        log.info("ASR 请求, 音频长度: {} bytes", audioData.length);
        return "";
    }

    /**
     * 发音评测
     * <p>
     * 将音频与参考文本提交至百度发音评测接口,获取 0-100 分数及反馈。
     * 调用失败时返回 0 分与提示文案,保证接口可用。
     * </p>
     *
     * @param audioData 音频数据(wav 16k 16bit mono)
     * @param text      参考文本
     * @return 评测结果(分数 + 反馈)
     */
    @Override
    public ScoreResponse scorePronunciation(byte[] audioData, String text) {
        log.info("发音评测: text={}, audioBytes={}", text, audioData.length);
        try {
            String token = getToken();
            String audioBase64 = Base64.getEncoder().encodeToString(audioData);

            Map<String, Object> body = new HashMap<>();
            body.put("audio", audioBase64);
            body.put("text", text);
            body.put("type", "wav");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = SCORE_URL + "?access_token=" + token;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);

            JsonNode node = MAPPER.readTree(resp.getBody());
            // 百度评测返回字段:score(总分,0-100)、可能还有 pronunciation/fluency 等子项
            int score = node.has("score") ? node.get("score").asInt() : 0;
            String feedback = buildFeedback(score);
            return new ScoreResponse(score, feedback);
        } catch (Exception e) {
            log.error("发音评测调用失败: text={}", text, e);
            return new ScoreResponse(0, "评分服务暂时不可用,请重试");
        }
    }

    /**
     * 根据分数生成鼓励文案
     * <p>
     * 分级规则: 80+ 优秀 / 60-79 良好 / 40-59 及格 / 40 以下鼓励重试。
     * </p>
     *
     * @param score 评测分数(0-100)
     * @return 鼓励文案
     */
    private String buildFeedback(int score) {
        if (score >= 80) return "太棒了!发音很标准!";
        if (score >= 60) return "不错哦!再练练更好!";
        if (score >= 40) return "加油!差一点点!";
        return "再试一次,你可以的!";
    }
}
