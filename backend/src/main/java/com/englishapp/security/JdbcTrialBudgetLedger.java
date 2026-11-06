package com.englishapp.security;

import java.time.Clock;
import java.time.LocalDate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 全站试用预算账本：非白名单用户的模型花费按北京时间每天累计（分），超过 limit_cents 后当天只走检索。
 * 金额 = token × 配置单价，由调用方算好传入；只记非白名单家庭。
 */
@Component
public class JdbcTrialBudgetLedger implements TrialBudgetLedger {

    private final JdbcTemplate jdbc;
    private final Clock clock;

    public JdbcTrialBudgetLedger(JdbcTemplate jdbc, Clock clock) {
        this.jdbc = jdbc;
        this.clock = clock;
    }

    @Override
    public boolean hasRemainingBudget() {
        Integer left = jdbc.query("SELECT limit_cents - spent_cents FROM agent_trial_budget_daily WHERE budget_date = ?",
                rs -> rs.next() ? rs.getInt(1) : null, today());
        return left == null || left > 0;
    }

    @Override
    public void record(double cents) {
        if (cents <= 0) return;
        // 不足 1 分按 1 分计，宁可多算，避免小额调用永远不入账
        int c = (int) Math.ceil(cents);
        jdbc.update("INSERT INTO agent_trial_budget_daily (budget_date, spent_cents) VALUES (?, ?) "
                + "ON CONFLICT(budget_date) DO UPDATE SET spent_cents = spent_cents + excluded.spent_cents, "
                + "updated_at = CURRENT_TIMESTAMP", today(), c);
    }

    private String today() {
        return LocalDate.now(clock.withZone(JdbcQuotaService.BEIJING)).toString();
    }
}
