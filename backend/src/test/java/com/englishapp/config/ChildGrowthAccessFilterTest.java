package com.englishapp.config;

import com.englishapp.common.ResultCode;
import com.englishapp.security.AuthException;
import com.englishapp.security.CurrentChild;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChildGrowthAccessFilterTest {
    @Test
    void should_返回403_4031_当_家长未二次验证() throws Exception {
        CurrentChild current = mock(CurrentChild.class);
        doThrow(new AuthException(ResultCode.PARENT_VERIFY_REQUIRED)).when(current).requireParentVerified();
        for (String path : new String[]{"/api/v1/child-growth/profile", "/api/v1/parent-settings/week"}) {
            var response = new MockHttpServletResponse();
            var chain = new MockFilterChain();
            new ChildGrowthAccessFilter(current).doFilter(new MockHttpServletRequest("GET", path), response, chain);
            assertEquals(403, response.getStatus());
            assertTrue(response.getContentAsString().contains("4031"));
            assertNull(chain.getRequest());
        }
    }

    @Test
    void should_放行_当_家长已二次验证() throws Exception {
        CurrentChild current = mock(CurrentChild.class);
        var chain = new MockFilterChain();
        new ChildGrowthAccessFilter(current).doFilter(
                new MockHttpServletRequest("GET", "/api/v1/child-growth/photos"), new MockHttpServletResponse(), chain);
        assertNotNull(chain.getRequest());
    }

    @Test
    void should_不要求二次验证_当_查询家长密码状态或访问其他接口() throws Exception {
        CurrentChild current = mock(CurrentChild.class);
        var filter = new ChildGrowthAccessFilter(current);
        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/parent-settings/password"),
                new MockHttpServletResponse(), new MockFilterChain());
        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/subjects"),
                new MockHttpServletResponse(), new MockFilterChain());
        verify(current, never()).requireParentVerified();
    }
}
