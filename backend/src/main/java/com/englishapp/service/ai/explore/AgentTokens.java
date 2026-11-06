package com.englishapp.service.ai.explore;

import com.englishapp.common.ResultCode;
import com.englishapp.security.AuthException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 无状态签名令牌：答题令牌（正确选项不下发）、语音回合令牌（asr 已扣过额度，chat 不再扣）。
 * 格式 base64url(payload).base64url(HMAC-SHA256)，payload = kind|subject|value|expiresAtMillis。
 */
@Component
public class AgentTokens {

    private static final Logger log = LoggerFactory.getLogger(AgentTokens.class);
    static final long QUIZ_TTL_MS = 10 * 60_000;
    static final long TURN_TTL_MS = 60_000;

    private final byte[] secret;
    private final Clock clock;

    public AgentTokens(@Value("${app.agent.quiz-secret:}") String secret, Clock clock) {
        this.clock = clock;
        if (secret == null || secret.isBlank()) {
            byte[] random = new byte[32];
            new SecureRandom().nextBytes(random);
            this.secret = random;
            log.warn("未配置 AGENT_QUIZ_SECRET，使用随机密钥：重启后旧题目令牌失效");
        } else {
            this.secret = secret.getBytes(StandardCharsets.UTF_8);
        }
    }

    /** 答题令牌里携带的判分信息（已过输出审核）；签名防篡改，不加密，不放敏感内容 */
    public record QuizKey(String answerId, String praise, String hint) { }

    /** 答题令牌：绑定答题人（child:ID 或 guest）、正确选项 id 与反馈语 */
    public String quiz(String subject, QuizKey key) {
        String value = Base64.getUrlEncoder().withoutPadding().encodeToString(
                (key.answerId() + "\u001f" + key.praise() + "\u001f" + key.hint()).getBytes(StandardCharsets.UTF_8));
        return sign("quiz", subject, value, QUIZ_TTL_MS);
    }

    /** 校验答题令牌；失效或不属于当前答题人 → 400/4002 */
    public QuizKey verifyQuiz(String token, String subject) {
        String value = verify(token, "quiz", subject);
        String[] p = new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8).split("\u001f", -1);
        if (p.length != 3) throw new AuthException(ResultCode.AGENT_QUIZ_INVALID);
        return new QuizKey(p[0], p[1], p[2]);
    }

    public String turn(Long familyId) {
        return sign("turn", "family:" + familyId, "1", TURN_TTL_MS);
    }

    /** 语音回合令牌是否有效（不抛异常：无效时按普通回合扣额度） */
    public boolean validTurn(String token, Long familyId) {
        if (token == null || token.isBlank()) return false;
        try {
            verify(token, "turn", "family:" + familyId);
            return true;
        } catch (AuthException e) {
            return false;
        }
    }

    private String sign(String kind, String subject, String value, long ttl) {
        String payload = kind + "|" + subject + "|" + value + "|" + (clock.millis() + ttl);
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        return enc.encodeToString(payload.getBytes(StandardCharsets.UTF_8)) + "." + enc.encodeToString(hmac(payload));
    }

    private String verify(String token, String kind, String subject) {
        AuthException invalid = new AuthException(ResultCode.AGENT_QUIZ_INVALID);
        if (token == null) throw invalid;
        int dot = token.indexOf('.');
        if (dot <= 0) throw invalid;
        String payload;
        byte[] mac;
        try {
            payload = new String(Base64.getUrlDecoder().decode(token.substring(0, dot)), StandardCharsets.UTF_8);
            mac = Base64.getUrlDecoder().decode(token.substring(dot + 1));
        } catch (IllegalArgumentException e) {
            throw invalid;
        }
        if (!MessageDigest.isEqual(hmac(payload), mac)) throw invalid;
        String[] parts = payload.split("\\|", -1);
        if (parts.length != 4 || !parts[0].equals(kind) || !parts[1].equals(subject)) throw invalid;
        try {
            if (Long.parseLong(parts[3]) < clock.millis()) throw invalid;
        } catch (NumberFormatException e) {
            throw invalid;
        }
        return parts[2];
    }

    private byte[] hmac(String payload) {
        try {
            Mac m = Mac.getInstance("HmacSHA256");
            m.init(new SecretKeySpec(secret, "HmacSHA256"));
            return m.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC 不可用", e);
        }
    }
}
