package com.englishapp.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

/** 建立 / 销毁登录 Session；登录时更换 Session ID 防会话固定，并清空旧孩子与家长验证状态 */
@Component
public class SessionLogin {
    private final SecurityContextRepository contexts;

    public SessionLogin(SecurityContextRepository contexts) { this.contexts = contexts; }

    public void login(AuthPrincipal principal, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        session.removeAttribute(SessionCurrentChild.CHILD_ID);
        session.removeAttribute(SessionCurrentChild.PARENT_VERIFIED_AT);
        request.changeSessionId();
        var auth = UsernamePasswordAuthenticationToken.authenticated(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_GUARDIAN")));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        contexts.saveContext(context, request, response);
    }

    public void logout(HttpServletRequest request) {
        invalidate(request);
        SecurityContextHolder.clearContext();
    }

    private static void invalidate(HttpServletRequest request) {
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
    }
}
