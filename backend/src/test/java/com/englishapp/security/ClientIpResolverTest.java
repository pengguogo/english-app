package com.englishapp.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientIpResolverTest {
    private final ClientIpResolver resolver = new ClientIpResolver();

    private MockHttpServletRequest request(String remote) {
        var r = new MockHttpServletRequest();
        r.setRemoteAddr(remote);
        return r;
    }

    @Test
    void should_采用XRealIP_当_请求来自本机反向代理() {
        var r = request("127.0.0.1");
        r.addHeader("X-Real-IP", "203.0.113.9");
        assertEquals("203.0.113.9", resolver.resolve(r));
    }

    @Test
    void should_忽略转发头_当_请求直接来自公网() {
        var r = request("198.51.100.1");
        r.addHeader("X-Real-IP", "1.1.1.1");
        r.addHeader("X-Forwarded-For", "2.2.2.2");
        assertEquals("198.51.100.1", resolver.resolve(r));
    }

    @Test
    void should_不采用XForwardedFor_当_来自本机但无XRealIP() {
        var r = request("::1");
        r.addHeader("X-Forwarded-For", "2.2.2.2");
        assertEquals("::1", resolver.resolve(r));
    }

    @Test
    void should_回退remoteAddr_当_XRealIP格式非法() {
        var r = request("127.0.0.1");
        r.addHeader("X-Real-IP", "evil; drop table");
        assertEquals("127.0.0.1", resolver.resolve(r));
    }
}
