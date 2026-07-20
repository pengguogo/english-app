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
    /** 百度语音识别(ASR)接口地址 —— 用于发音评测:识别用户读音后与目标文本做相似度比较 */
    private static final String ASR_URL = "https://vop.baidu.com/server_api";

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
     * 调用百度 TTS 接口合成语音,根据 lan 参数选择语言。
     * 英文使用 per=0(英文男声),中文使用 per=4118(度小薇女声)。
     * 调用失败时返回空字节数组,保证接口不抛异常。
     * </p>
     *
     * @param text 待合成的文本
     * @param lan  语言代码("en" 英文, "zh" 中文)
     * @return 音频二进制数据;失败时返回空数组
     */
    @Override
    public byte[] textToSpeech(String text, String lan) {
        log.info("TTS 请求: text={}, lan={}", text, lan);
        try {
            String token = getToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("tex", text);
            form.add("tok", token);
            form.add("cuid", "english-app-web");
            form.add("lan", lan);
            form.add("ctp", "1");
            // 英文使用英文男声,中文使用度小薇(女声,适合儿童)
            form.add("per", "en".equals(lan) ? "0" : "4118");

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
     * 百度原发音评测接口 {@code brain_assessment/score} 已下线(返回 error_code=3
     * "Unsupported openapi method"),这里改用百度语音识别(ASR)做降级评分:
     * 将用户录音发送至百度 ASR 识别出文本,再与参考文本做 Levenshtein 相似度比较,
     * 将相似度映射为 0-100 分数。
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

            // 构建百度 ASR 请求体
            Map<String, Object> body = new HashMap<>();
            body.put("format", "wav");
            body.put("rate", 16000);
            body.put("channel", 1);
            body.put("cuid", "english-app-web");
            body.put("token", token);
            body.put("speech", audioBase64);
            body.put("len", audioData.length);
            // dev_pid=1737 使用英文模型,适配英语学习场景
            body.put("dev_pid", 1737);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(ASR_URL, entity, String.class);

            JsonNode node = MAPPER.readTree(resp.getBody());
            int errNo = node.has("err_no") ? node.get("err_no").asInt() : -1;
            if (errNo != 0) {
                String errMsg = node.has("err_msg") ? node.get("err_msg").asText() : "unknown";
                log.warn("ASR 识别失败: err_no={}, err_msg={}", errNo, errMsg);
                return new ScoreResponse(0, "没有听清楚,请再试一次");
            }

            // 提取识别结果(result 是字符串数组)
            String recognized = "";
            if (node.has("result") && node.get("result").isArray() && node.get("result").size() > 0) {
                recognized = node.get("result").get(0).asText();
            }
            log.info("ASR 识别结果: recognized={}, target={}", recognized, text);

            // 计算相似度并映射为分数
            int score = calculateScore(text, recognized);
            String feedback = buildFeedback(score);
            return new ScoreResponse(score, feedback);
        } catch (Exception e) {
            log.error("发音评测调用失败: text={}", text, e);
            return new ScoreResponse(0, "评分服务暂时不可用,请重试");
        }
    }

    /**
     * 计算发音评分。
     * 将目标文本与 ASR 识别结果做归一化(小写 + 去标点)后,
     * 用 Levenshtein 编辑距离计算相似度,再映射为 0-100 分数。
     * <p>
     * 映射规则:相似度 ≥ 0.9 → 90-100 分;≥ 0.7 → 70-89 分;以此类推。
     * 这样既奖励发音准确(识别正确)的孩子,也对部分接近的发音给一定分数。
     * </p>
     *
     * @param target    目标文本
     * @param recognized ASR 识别结果
     * @return 0-100 分数
     */
    private int calculateScore(String target, String recognized) {
        String t = normalizeText(target);
        String r = normalizeText(recognized);
        if (t.isEmpty()) return 0;
        if (r.isEmpty()) return 0;

        double similarity = 1.0 - (double) levenshtein(t, r) / Math.max(t.length(), r.length());
        // 相似度 0-1 映射到分数 0-100,并用二次曲线在低分段适当压低、高分段适当提升
        int score = (int) Math.round(similarity * 100);
        // 至少给 10 分底分,避免完全 0 分打击孩子积极性
        return Math.max(10, score);
    }

    /**
     * 文本归一化:转小写、去标点与空白,只保留字母用于比较。
     *
     * @param s 原始文本
     * @return 归一化后的纯字母小写字符串
     */
    private String normalizeText(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("[^a-z]", "");
    }

    /**
     * Levenshtein 编辑距离(动态规划实现)。
     * 用于衡量两个字符串之间的差异程度。
     *
     * @param a 字符串 a
     * @param b 字符串 b
     * @return 编辑距离(非负整数)
     */
    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
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
