package com.englishapp.security;

import org.springframework.stereotype.Component;

/** 阶段 0 占位：尚未接入计费，预算视为始终有余额 */
@Component
public class UnlimitedTrialBudgetLedger implements TrialBudgetLedger {
    @Override
    public boolean hasRemainingBudget() { return true; }
}
