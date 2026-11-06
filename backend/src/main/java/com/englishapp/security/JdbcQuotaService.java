package com.englishapp.security;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 计数存 agent_quota_usage。检查与写入在同一条 INSERT ... SELECT 中完成：
 * SQLite 写语句开始即持有写锁，并发请求不会同时越过上限。
 */
@Service
public class JdbcQuotaService implements QuotaService {
    static final ZoneId BEIJING = ZoneId.of("Asia/Shanghai");
    static final int FAMILY_PER_DAY = 10;
    static final int FAMILY_PER_MINUTE = 3;
    static final int GUEST_PER_DAY = 3;
    static final int GUEST_PER_MINUTE = 2;
    private static final long MINUTE_MS = Duration.ofMinutes(1).toMillis();

    private final JdbcTemplate jdbc;
    private final Clock clock;
    private final TrialBudgetLedger budget;
    private final Set<String> unlimitedEmails;

    public JdbcQuotaService(JdbcTemplate jdbc, Clock clock, TrialBudgetLedger budget,
                            @Value("${app.agent.unlimited-family-emails:}") String unlimitedEmails) {
        this.jdbc = jdbc;
        this.clock = clock;
        this.budget = budget;
        this.unlimitedEmails = Arrays.stream(unlimitedEmails.split(","))
                .map(s -> s.trim().toLowerCase(Locale.ROOT)).filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Decision tryConsumeFamily(Long familyId) {
        if (isUnlimited(familyId)) return Decision.ALLOWED;
        return consume("family:" + familyId, FAMILY_PER_DAY, FAMILY_PER_MINUTE);
    }

    @Override
    public Decision tryConsumeGuest(String clientIp) {
        return consume("ip:" + clientIp, GUEST_PER_DAY, GUEST_PER_MINUTE);
    }

    private Decision consume(String key, int perDay, int perMinute) {
        if (!budget.hasRemainingBudget()) return Decision.TRIAL_BUDGET_EXHAUSTED;
        long now = clock.millis();
        long dayStart = LocalDate.ofInstant(clock.instant(), BEIJING).atStartOfDay(BEIJING).toInstant().toEpochMilli();
        long minuteStart = now - MINUTE_MS;
        int inserted = jdbc.update("INSERT INTO agent_quota_usage(subject_key, used_at) SELECT ?, ? WHERE "
                + "(SELECT COUNT(*) FROM agent_quota_usage WHERE subject_key = ? AND used_at >= ?) < ? AND "
                + "(SELECT COUNT(*) FROM agent_quota_usage WHERE subject_key = ? AND used_at > ?) < ?",
                key, now, key, dayStart, perDay, key, minuteStart, perMinute);
        if (inserted == 1) {
            // 只保留当天记录，表不会无限增长
            jdbc.update("DELETE FROM agent_quota_usage WHERE subject_key = ? AND used_at < ?", key, dayStart);
            return Decision.ALLOWED;
        }
        Integer today = jdbc.queryForObject("SELECT COUNT(*) FROM agent_quota_usage WHERE subject_key = ? AND used_at >= ?",
                Integer.class, key, dayStart);
        return today != null && today >= perDay ? Decision.DAILY_LIMIT : Decision.MINUTE_LIMIT;
    }

    /** 家庭任一家长邮箱在白名单内即不限次数 */
    private boolean isUnlimited(Long familyId) {
        if (unlimitedEmails.isEmpty()) return false;
        return jdbc.queryForList("SELECT email FROM guardian_credential WHERE family_id = ?", String.class, familyId)
                .stream().anyMatch(unlimitedEmails::contains);
    }
}
