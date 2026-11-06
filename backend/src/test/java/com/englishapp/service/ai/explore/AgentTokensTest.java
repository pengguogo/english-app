package com.englishapp.service.ai.explore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.englishapp.security.AuthException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class AgentTokensTest {

    private final AtomicLong now = new AtomicLong(1_800_000_000_000L);
    private final Clock clock = new Clock() {
        @Override public ZoneOffset getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(java.time.ZoneId z) { return this; }
        @Override public Instant instant() { return Instant.ofEpochMilli(now.get()); }
    };
    private final AgentTokens tokens = new AgentTokens("test-secret", clock);
    private final AgentTokens.QuizKey key = new AgentTokens.QuizKey("b", "答对啦", "再想想");

    @Test
    void should_还原正确答案_当_令牌有效() {
        String t = tokens.quiz("child:1", key);
        assertEquals(key, tokens.verifyQuiz(t, "child:1"));
    }

    @Test
    void should_拒绝_当_换了答题人() {
        String t = tokens.quiz("child:1", key);
        assertThrows(AuthException.class, () -> tokens.verifyQuiz(t, "child:2"));
        assertThrows(AuthException.class, () -> tokens.verifyQuiz(t, "guest"));
    }

    @Test
    void should_拒绝_当_超过10分钟() {
        String t = tokens.quiz("guest", key);
        now.addAndGet(AgentTokens.QUIZ_TTL_MS + 1);
        assertThrows(AuthException.class, () -> tokens.verifyQuiz(t, "guest"));
    }

    @Test
    void should_拒绝_当_令牌被篡改或乱填() {
        String t = tokens.quiz("guest", key);
        String tampered = "A" + t.substring(1);
        assertThrows(AuthException.class, () -> tokens.verifyQuiz(tampered, "guest"));
        assertThrows(AuthException.class, () -> tokens.verifyQuiz("abc", "guest"));
        assertThrows(AuthException.class, () -> tokens.verifyQuiz("!!.!!", "guest"));
        // 不同密钥签的令牌无效
        String other = new AgentTokens("another", clock).quiz("guest", key);
        assertThrows(AuthException.class, () -> tokens.verifyQuiz(other, "guest"));
    }

    @Test
    void should_语音回合令牌只对本家庭60秒有效() {
        String t = tokens.turn(7L);
        assertTrue(tokens.validTurn(t, 7L));
        assertFalse(tokens.validTurn(t, 8L));
        // 答题令牌不能冒充回合令牌
        assertFalse(tokens.validTurn(tokens.quiz("family:7", key), 7L));
        now.addAndGet(AgentTokens.TURN_TTL_MS + 1);
        assertFalse(tokens.validTurn(t, 7L));
        assertFalse(tokens.validTurn(null, 7L));
    }
}
