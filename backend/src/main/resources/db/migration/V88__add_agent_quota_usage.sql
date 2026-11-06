-- Agent 回合计数（服务端，不信任前端）：每消耗一个回合记一行。
-- subject_key：'family:<id>' 或 'ip:<地址>'；used_at 为毫秒时间戳，按北京时间 0 点切日。
CREATE TABLE agent_quota_usage (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    subject_key VARCHAR(80) NOT NULL,
    used_at INTEGER NOT NULL
);
CREATE INDEX idx_agent_quota_usage_subject_key ON agent_quota_usage(subject_key, used_at);

-- 全站试用预算账本：非白名单用户合计每天上限（分）。阶段 0 只建结构，金额统计在阶段 2 写入。
CREATE TABLE agent_trial_budget_daily (
    budget_date DATE PRIMARY KEY,
    spent_cents INTEGER NOT NULL DEFAULT 0,
    limit_cents INTEGER NOT NULL DEFAULT 1000,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
