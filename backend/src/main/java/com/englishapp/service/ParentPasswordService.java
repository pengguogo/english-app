package com.englishapp.service;

import com.englishapp.common.ResultCode;
import com.englishapp.security.AuthException;
import com.englishapp.security.GuardianPasswordChecker;
import com.englishapp.security.SessionCurrentChild;
import org.springframework.stereotype.Service;

/**
 * 家长二次验证：校验当前家庭的家长登录密码，和登录共用失败计数。
 * 成功后刷新 Session 中的验证时间（10 分钟），/auth/parent-verify 与游戏解锁共用。
 */
@Service
public class ParentPasswordService {
    private final SessionCurrentChild current;
    private final GuardianPasswordChecker checker;

    public ParentPasswordService(SessionCurrentChild current, GuardianPasswordChecker checker) {
        this.current = current;
        this.checker = checker;
    }

    /** 家长密码即登录密码，注册即已设置 */
    public boolean isConfigured() { return true; }

    /**
     * 校验成功返回 true；未登录 401，密码错误 403(4032)，锁定 401(4012)。
     * 须在调用方写库之前调用。
     */
    public boolean verify(String password) {
        switch (checker.check(current.principal().guardianId(), password)) {
            case LOCKED -> throw new AuthException(ResultCode.ACCOUNT_LOCKED);
            case WRONG -> throw new AuthException(ResultCode.PARENT_PASSWORD_WRONG);
            default -> { }
        }
        current.markParentVerified();
        return true;
    }
}
