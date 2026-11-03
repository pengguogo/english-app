package com.englishapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebConfigCorsTest {
    @Configuration
    @EnableWebMvc
    static class TestConfig extends WebConfig { }

    @RestController
    static class TestController {
        @PostMapping("/api/v1/voice/score")
        String score() { return "ok"; }
    }

    @Test
    void should_允许正式站点并拒绝陌生来源_当_代理以http转发请求() throws Exception {
        try (AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(TestConfig.class, TestController.class);
            context.refresh();
            MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
            mvc.perform(post("/api/v1/voice/score").header("Origin", "https://study.penggeai.com"))
                    .andExpect(status().isOk());
            mvc.perform(post("/api/v1/voice/score").header("Origin", "https://example.com"))
                    .andExpect(status().isForbidden());
        }
    }
}
