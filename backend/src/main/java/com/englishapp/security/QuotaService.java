package com.englishapp.security;

/**
 * Agent 回合额度（设计 5.2）：一个回合 = agent 接口一次调用。
 * 白名单家庭不限；注册家庭每天 10 / 每分钟 3；游客按 IP 每天 3 / 每分钟 2；北京时间 0 点重置。
 * <p>
 * TODO(阶段 1 接入 agent 接口时定)：被拒时的 HTTP 映射，建议 429 + 子码
 * 4291 分钟限流 / 4292 当日额度 / 4293 全站预算。
 */
public interface QuotaService {

    enum Decision { ALLOWED, MINUTE_LIMIT, DAILY_LIMIT, TRIAL_BUDGET_EXHAUSTED }

    /** 已登录家庭消耗一个回合；允许时计数 +1 */
    Decision tryConsumeFamily(Long familyId);

    /** 未登录游客按 IP 消耗一个回合；允许时计数 +1 */
    Decision tryConsumeGuest(String clientIp);
}
