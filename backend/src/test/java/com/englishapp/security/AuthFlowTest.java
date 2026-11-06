package com.englishapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 鉴权流程：未选孩子 409、登录失败锁定、家长二次验证过期、忽略客户端 userId */
class AuthFlowTest extends IntegrationTestBase {

    @SpyBean
    private Clock clock;
    @Autowired
    private JdbcTemplate jdbc;

    /** 测试内可推进的“当前时间” */
    private Instant now;

    @BeforeEach
    void freezeClock() {
        now = Instant.parse("2026-11-06T10:00:00Z");
        doAnswer(inv -> now).when(clock).instant();
        doAnswer(inv -> ZoneId.of("Asia/Shanghai")).when(clock).getZone();
    }

    private String loginBody(String email, String password) throws Exception {
        return json(Map.of("email", email, "password", password));
    }

    @Test
    void should_返回409_当_已登录未选孩子() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        MockHttpSession fresh = loginAs(parent);
        mockMvc.perform(get("/api/v1/wrong-answers").session(fresh))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(4091));
    }

    @Test
    void should_锁定15分钟且正确密码也拒绝_当_同一邮箱连续失败5次() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/auth/login").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON).content(loginBody(parent.email(), "wrong-password")))
                    .andExpect(status().isUnauthorized());
        }
        mockMvc.perform(post("/api/v1/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(loginBody(parent.email(), parent.password())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4012));

        now = now.plus(Duration.ofMinutes(16));
        mockMvc.perform(post("/api/v1/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(loginBody(parent.email(), parent.password())))
                .andExpect(status().isOk());
    }

    @Test
    void should_返回403_当_未做家长二次验证() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        mockMvc.perform(parent.with(get("/api/v1/child-growth/profile")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4031));
        mockMvc.perform(parent.with(get("/api/v1/parent-settings/week")))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_返回403_当_家长验证密码错误() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        mockMvc.perform(parent.with(post("/api/v1/auth/parent-verify"))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("password", "wrong-password"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4032));
    }

    @Test
    void should_10分钟内放行之后403_当_家长二次验证过期() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        verifyParent(parent);
        now = now.plus(Duration.ofMinutes(9));
        mockMvc.perform(parent.with(get("/api/v1/child-growth/profile"))).andExpect(status().isOk());

        now = now.plus(Duration.ofMinutes(2));
        mockMvc.perform(parent.with(get("/api/v1/child-growth/profile")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4031));
    }

    @Test
    void should_忽略客户端userId_当_请求带userId等于1() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        int lessonId = jdbc.queryForObject("SELECT MIN(id) FROM lesson WHERE type = 'QUIZ'", Integer.class);
        // 冒充 user_id=1 写错题
        jdbc.update("INSERT INTO wrong_answer (user_id, lesson_id, question_index, question_type) VALUES (1, ?, 0, 'QUIZ')", lessonId);

        mockMvc.perform(parent.with(get("/api/v1/wrong-answers").param("userId", "1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));

        String body = "{\"eventId\":\"" + UUID.randomUUID() + "\",\"lessonId\":" + lessonId
                + ",\"questionIndex\":0,\"firstCorrect\":true,\"assistedCorrect\":false,\"userId\":1}";
        mockMvc.perform(parent.with(post("/api/v1/learning/attempts").param("userId", "1"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        assertEquals(parent.childId(), jdbc.queryForObject(
                "SELECT user_id FROM learning_attempt ORDER BY rowid DESC LIMIT 1", Integer.class));
    }

    @Test
    void should_共用失败计数并锁定_当_解锁游戏家长验证和登录累计输错5次() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        String wrong = json(Map.of("password", "wrong-password"));
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(parent.with(post("/api/v1/games/unlock")).contentType(MediaType.APPLICATION_JSON).content(wrong))
                    .andExpect(status().isForbidden());
            mockMvc.perform(parent.with(post("/api/v1/auth/parent-verify")).contentType(MediaType.APPLICATION_JSON).content(wrong))
                    .andExpect(status().isForbidden());
        }
        mockMvc.perform(post("/api/v1/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(loginBody(parent.email(), "wrong-password")))
                .andExpect(status().isUnauthorized());

        String right = json(Map.of("password", parent.password()));
        mockMvc.perform(parent.with(post("/api/v1/games/unlock")).contentType(MediaType.APPLICATION_JSON).content(right))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4012));
        mockMvc.perform(parent.with(post("/api/v1/auth/parent-verify")).contentType(MediaType.APPLICATION_JSON).content(right))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4012));
        mockMvc.perform(post("/api/v1/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(loginBody(parent.email(), parent.password())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4012));
    }

    @Test
    void should_锁定解锁和登录_当_解锁游戏连续输错5次() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        String wrong = json(Map.of("password", "wrong-password"));
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(parent.with(post("/api/v1/games/unlock")).contentType(MediaType.APPLICATION_JSON).content(wrong))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(4032));
        }
        mockMvc.perform(parent.with(post("/api/v1/games/unlock"))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("password", parent.password()))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4012));
        mockMvc.perform(post("/api/v1/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(loginBody(parent.email(), parent.password())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4012));
    }

    @Test
    void should_返回403且4032_当_解锁游戏密码错误() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        mockMvc.perform(parent.with(post("/api/v1/games/unlock"))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("password", "wrong-password"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4032));
        mockMvc.perform(parent.with(get("/api/v1/parent-settings/week"))).andExpect(status().isForbidden());
    }

    @Test
    void should_10分钟内免再验证_当_解锁游戏成功后访问家长报告() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        mockMvc.perform(parent.with(post("/api/v1/games/unlock"))
                        .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("password", parent.password()))))
                .andExpect(status().isOk());
        now = now.plus(Duration.ofMinutes(9));
        mockMvc.perform(parent.with(get("/api/v1/parent-settings/week"))).andExpect(status().isOk());
        now = now.plus(Duration.ofMinutes(2));
        mockMvc.perform(parent.with(get("/api/v1/parent-settings/week")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4031));
    }

    @Test
    void should_返回409且4092_当_注册已存在邮箱() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        mockMvc.perform(post("/api/v1/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(loginBody(parent.email(), "AnotherPass123")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(4092));
    }

    @Test
    void should_返回401且会话失效_当_登出后访问() throws Exception {
        Actor parent = newFamilyWithChild("小明");
        mockMvc.perform(parent.with(post("/api/v1/auth/logout"))).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/auth/me").session(parent.session()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
        mockMvc.perform(get("/api/v1/wrong-answers").session(parent.session()))
                .andExpect(status().isUnauthorized());
    }
}
