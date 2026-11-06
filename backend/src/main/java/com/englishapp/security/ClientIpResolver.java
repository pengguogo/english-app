package com.englishapp.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 游客额度用的客户端 IP。只有请求来自本机反向代理（nginx 在 127.0.0.1 转发）时才采用 X-Real-IP：
 * 该头由 nginx 以 $remote_addr 覆盖，客户端无法伪造。不使用 X-Forwarded-For（最左段可伪造）。
 */
@Component
public class ClientIpResolver {

    public String resolve(HttpServletRequest request) {
        String remote = request.getRemoteAddr();
        if (isLoopback(remote)) {
            String real = request.getHeader("X-Real-IP");
            if (real != null && real.matches("[0-9A-Fa-f:.]{2,45}")) return real;
        }
        return remote;
    }

    private static boolean isLoopback(String addr) {
        return "127.0.0.1".equals(addr) || "::1".equals(addr) || "0:0:0:0:0:0:0:1".equals(addr);
    }
}
