package com.englishapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ChildGrowthAccessFilter extends OncePerRequestFilter {
    private final String accessKey;

    public ChildGrowthAccessFilter(@Value("${app.child-growth.access-key:}") String accessKey) {
        this.accessKey = accessKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/v1/child-growth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        if (accessKey.isBlank()) {
            reject(response, 503, "请先配置成长档案访问口令");
            return;
        }
        String supplied = request.getHeader("X-Child-Growth-Key");
        if (supplied == null || !MessageDigest.isEqual(accessKey.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8))) {
            reject(response, 401, "访问口令不正确");
            return;
        }
        chain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\",\"data\":null}");
    }
}
