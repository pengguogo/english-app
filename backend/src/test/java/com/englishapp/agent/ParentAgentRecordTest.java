package com.englishapp.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.englishapp.security.IntegrationTestBase;
import com.englishapp.service.AuthService;
import com.englishapp.service.ai.llm.LlmClient;
import com.englishapp.service.ai.record.AgentRecordService;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

/** 阶段 3：对话与答题落库、脱敏、家长查看与删除、越权交叉、学习状态与复习建议 */
class ParentAgentRecordTest extends IntegrationTestBase {

    @MockBean
    private LlmClient llm;
    @SpyBean
    private AgentRecordService records;
    @Autowired
    private JdbcTemplate jdbc;

    /** 正确答案是 b */
    private static final String QUIZ_JSON =
            "{\"question\":\"霸王龙吃什么？\",\"options\":[\"吃草\",\"吃肉\"],\"answer\":1,\"praise\":\"答对啦\",\"hint\":\"看看它的大牙齿\"}";

    @BeforeEach
    void setUp() {
        reset(llm, records);
        when(llm.enabled()).thenReturn(true);
        when(llm.complete(anyString(), anyString())).thenReturn(new LlmClient.Completion(QUIZ_JSON, 1, 1, 1));
    }

    private Actor consented() throws Exception {
        Actor a = newFamilyWithChild("大宝");
        consent(a);
        return a;
    }

    private void consent(Actor a) throws Exception {
        mockMvc.perform(post("/api/v1/auth/consent").session(a.session()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("version", AuthService.CONSENT_VERSION))))
                .andExpect(status().isOk());
    }

    private JsonNode chat(Actor a, String text, Long sessionId) throws Exception {
        Map<String, Object> body = new HashMap<>(Map.of("text", text));
        if (sessionId != null) body.put("sessionId", sessionId);
        return data(mockMvc.perform(a.with(post("/api/v1/agent/chat")).contentType(MediaType.APPLICATION_JSON)
                .content(json(body))).andExpect(status().isOk()).andReturn());
    }

    private JsonNode answer(Actor a, JsonNode chat, String optionId) throws Exception {
        return data(mockMvc.perform(a.with(post("/api/v1/agent/answer")).contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("quizToken", chat.path("quiz").path("quizToken").asText(),
                        "optionId", optionId, "sessionId", chat.path("sessionId").asLong()))))
                .andExpect(status().isOk()).andReturn());
    }

    private int count(String sql, Object... args) {
        return jdbc.queryForObject(sql, Integer.class, args);
    }

    @Test
    void should_落库三轮对话与首次作答_当_已同意家庭() throws Exception {
        Actor a = consented();
        JsonNode r1 = chat(a, "霸王龙的牙齿", null);
        long sid = r1.path("sessionId").asLong();
        assertTrue(sid > 0);
        // 不存在的选项不记录
        answer(a, r1, "z");
        answer(a, r1, "a");
        // 重选只判分，不改库
        assertTrue(answer(a, r1, "b").path("correct").asBoolean());
        JsonNode r2 = chat(a, "我妈妈电话13812345678", sid);
        JsonNode r3 = chat(a, "霸王龙", sid);
        assertEquals(sid, r2.path("sessionId").asLong());
        assertEquals(sid, r3.path("sessionId").asLong());

        verifyParent(a);
        mockMvc.perform(a.with(get("/api/v1/parent/agent-sessions")))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].turns").value(3))
                .andExpect(jsonPath("$.data.items[0].childName").value("大宝"))
                .andExpect(jsonPath("$.data.items[0].sourcePage").value("FREE"))
                .andExpect(jsonPath("$.data.items[0].createdAt").value(org.hamcrest.Matchers.endsWith("+08:00")))
                .andExpect(jsonPath("$.data.hasMore").value(false));
        JsonNode d = data(mockMvc.perform(a.with(get("/api/v1/parent/agent-sessions/" + sid))).andReturn());
        assertEquals(6, d.path("messages").size());
        assertEquals("CHILD", d.path("messages").get(0).path("role").asText());
        assertTrue(d.path("messages").get(1).path("text").asText().startsWith("【"));
        // PERSONAL_INFO 类输入只存占位语，不存原文
        assertEquals("（孩子分享了个人信息，已隐藏）", d.path("messages").get(2).path("text").asText());
        assertEquals("SAFE_REPLY", d.path("messages").get(3).path("moderation").asText());
        JsonNode first = d.path("interactions").get(0);
        assertEquals("a", first.path("chosen").asText());
        assertFalse(first.path("correct").asBoolean());
        assertEquals("b", first.path("options").get(1).path("id").asText());
        // 第三轮的题目还没作答
        assertTrue(d.path("interactions").get(1).path("chosen").isNull());
        assertEquals(0, count("SELECT COUNT(*) FROM agent_message WHERE text LIKE '%13812345678%'"));
    }

    @Test
    void should_脱敏长数字_当_原话不属于个人信息分类() throws Exception {
        Actor a = consented();
        JsonNode r = chat(a, "一二三 1234567", null);
        assertEquals(1, count("SELECT COUNT(*) FROM agent_message WHERE session_id=? AND role='CHILD'"
                + " AND text NOT LIKE '%1234567%'", r.path("sessionId").asLong()));
    }

    @Test
    void should_不写库_当_游客或未同意家庭() throws Exception {
        int before = count("SELECT COUNT(*) FROM agent_session");
        mockMvc.perform(post("/api/v1/agent/chat").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("text", "霸王龙的牙齿"))))
                .andExpect(jsonPath("$.data.sessionId").doesNotExist());
        Actor notConsented = newFamilyWithChild("小宝");
        assertTrue(chat(notConsented, "霸王龙的牙齿", null).path("sessionId").isMissingNode());
        assertEquals(before, count("SELECT COUNT(*) FROM agent_session"));
    }

    @Test
    void should_标记会话_当_孩子提到陌生人() throws Exception {
        Actor a = consented();
        chat(a, "有个陌生人给我糖吃", null);
        verifyParent(a);
        mockMvc.perform(a.with(get("/api/v1/parent/agent-sessions?flagged=true")))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].flagged").value(true))
                .andExpect(jsonPath("$.data.items[0].flagLabel").value("提到了陌生人"));
        mockMvc.perform(a.with(get("/api/v1/parent/agent-sessions?flagged=false")))
                .andExpect(jsonPath("$.data.items.length()").value(0));
    }

    @Test
    void should_新建会话_当_传入别的孩子的会话id() throws Exception {
        Actor a = consented();
        Actor b = consented();
        long sidA = chat(a, "霸王龙", null).path("sessionId").asLong();
        long sidB = chat(b, "霸王龙", sidA).path("sessionId").asLong();
        assertTrue(sidB != sidA);
        assertEquals(1, count("SELECT COUNT(*) FROM agent_message WHERE session_id=? AND role='CHILD'", sidA));
    }

    @Test
    void should_仍返回回答_当_落库失败() throws Exception {
        Actor a = consented();
        doThrow(new IllegalStateException("db down")).when(records)
                .recordChat(anyLong(), anyInt(), any(), anyString(), any(), any());
        JsonNode r = chat(a, "霸王龙的牙齿", null);
        assertEquals("EXPLAIN", r.path("type").asText());
        assertTrue(r.path("sessionId").isMissingNode());
        doCallRealMethod().when(records).recordChat(anyLong(), anyInt(), any(), anyString(), any(), any());
    }

    @Test
    void should_要求家长验证_当_访问家长接口() throws Exception {
        Actor a = consented();
        mockMvc.perform(a.with(get("/api/v1/parent/agent-sessions")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4031));
    }

    /** 两个家庭 × 两个孩子：只看得到本家庭；别的家庭的 childId 只当筛选条件 → 空；别家会话 → 404 */
    @Test
    void should_只看到本家庭记录_当_两个家庭各两个孩子() throws Exception {
        Actor f1 = consented();
        int f1c2 = createChild(f1.session(), "二宝");
        Actor f2 = consented();
        int f2c2 = createChild(f2.session(), "二宝");
        long s11 = chat(f1, "霸王龙", null).path("sessionId").asLong();
        selectChild(f1.session(), f1c2);
        long s12 = chat(f1, "霸王龙", null).path("sessionId").asLong();
        long s21 = chat(f2, "霸王龙", null).path("sessionId").asLong();
        selectChild(f2.session(), f2c2);
        chat(f2, "霸王龙", null);
        verifyParent(f1);

        mockMvc.perform(f1.with(get("/api/v1/parent/agent-sessions")))
                .andExpect(jsonPath("$.data.items.length()").value(2));
        mockMvc.perform(f1.with(get("/api/v1/parent/agent-sessions?childId=" + f1c2)))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].id").value(s12));
        mockMvc.perform(f1.with(get("/api/v1/parent/agent-sessions?childId=" + f2c2)))
                .andExpect(jsonPath("$.data.items.length()").value(0));
        mockMvc.perform(f1.with(get("/api/v1/parent/agent-sessions?childId=" + f2.childId())))
                .andExpect(jsonPath("$.data.items.length()").value(0));
        mockMvc.perform(f1.with(get("/api/v1/parent/learning-topics?childId=" + f2c2)))
                .andExpect(jsonPath("$.data.length()").value(0));
        mockMvc.perform(f1.with(get("/api/v1/parent/agent-sessions/" + s21)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("记录不存在"));
        mockMvc.perform(f1.with(delete("/api/v1/parent/agent-sessions/" + s21)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("记录不存在"));
        assertEquals(1, count("SELECT COUNT(*) FROM agent_session WHERE id=?", s21));
        mockMvc.perform(f1.with(get("/api/v1/parent/agent-sessions/" + s11)))
                .andExpect(status().isOk());
    }

    /** 两道错题 → PRACTICE + 建议；删除会话 → 消息、答题行数为 0，建议同步删除 */
    @Test
    void should_生成建议并在删除后同步删掉_当_最近答错两次() throws Exception {
        Actor a = consented();
        JsonNode r1 = chat(a, "霸王龙的牙齿", null);
        long sid = r1.path("sessionId").asLong();
        answer(a, r1, "a");
        JsonNode r2 = chat(a, "霸王龙的牙齿", sid);
        answer(a, r2, "a");
        verifyParent(a);

        JsonNode topics = data(mockMvc.perform(a.with(get("/api/v1/parent/learning-topics"))).andReturn());
        assertEquals(1, topics.size());
        JsonNode t = topics.get(0);
        assertEquals("PRACTICE", t.path("status").asText());
        assertEquals(2, t.path("wrong").asInt());
        assertEquals(sid, t.path("sessions").get(0).asLong());
        assertTrue(t.path("themeName").isTextual());
        assertTrue(t.path("suggestion").path("route").asText().startsWith("/lesson/"));
        assertEquals("最近 2 次答题错了 2 次", t.path("suggestion").path("basis").asText());

        mockMvc.perform(a.with(delete("/api/v1/parent/agent-sessions/" + sid)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
        assertEquals(0, count("SELECT COUNT(*) FROM agent_message WHERE session_id=?", sid));
        assertEquals(0, count("SELECT COUNT(*) FROM agent_interaction WHERE session_id=?", sid));
        assertEquals(0, count("SELECT COUNT(*) FROM review_suggestion WHERE child_id=?", a.childId()));
        mockMvc.perform(a.with(get("/api/v1/parent/learning-topics")))
                .andExpect(jsonPath("$.data.length()").value(0));
        mockMvc.perform(a.with(get("/api/v1/parent/agent-sessions/" + sid)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("记录不存在"));
    }
}
