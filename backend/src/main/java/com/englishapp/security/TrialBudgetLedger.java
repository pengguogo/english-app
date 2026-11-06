package com.englishapp.security;

/**
 * 全站试用预算账本（非白名单用户合计每天 10 元）。阶段 0 只留接口，金额统计在阶段 2 实现。
 */
public interface TrialBudgetLedger {

    /** 当天试用预算是否还有余额 */
    boolean hasRemainingBudget();
}
