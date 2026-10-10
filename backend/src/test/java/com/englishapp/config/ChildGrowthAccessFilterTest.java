package com.englishapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;

class ChildGrowthAccessFilterTest {
    @Test
    void should_保护密码设置和周报_当_没有家长口令() throws Exception {
        for (String path : new String[]{"/api/v1/parent-settings/password", "/api/v1/parent-settings/week"}) {
            var response = new MockHttpServletResponse();
            new ChildGrowthAccessFilter("secret").doFilter(new MockHttpServletRequest("PUT", path), response, new MockFilterChain());
            assertEquals(401, response.getStatus());
        }
    }

    @Test
    void should_拒绝错误口令_当_读取成长档案() throws Exception {
        var filter = new ChildGrowthAccessFilter("secret");
        var request = new MockHttpServletRequest("GET", "/api/v1/child-growth/profile");
        request.addHeader("X-Child-Growth-Key", "wrong");
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertEquals(401, response.getStatus());
    }

    @Test
    void should_放行正确口令_当_读取成长档案() throws Exception {
        var filter = new ChildGrowthAccessFilter("secret");
        var request = new MockHttpServletRequest("GET", "/api/v1/child-growth/profile");
        request.addHeader("X-Child-Growth-Key", "secret");
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();
        filter.doFilter(request, response, chain);
        assertNotNull(chain.getRequest());
    }

    @Test
    void should_拒绝访问_当_未配置口令() throws Exception {
        var filter = new ChildGrowthAccessFilter("");
        var response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/child-growth/photos"),
                response, new MockFilterChain());
        assertEquals(503, response.getStatus());
    }
}
