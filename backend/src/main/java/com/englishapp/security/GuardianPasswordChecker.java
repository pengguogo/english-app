package com.englishapp.security;

import com.englishapp.domain.GuardianCredential;
import com.englishapp.repository.GuardianCredentialRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 家长登录密码校验，登录、家长二次验证、游戏解锁共用同一失败计数：连续失败 5 次锁 15 分钟。
 * REQUIRES_NEW：计数独立提交，外层事务回滚也不丢失。调用方须在外层写库之前调用（SQLite 库级写锁）。
 */
@Component
public class GuardianPasswordChecker {
    static final int MAX_FAILURES = 5;
    static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    public enum Result { OK, WRONG, LOCKED }

    private final GuardianCredentialRepository credentials;
    private final PasswordEncoder encoder;
    private final Clock clock;

    public GuardianPasswordChecker(GuardianCredentialRepository credentials, PasswordEncoder encoder, Clock clock) {
        this.credentials = credentials;
        this.encoder = encoder;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result check(Long credentialId, String rawPassword) {
        GuardianCredential c = credentials.findById(credentialId).orElseThrow();
        LocalDateTime now = LocalDateTime.now(clock);
        if (c.getLockedUntil() != null) {
            if (now.isBefore(c.getLockedUntil())) return Result.LOCKED;
            c.setLockedUntil(null);
            c.setFailedAttempts(0);
        }
        if (rawPassword != null && encoder.matches(rawPassword, c.getPasswordHash())) {
            c.setFailedAttempts(0);
            credentials.save(c);
            return Result.OK;
        }
        int failures = c.getFailedAttempts() + 1;
        c.setFailedAttempts(failures);
        if (failures >= MAX_FAILURES) c.setLockedUntil(now.plus(LOCK_DURATION));
        credentials.save(c);
        return Result.WRONG;
    }
}
