package com.englishapp.config;

import com.englishapp.security.AuthException;
import com.englishapp.security.CurrentChild;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 家长区兜底：/child-growth/** 与 /parent-settings/** 要求登录且 10 分钟内完成家长二次验证。
 * 运行在 Spring Security 之后；GET /parent-settings/password 只要求登录。
 */
@Component
public class ChildGrowthAccessFilter extends OncePerRequestFilter {
    private final CurrentChild currentChild;

    public ChildGrowthAccessFilter(CurrentChild currentChild) {
        this.currentChild = currentChild;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if ("GET".equals(request.getMethod()) && "/api/v1/parent-settings/password".equals(path)) return true;
        return !path.startsWith("/api/v1/child-growth/") && !path.startsWith("/api/v1/parent-settings/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        try {
            currentChild.requireParentVerified();
        } catch (AuthException e) {
            response.setStatus(e.httpStatus());
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.getWriter().write("{\"code\":" + e.getResultCode().getCode() + ",\"message\":\""
                    + e.getResultCode().getMessage() + "\",\"data\":null}");
            return;
        }
        chain.doFilter(request, response);
    }
}
