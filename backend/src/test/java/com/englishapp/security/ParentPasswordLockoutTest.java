package com.englishapp.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 真实 SQLite：游戏解锁输错密码，外层事务回滚后失败计数仍保留，且不出现 SQLITE_BUSY */
class ParentPasswordLockoutTest extends IntegrationTestBase {
    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void should_保留失败计数并与登录共用锁定_当_游戏解锁密码错误() throws Exception {
        String email = "lock-" + UUID.randomUUID() + "@example.com";
        String password = "Passw0rd-lockout";
        MockHttpSession session = register(email, password);
        int childId = createChild(session, "小明");
        selectChild(session, childId);

        for (int i = 1; i <= 5; i++) {
            mockMvc.perform(unlock(session, "wrong-password"))
                    .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value(4032));
            assertEquals(i, failedAttempts(email), "外层事务回滚后计数应保留");
        }
        assertEquals(0, jdbc.queryForObject(
                "SELECT COUNT(*) FROM daily_study_time WHERE user_id = ? AND parent_unlocked = 1", Integer.class, childId));

        mockMvc.perform(unlock(session, password))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(4012));
        mockMvc.perform(post("/api/v1/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", password))))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(4012));
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder unlock(
            MockHttpSession session, String password) throws Exception {
        return post("/api/v1/games/unlock").session(session).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(json(Map.of("password", password)));
    }

    private int failedAttempts(String email) {
        return jdbc.queryForObject("SELECT failed_attempts FROM guardian_credential WHERE email = ?", Integer.class, email);
    }
}
