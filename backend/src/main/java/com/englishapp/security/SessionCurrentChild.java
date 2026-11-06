package com.englishapp.security;

import com.englishapp.common.ResultCode;
import jakarta.servlet.http.HttpSession;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** 基于 Spring Security 上下文 + HttpSession 的当前身份实现 */
@Component
public class SessionCurrentChild implements CurrentChild {
    static final String CHILD_ID = "EA_CURRENT_CHILD_ID";
    static final String PARENT_VERIFIED_AT = "EA_PARENT_VERIFIED_AT";
    public static final Duration PARENT_VERIFY_TTL = Duration.ofMinutes(10);

    private final Clock clock;

    public SessionCurrentChild(Clock clock) { this.clock = clock; }

    @Override
    public Integer requireChildId() {
        principal();
        return childIdInSession().orElseThrow(() -> new AuthException(ResultCode.CHILD_NOT_SELECTED));
    }

    @Override
    public Long requireFamilyId() { return principal().familyId(); }

    @Override
    public void requireParentVerified() {
        principal();
        HttpSession session = session(false);
        Object at = session == null ? null : session.getAttribute(PARENT_VERIFIED_AT);
        if (!(at instanceof Instant verifiedAt)
                || clock.instant().isAfter(verifiedAt.plus(PARENT_VERIFY_TTL))) {
            throw new AuthException(ResultCode.PARENT_VERIFY_REQUIRED);
        }
    }

    @Override
    public Optional<Integer> currentChildId() {
        return optionalPrincipal().isPresent() ? childIdInSession() : Optional.empty();
    }

    /** 当前登录家长；未登录抛 401 */
    public AuthPrincipal principal() {
        return optionalPrincipal().orElseThrow(() -> new AuthException(ResultCode.UNAUTHORIZED));
    }

    public Optional<AuthPrincipal> optionalPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getPrincipal() instanceof AuthPrincipal p ? Optional.of(p) : Optional.empty();
    }

    /** 选择孩子（调用方须先校验孩子属于当前家庭） */
    public void selectChild(Integer childId) { session(true).setAttribute(CHILD_ID, childId); }

    /** 记录家长二次验证时间 */
    public void markParentVerified() { session(true).setAttribute(PARENT_VERIFIED_AT, clock.instant()); }

    private Optional<Integer> childIdInSession() {
        HttpSession session = session(false);
        return session != null && session.getAttribute(CHILD_ID) instanceof Integer id ? Optional.of(id) : Optional.empty();
    }

    private HttpSession session(boolean create) {
        var attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attrs.getRequest().getSession(create);
    }
}
