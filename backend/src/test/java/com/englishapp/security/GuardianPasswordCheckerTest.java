package com.englishapp.security;

import com.englishapp.domain.GuardianCredential;
import com.englishapp.repository.GuardianCredentialRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuardianPasswordCheckerTest {
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private final GuardianCredentialRepository repo = mock(GuardianCredentialRepository.class);
    private final GuardianCredential credential = new GuardianCredential();
    private Instant now = Instant.parse("2026-11-06T02:00:00Z");

    GuardianPasswordCheckerTest() {
        credential.setId(1L);
        credential.setPasswordHash(encoder.encode("correct-pass"));
        credential.setFailedAttempts(0);
        when(repo.findById(1L)).thenReturn(Optional.of(credential));
    }

    private GuardianPasswordChecker checker() {
        return new GuardianPasswordChecker(repo, encoder, Clock.fixed(now, ZONE));
    }

    @Test
    void should_锁定15分钟_当_连续失败5次() {
        for (int i = 0; i < 4; i++) assertEquals(GuardianPasswordChecker.Result.WRONG, checker().check(1L, "wrong-pass"));
        assertNull(credential.getLockedUntil());
        assertEquals(GuardianPasswordChecker.Result.WRONG, checker().check(1L, "wrong-pass"));
        assertNotNull(credential.getLockedUntil());
        assertEquals(GuardianPasswordChecker.Result.LOCKED, checker().check(1L, "correct-pass"));
        now = now.plus(Duration.ofMinutes(14));
        assertEquals(GuardianPasswordChecker.Result.LOCKED, checker().check(1L, "correct-pass"));
    }

    @Test
    void should_解锁并清零_当_锁定过期后密码正确() {
        for (int i = 0; i < 5; i++) checker().check(1L, "wrong-pass");
        now = now.plus(Duration.ofMinutes(16));
        assertEquals(GuardianPasswordChecker.Result.OK, checker().check(1L, "correct-pass"));
        assertEquals(0, credential.getFailedAttempts());
        assertNull(credential.getLockedUntil());
    }

    @Test
    void should_清零失败计数_当_未锁定时密码正确() {
        checker().check(1L, "wrong-pass");
        checker().check(1L, "wrong-pass");
        assertEquals(GuardianPasswordChecker.Result.OK, checker().check(1L, "correct-pass"));
        assertEquals(0, credential.getFailedAttempts());
    }
}
