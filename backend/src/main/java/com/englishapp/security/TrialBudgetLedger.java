package com.englishapp.security;

/**
 * 全站试用预算账本（非白名单用户合计每天上限，默认 10 元，见 agent_trial_budget_daily.limit_cents）。
 */
public interface TrialBudgetLedger {

    /** 当天试用预算是否还有余额 */
    boolean hasRemainingBudget();

    /** 记一笔非白名单用户的模型花费（分）；默认不记（测试替身用） */
    default void record(double cents) { }
}
