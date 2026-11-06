package com.englishapp.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 游客（未登录）访问：公开接口 200 且无进度；其余接口 401；写接口缺 CSRF 403 */
class GuestAccessTest extends IntegrationTestBase {

    @Autowired
    private JdbcTemplate jdbc;

    private int minId(String table) {
        return jdbc.queryForObject("SELECT MIN(id) FROM " + table, Integer.class);
    }

    @Test
    void should_返回200且进度为0_当_游客查看主题单元() throws Exception {
        int themeId = jdbc.queryForObject("SELECT MIN(theme_id) FROM unit", Integer.class);
        mockMvc.perform(get("/api/v1/units/theme/{id}", themeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].completedLessons").value(0));
    }

    @Test
    void should_返回200默认进度_当_游客查看课时进度() throws Exception {
        mockMvc.perform(get("/api/v1/progress/lesson/{id}", minId("lesson")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stars").value(0));
    }

    @Test
    void should_返回200_当_游客查看单元进度() throws Exception {
        mockMvc.perform(get("/api/v1/progress/unit/{id}", minId("unit")))
                .andExpect(status().isOk());
    }

    @Test
    void should_返回200_当_游客查看今日任务() throws Exception {
        mockMvc.perform(get("/api/v1/learning/today")).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/progress/learned", "/api/v1/progress/learned/stats",
            "/api/v1/wrong-answers", "/api/v1/wrong-answers/stats",
            "/api/v1/characters/progress", "/api/v1/characters/review",
            "/api/v1/games/access", "/api/v1/parent-settings/week",
            "/api/v1/child-growth/profile", "/api/v1/child-growth/measurements",
            "/api/v1/child-growth/photos"})
    void should_返回401_当_游客访问需登录读接口(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void should_返回401_当_游客提交答题() throws Exception {
        mockMvc.perform(post("/api/v1/learning/attempts").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\":\"g-1\",\"lessonId\":1,\"questionIndex\":0,\"firstCorrect\":true,\"assistedCorrect\":false}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_判分通过但不落库_当_游客全对提交复习() throws Exception {
        int lessonId = jdbc.queryForObject("SELECT MIN(id) FROM lesson WHERE type = 'QUIZ'", Integer.class);
        String content = jdbc.queryForObject("SELECT content FROM lesson WHERE id = ?", String.class, lessonId);
        var answers = new java.util.ArrayList<Integer>();
        for (var item : new com.fasterxml.jackson.databind.ObjectMapper().readTree(content).path("items")) {
            answers.add(item.path("answer").asInt());
        }
        int before = jdbc.queryForObject("SELECT COUNT(*) FROM lesson_study_session", Integer.class);

        mockMvc.perform(post("/api/v1/lessons/{id}/review", lessonId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(java.util.Map.of("answers", answers))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.passed").value(true));

        org.junit.jupiter.api.Assertions.assertEquals(before,
                jdbc.queryForObject("SELECT COUNT(*) FROM lesson_study_session", Integer.class));
    }

    @Test
    void should_返回403_当_游客提交复习缺CSRF() throws Exception {
        int lessonId = jdbc.queryForObject("SELECT MIN(id) FROM lesson WHERE type = 'QUIZ'", Integer.class);
        mockMvc.perform(post("/api/v1/lessons/{id}/review", lessonId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"answers\":[0]}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));
    }

    @Test
    void should_返回403_当_写请求缺CSRF() throws Exception {
        mockMvc.perform(post("/api/v1/learning/attempts")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));
    }
}
