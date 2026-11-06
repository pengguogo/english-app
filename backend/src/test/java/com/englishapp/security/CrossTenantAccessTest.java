package com.englishapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 两个家庭 × 各两个孩子的交叉读写。
 * A 的数据通过 JPA 仓库预置（日期格式与业务代码一致，SQLite 方言按毫秒存 LocalDate），
 * 再用其他身份按 ID 访问：跨孩子/跨家庭一律 404 且 message 来自隔离逻辑，A 的数据不变。
 */
class CrossTenantAccessTest extends IntegrationTestBase {

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private com.englishapp.repository.LessonStudySessionRepository studySessions;
    @Autowired
    private com.englishapp.repository.WrongAnswerRepository wrongAnswers;
    @Autowired
    private com.englishapp.repository.GrowthMeasurementRepository measurements;
    @Autowired
    private com.englishapp.repository.ChildPhotoRepository photos;
    @org.springframework.beans.factory.annotation.Value("${app.child-photo.dir}")
    private String photoDir;

    private Actor a1;   // 家庭 1 · 孩子 A（数据拥有者）
    private Actor a2;   // 家庭 1 · 孩子 A 的兄弟姐妹（独立会话）
    private Actor b1;   // 家庭 2 · 孩子 1
    private Actor b2;   // 家庭 2 · 孩子 2
    private int quizLessonId;

    @BeforeEach
    void setUp() throws Exception {
        a1 = newFamilyWithChild("A1");
        a2 = newSessionForSibling(a1, "A2");
        b1 = newFamilyWithChild("B1");
        b2 = newSessionForSibling(b1, "B2");
        quizLessonId = jdbc.queryForObject("SELECT MIN(id) FROM lesson WHERE type = 'QUIZ'", Integer.class);
    }

    /** 同一家庭的第二个孩子，用独立会话登录后选中，避免和 a1 共用 Session 互相覆盖 */
    private Actor newSessionForSibling(Actor parent, String name) throws Exception {
        int childId = createChild(parent.session(), name);
        var session = loginAs(parent);
        selectChild(session, childId);
        return new Actor(session, parent.familyId(), childId, parent.email(), parent.password());
    }

    /** 依次断言非拥有者访问返回 404，且 message 证明来自归属校验而非其他异常 */
    private void assertAllNotFound(String message, MockHttpServletRequestBuilderFactory request) throws Exception {
        for (Actor other : List.of(a2, b1, b2)) {
            verifyParent(other);
            mockMvc.perform(other.with(request.build()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(message));
        }
    }

    @FunctionalInterface
    interface MockHttpServletRequestBuilderFactory {
        MockHttpServletRequestBuilder build();
    }

    // ---------- 错题 ----------

    private int insertWrongAnswer(Actor owner) {
        var wa = new com.englishapp.domain.WrongAnswer();
        wa.setUserId(owner.childId());
        wa.setLessonId(quizLessonId);
        wa.setQuestionIndex(0);
        wa.setQuestionType("QUIZ");
        wa.setWrongCount(1);
        wa.setIsResolved(false);
        var now = java.time.LocalDateTime.now();
        wa.setLastWrongAt(now);
        wa.setCreatedAt(now);
        wa.setUpdatedAt(now);
        return wrongAnswers.save(wa).getId();
    }

    @Test
    void should_返回404且不改数据_当_他人标记错题已掌握() throws Exception {
        int id = insertWrongAnswer(a1);
        assertAllNotFound("错题不存在", () -> patch("/api/v1/wrong-answers/{id}/resolve", id));
        assertEquals(Boolean.FALSE, jdbc.queryForObject("SELECT is_resolved FROM wrong_answer WHERE id=?", Boolean.class, id));
    }

    @Test
    void should_返回404且不删数据_当_他人删除错题() throws Exception {
        int id = insertWrongAnswer(a1);
        assertAllNotFound("错题不存在", () -> delete("/api/v1/wrong-answers/{id}", id));
        assertEquals(1, count("wrong_answer", id));
    }

    @Test
    void should_只看到自己的错题_当_查询错题列表() throws Exception {
        insertWrongAnswer(a1);
        mockMvc.perform(a1.with(get("/api/v1/wrong-answers"))).andExpect(jsonPath("$.data.length()").value(1));
        for (Actor other : List.of(a2, b1, b2)) {
            mockMvc.perform(other.with(get("/api/v1/wrong-answers"))).andExpect(jsonPath("$.data.length()").value(0));
        }
    }

    // ---------- 测量记录 ----------

    private int insertMeasurement(Actor owner) {
        var m = new com.englishapp.domain.GrowthMeasurement();
        m.setUserId(owner.childId());
        m.setMeasuredAt(java.time.LocalDate.of(2026, 1, 1));
        m.setHeightCm(new java.math.BigDecimal("110.0"));
        m.setCreatedAt(java.time.LocalDateTime.now());
        return measurements.save(m).getId();
    }

    @Test
    void should_返回404_当_他人修改测量记录() throws Exception {
        int id = insertMeasurement(a1);
        assertAllNotFound("测量记录不存在", () -> put("/api/v1/child-growth/measurements/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"measuredAt\":\"2026-01-02\",\"heightCm\":120.0}"));
        assertEquals(0, new java.math.BigDecimal("110.0").compareTo(
                jdbc.queryForObject("SELECT height_cm FROM growth_measurement WHERE id=?", java.math.BigDecimal.class, id)));
    }

    @Test
    void should_返回404_当_他人删除测量记录() throws Exception {
        int id = insertMeasurement(a1);
        assertAllNotFound("测量记录不存在", () -> delete("/api/v1/child-growth/measurements/{id}", id));
        assertEquals(1, count("growth_measurement", id));
    }

    // ---------- 照片 ----------

    /** 预置照片：走 JPA 保存记录，并在照片目录写入对应文件，保证拥有者能真实读到 */
    private int insertPhoto(Actor owner) throws Exception {
        String fileName = UUID.randomUUID() + ".jpg";
        var dir = java.nio.file.Path.of(photoDir).toAbsolutePath().normalize();
        java.nio.file.Files.createDirectories(dir);
        java.nio.file.Files.write(dir.resolve(fileName), new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xD9});
        var p = new com.englishapp.domain.ChildPhoto();
        p.setUserId(owner.childId());
        p.setTakenAt(java.time.LocalDate.of(2026, 1, 1));
        p.setFileName(fileName);
        p.setContentType("image/jpeg");
        p.setCreatedAt(java.time.LocalDateTime.now());
        return photos.save(p).getId();
    }

    @Test
    void should_返回404_当_他人读取照片原图和缩略图() throws Exception {
        int id = insertPhoto(a1);
        assertAllNotFound("照片不存在", () -> get("/api/v1/child-growth/photos/{id}/image", id));
        assertAllNotFound("照片不存在", () -> get("/api/v1/child-growth/photos/{id}/thumbnail", id));
        // 正向对照：拥有者能读到原图
        verifyParent(a1);
        mockMvc.perform(a1.with(get("/api/v1/child-growth/photos/{id}/image", id))).andExpect(status().isOk());
    }

    @Test
    void should_返回404_当_他人修改或删除照片() throws Exception {
        int id = insertPhoto(a1);
        assertAllNotFound("照片不存在", () -> put("/api/v1/child-growth/photos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON).content("{\"takenAt\":\"2026-01-02\",\"caption\":\"x\"}"));
        assertAllNotFound("照片不存在", () -> delete("/api/v1/child-growth/photos/{id}", id));
        assertEquals(1, count("child_photo", id));
    }

    @Test
    void should_整体404_当_打包照片中含他人ID() throws Exception {
        int foreign = insertPhoto(a1);
        int own = insertPhoto(b1);
        verifyParent(b1);
        mockMvc.perform(b1.with(get("/api/v1/child-growth/photos/zip").param("ids", own + "," + foreign)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("照片不存在"));

        // 正向对照：只传自己的 id 时能拿到含 1 个条目的 zip
        var started = mockMvc.perform(b1.with(get("/api/v1/child-growth/photos/zip").param("ids", String.valueOf(own))))
                .andExpect(request().asyncStarted()).andReturn();
        byte[] zip = mockMvc.perform(asyncDispatch(started))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        int entries = 0;
        try (var in = new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(zip))) {
            while (in.getNextEntry() != null) entries++;
        }
        assertEquals(1, entries);
    }

    @Test
    void should_只看到自己的照片_当_查询照片列表() throws Exception {
        insertPhoto(a1);
        for (Actor other : List.of(a2, b1, b2)) {
            verifyParent(other);
            mockMvc.perform(other.with(get("/api/v1/child-growth/photos"))).andExpect(jsonPath("$.data.length()").value(0));
        }
    }

    // ---------- 档案 ----------

    @Test
    void should_只读写当前孩子档案_当_访问档案() throws Exception {
        verifyParent(b1);
        mockMvc.perform(b1.with(put("/api/v1/child-growth/profile"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nickname\":\"改名\"}"))
                .andExpect(status().isOk());
        assertEquals("A1", jdbc.queryForObject("SELECT nickname FROM app_user WHERE id=?", String.class, a1.childId()));
        assertEquals("改名", jdbc.queryForObject("SELECT nickname FROM app_user WHERE id=?", String.class, b1.childId()));
    }

    // ---------- 选孩子 ----------

    @Test
    void should_返回404_当_选择其他家庭的孩子() throws Exception {
        mockMvc.perform(b1.with(put("/api/v1/children/current"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":" + a1.childId() + "}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("孩子不存在"));
    }

    @Test
    void should_只列出本家庭孩子_当_查询孩子列表() throws Exception {
        mockMvc.perform(b1.with(get("/api/v1/children"))).andExpect(jsonPath("$.data.length()").value(2));
    }

    // ---------- 幂等 eventId ----------

    @Test
    void should_返回400且不改原记录_当_他人复用答题eventId() throws Exception {
        String eventId = UUID.randomUUID().toString();
        String body = "{\"eventId\":\"" + eventId + "\",\"lessonId\":" + quizLessonId
                + ",\"questionIndex\":0,\"firstCorrect\":false,\"assistedCorrect\":false}";
        mockMvc.perform(a1.with(post("/api/v1/learning/attempts")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        for (Actor other : List.of(a2, b1)) {
            mockMvc.perform(other.with(post("/api/v1/learning/attempts")).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        assertEquals(a1.childId(), jdbc.queryForObject("SELECT user_id FROM learning_attempt WHERE id=?", Integer.class, eventId));
    }

    @Test
    void should_返回400且不改原记录_当_他人复用学习时长eventId() throws Exception {
        String eventId = UUID.randomUUID().toString();
        String body = "{\"eventId\":\"" + eventId + "\",\"lessonId\":" + quizLessonId + ",\"seconds\":10}";
        mockMvc.perform(a1.with(post("/api/v1/games/study-time")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mockMvc.perform(b1.with(post("/api/v1/games/study-time")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertEquals(a1.childId(), jdbc.queryForObject("SELECT user_id FROM study_time_event WHERE id=?", Integer.class, eventId));
    }

    @Test
    void should_各自写入_当_两个孩子使用同一识字eventId() throws Exception {
        // V87 起 character_attempt 按 (user_id, event_id) 联合唯一
        var item = jdbc.queryForMap("SELECT l.id FROM lesson l JOIN unit u ON u.id = l.unit_id "
                + "WHERE u.theme_id = 10 AND l.type = 'WORD' ORDER BY l.id LIMIT 1");
        String body = "{\"eventId\":\"same-char-event\",\"lessonId\":" + item.get("id") + ",\"itemIndex\":0,\"outcome\":\"INDEPENDENT\"}";
        for (Actor actor : List.of(a1, b1)) {
            mockMvc.perform(actor.with(post("/api/v1/characters/attempts")).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk());
        }
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM character_attempt WHERE event_id='same-char-event'", Integer.class));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM character_attempt WHERE event_id='same-char-event' AND user_id=?",
                Integer.class, a1.childId()));
    }

    // ---------- 进度（按 lessonId 写，按当前孩子读） ----------

    @Test
    void should_不影响他人进度_当_完成课时() throws Exception {
        // 完成课时须当天复习已通过（ProgressServiceImpl 业务规则），预置 A 的通过记录
        // 走 JPA 保存，日期格式与业务代码一致（SQLite 方言按毫秒时间戳存 LocalDate）
        var session = new com.englishapp.domain.LessonStudySession();
        session.setUserId(a1.childId());
        session.setLessonId(quizLessonId);
        session.setStudyDate(java.time.LocalDate.now());
        session.setPendingSeconds(0);
        session.setPassed(true);
        studySessions.save(session);
        mockMvc.perform(a1.with(post("/api/v1/progress/lesson/{id}/complete", quizLessonId))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"stars\":3,\"score\":100}"))
                .andExpect(status().isOk());
        mockMvc.perform(a1.with(get("/api/v1/progress/lesson/{id}", quizLessonId))).andExpect(jsonPath("$.data.stars").value(3));
        for (Actor other : List.of(a2, b1, b2)) {
            mockMvc.perform(other.with(get("/api/v1/progress/lesson/{id}", quizLessonId))).andExpect(jsonPath("$.data.stars").value(0));
            mockMvc.perform(other.with(get("/api/v1/progress/learned"))).andExpect(jsonPath("$.data.length()").value(0));
        }
    }

    private int count(String table, int id) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + table + " WHERE id=?", Integer.class, id);
    }
}
