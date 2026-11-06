package com.englishapp.security;

import com.englishapp.common.ResultCode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static org.junit.jupiter.api.Assertions.*;

class SessionCurrentChildTest {
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private Instant now = Instant.parse("2026-11-06T02:00:00Z");

    @BeforeEach
    void bind() { RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request)); }

    @AfterEach
    void clear() {
        RequestContextHolder.resetRequestAttributes();
        SecurityContextHolder.clearContext();
    }

    private SessionCurrentChild current() { return new SessionCurrentChild(Clock.fixed(now, ZoneOffset.UTC)); }

    private void login() {
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                new AuthPrincipal(1L, 5L, "a@b.co"), null, List.of()));
    }

    @Test
    void should_抛401_当_未登录() {
        assertEquals(ResultCode.UNAUTHORIZED, assertThrows(AuthException.class, () -> current().requireChildId()).getResultCode());
        assertEquals(ResultCode.UNAUTHORIZED, assertThrows(AuthException.class, () -> current().requireFamilyId()).getResultCode());
        assertTrue(current().currentChildId().isEmpty());
    }

    @Test
    void should_抛409_4091_当_已登录未选孩子() {
        login();
        var e = assertThrows(AuthException.class, () -> current().requireChildId());
        assertEquals(4091, e.getResultCode().getCode());
        assertEquals(409, e.httpStatus());
        assertEquals(5L, current().requireFamilyId());
    }

    @Test
    void should_返回孩子ID_当_已选孩子() {
        login();
        current().selectChild(42);
        assertEquals(42, current().requireChildId());
        assertEquals(42, current().currentChildId().orElseThrow());
    }

    @Test
    void should_10分钟内有效_当_家长已二次验证() {
        login();
        assertEquals(ResultCode.PARENT_VERIFY_REQUIRED,
                assertThrows(AuthException.class, () -> current().requireParentVerified()).getResultCode());
        current().markParentVerified();
        now = now.plus(Duration.ofMinutes(10));
        assertDoesNotThrow(() -> current().requireParentVerified());
        now = now.plusSeconds(1);
        assertThrows(AuthException.class, () -> current().requireParentVerified());
    }
}
