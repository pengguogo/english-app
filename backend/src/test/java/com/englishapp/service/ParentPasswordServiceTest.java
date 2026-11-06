package com.englishapp.service;

import com.englishapp.common.ResultCode;
import com.englishapp.security.AuthException;
import com.englishapp.security.AuthPrincipal;
import com.englishapp.security.GuardianPasswordChecker;
import com.englishapp.security.SessionCurrentChild;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParentPasswordServiceTest {
    private final SessionCurrentChild current = mock(SessionCurrentChild.class);
    private final GuardianPasswordChecker checker = mock(GuardianPasswordChecker.class);
    private final ParentPasswordService service = new ParentPasswordService(current, checker);

    ParentPasswordServiceTest() {
        when(current.principal()).thenReturn(new AuthPrincipal(7L, 3L, "a@b.co"));
    }

    @Test
    void should_刷新验证时间_当_家长密码正确() {
        when(checker.check(7L, "goodpass")).thenReturn(GuardianPasswordChecker.Result.OK);
        assertTrue(service.verify("goodpass"));
        verify(current).markParentVerified();
    }

    @Test
    void should_抛4032且不刷新_当_密码错误() {
        when(checker.check(7L, "bad")).thenReturn(GuardianPasswordChecker.Result.WRONG);
        var e = assertThrows(AuthException.class, () -> service.verify("bad"));
        assertEquals(ResultCode.PARENT_PASSWORD_WRONG, e.getResultCode());
        assertEquals(403, e.httpStatus());
        verify(current, never()).markParentVerified();
    }

    @Test
    void should_抛4012_当_账号已锁定() {
        when(checker.check(7L, "goodpass")).thenReturn(GuardianPasswordChecker.Result.LOCKED);
        var e = assertThrows(AuthException.class, () -> service.verify("goodpass"));
        assertEquals(ResultCode.ACCOUNT_LOCKED, e.getResultCode());
        assertEquals(401, e.httpStatus());
    }

    @Test
    void should_始终已配置_当_家长密码即登录密码() {
        assertTrue(service.isConfigured());
    }
}
