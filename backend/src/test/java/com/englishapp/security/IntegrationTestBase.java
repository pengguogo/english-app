package com.englishapp.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * 集成测试基类：每个测试类一个临时 SQLite 文件，真实跑 Flyway（含 V86+）。
 * 不用 test profile 的 H2，保证迁移和 SQLite 方言都被覆盖。
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    static void sqlite(DynamicPropertyRegistry registry) throws Exception {
        Path db = Files.createTempFile("ea-it-", ".db");
        db.toFile().deleteOnExit();
        registry.add("spring.datasource.url", () -> "jdbc:sqlite:" + db);
        registry.add("spring.datasource.driver-class-name", () -> "org.sqlite.JDBC");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.community.dialect.SQLiteDialect");
        registry.add("spring.jpa.show-sql", () -> "false");
        registry.add("spring.flyway.enabled", () -> "true");
        // 路径只生成一次：Supplier 每次读取都会被调用，内联 randomUUID 会导致各处拿到不同目录
        String photoDir = db.getParent().resolve("ea-it-photos-" + UUID.randomUUID()).toString();
        registry.add("app.child-photo.dir", () -> photoDir);
    }

    /** 已登录且已选孩子的身份，所有请求自动带 Session 与 CSRF */
    public record Actor(MockHttpSession session, long familyId, int childId, String email, String password) {
        public MockHttpServletRequestBuilder with(MockHttpServletRequestBuilder b) {
            return b.session(session).with(csrf());
        }
    }

    /** 注册家庭 → 登录 → 建孩子 → 选孩子 */
    protected Actor newFamilyWithChild(String childName) throws Exception {
        String email = "it-" + UUID.randomUUID() + "@example.com";
        String password = "Passw0rd!" + UUID.randomUUID();
        MockHttpSession session = register(email, password);
        int childId = createChild(session, childName);
        selectChild(session, childId);
        return new Actor(session, familyIdOf(session), childId, email, password);
    }

    /** 用同一家长账号开一个新会话（未选孩子） */
    protected MockHttpSession loginAs(Actor parent) throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/v1/auth/login").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", parent.email(), "password", parent.password()))))
                .andReturn();
        return session;
    }

    /** 家长二次验证（10 分钟有效） */
    protected void verifyParent(Actor actor) throws Exception {
        mockMvc.perform(post("/api/v1/auth/parent-verify").session(actor.session()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("password", actor.password()))))
                .andReturn();
    }

    protected MockHttpSession register(String email, String password) throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/v1/auth/register").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", password))))
                .andReturn();
        mockMvc.perform(post("/api/v1/auth/login").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", password))))
                .andReturn();
        return session;
    }

    protected int createChild(MockHttpSession session, String nickname) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/children").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nickname", nickname))))
                .andReturn();
        return data(r).path("id").asInt();
    }

    protected void selectChild(MockHttpSession session, int childId) throws Exception {
        mockMvc.perform(put("/api/v1/children/current").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("childId", childId))))
                .andReturn();
    }

    protected long familyIdOf(MockHttpSession session) throws Exception {
        MvcResult r = mockMvc.perform(get("/api/v1/auth/me").session(session)).andReturn();
        return data(r).path("familyId").asLong();
    }

    protected JsonNode data(MvcResult r) throws Exception {
        return objectMapper.readTree(r.getResponse().getContentAsString()).path("data");
    }

    protected String json(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }
}
