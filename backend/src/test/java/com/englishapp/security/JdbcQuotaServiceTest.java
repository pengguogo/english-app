package com.englishapp.security;

import com.englishapp.security.QuotaService.Decision;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import static org.junit.jupiter.api.Assertions.*;

/** 真实 SQLite 临时库；用可推进的 Clock 覆盖每分钟与北京时间 0 点边界 */
class JdbcQuotaServiceTest {
    @TempDir
    Path dir;

    private JdbcTemplate jdbc;
    private final MutableClock clock = new MutableClock(Instant.parse("2026-11-06T02:00:00Z")); // 北京 10:00
    private boolean budgetLeft = true;

    @BeforeEach
    void setUp() throws Exception {
        Path db = Files.createFile(dir.resolve("quota.db"));
        jdbc = new JdbcTemplate(new SingleConnectionDataSource("jdbc:sqlite:" + db, true));
        jdbc.execute("CREATE TABLE agent_quota_usage (id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " subject_key VARCHAR(80) NOT NULL, used_at INTEGER NOT NULL)");
        jdbc.execute("CREATE TABLE guardian_credential (id INTEGER PRIMARY KEY, family_id INTEGER, email TEXT)");
        jdbc.update("INSERT INTO guardian_credential VALUES (1, 7, 'vip@example.com'), (2, 8, 'normal@example.com')");
    }

    private QuotaService service(String whitelist) {
        return new JdbcQuotaService(jdbc, clock, () -> budgetLeft, whitelist);
    }

    @Test
    void should_每分钟最多3回合_当_注册家庭() {
        QuotaService q = service("");
        for (int i = 0; i < 3; i++) assertEquals(Decision.ALLOWED, q.tryConsumeFamily(8L));
        assertEquals(Decision.MINUTE_LIMIT, q.tryConsumeFamily(8L));
        clock.advance(Duration.ofSeconds(59));
        assertEquals(Decision.MINUTE_LIMIT, q.tryConsumeFamily(8L));
        clock.advance(Duration.ofSeconds(1));
        assertEquals(Decision.ALLOWED, q.tryConsumeFamily(8L), "满 60 秒后第一条移出窗口");
    }

    @Test
    void should_每天10回合且北京时间0点重置_当_注册家庭() {
        QuotaService q = service("");
        for (int i = 0; i < 10; i++) {
            assertEquals(Decision.ALLOWED, q.tryConsumeFamily(8L));
            clock.advance(Duration.ofMinutes(1));
        }
        assertEquals(Decision.DAILY_LIMIT, q.tryConsumeFamily(8L));
        clock.set(Instant.parse("2026-11-06T15:59:59Z")); // 北京 23:59:59
        assertEquals(Decision.DAILY_LIMIT, q.tryConsumeFamily(8L));
        clock.set(Instant.parse("2026-11-06T16:00:00Z")); // 北京次日 00:00
        assertEquals(Decision.ALLOWED, q.tryConsumeFamily(8L));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM agent_quota_usage", Integer.class), "前一天记录已清理");
    }

    @Test
    void should_每天3回合每分钟2回合_当_游客按IP() {
        QuotaService q = service("");
        assertEquals(Decision.ALLOWED, q.tryConsumeGuest("1.2.3.4"));
        assertEquals(Decision.ALLOWED, q.tryConsumeGuest("1.2.3.4"));
        assertEquals(Decision.MINUTE_LIMIT, q.tryConsumeGuest("1.2.3.4"));
        assertEquals(Decision.ALLOWED, q.tryConsumeGuest("5.6.7.8"), "不同 IP 独立计数");
        clock.advance(Duration.ofMinutes(1));
        assertEquals(Decision.ALLOWED, q.tryConsumeGuest("1.2.3.4"));
        clock.advance(Duration.ofMinutes(1));
        assertEquals(Decision.DAILY_LIMIT, q.tryConsumeGuest("1.2.3.4"));
    }

    @Test
    void should_不限次数且不记数_当_白名单家庭() {
        QuotaService q = service(" VIP@example.com , other@x.com");
        for (int i = 0; i < 20; i++) assertEquals(Decision.ALLOWED, q.tryConsumeFamily(7L));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM agent_quota_usage", Integer.class));
        budgetLeft = false;
        assertEquals(Decision.ALLOWED, q.tryConsumeFamily(7L), "白名单不受全站试用预算影响");
    }

    @Test
    void should_拒绝且不记数_当_全站试用预算用完() {
        budgetLeft = false;
        QuotaService q = service("");
        assertEquals(Decision.TRIAL_BUDGET_EXHAUSTED, q.tryConsumeFamily(8L));
        assertEquals(Decision.TRIAL_BUDGET_EXHAUSTED, q.tryConsumeGuest("1.2.3.4"));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM agent_quota_usage", Integer.class));
    }

    @Test
    void should_不消耗额度_当_被拒绝() {
        QuotaService q = service("");
        for (int i = 0; i < 3; i++) q.tryConsumeFamily(8L);
        for (int i = 0; i < 5; i++) assertEquals(Decision.MINUTE_LIMIT, q.tryConsumeFamily(8L));
        assertEquals(3, jdbc.queryForObject("SELECT COUNT(*) FROM agent_quota_usage", Integer.class));
    }

    /** 测试用可推进时钟 */
    static final class MutableClock extends Clock {
        private Instant now;
        MutableClock(Instant now) { this.now = now; }
        void advance(Duration d) { now = now.plus(d); }
        void set(Instant i) { now = i; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }
}
