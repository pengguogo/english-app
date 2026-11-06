package com.englishapp.security;

import com.englishapp.controller.LessonReviewController;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 游客 POST 复习只放行这一条路径；CSRF 仍校验，邻近写接口仍需登录 */
class GuestReviewSecurityTest extends IntegrationTestBase {

    @Test
    void should_通过安全层到达Controller_当_游客提交复习() throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/lessons/{id}/review", 1).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"answers\":[0]}"))
                .andExpect(handler().handlerType(LessonReviewController.class))
                .andReturn();
        // 安全层不拦；结果由 Controller 游客分支决定（ea-isolate 实现前可能仍是 401 业务异常）
        assertNotEquals(403, r.getResponse().getStatus());
    }

    @Test
    void should_返回403_当_游客提交复习缺CSRF() throws Exception {
        mockMvc.perform(post("/api/v1/lessons/{id}/review", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"answers\":[0]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_仍返回401_当_游客调用其他写接口() throws Exception {
        mockMvc.perform(post("/api/v1/progress/lesson/{id}/complete", 1).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"stars\":3,\"score\":100}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/lessons/{id}/review", 1).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/lessons/{id}/review/extra", 1).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
