package com.englishapp.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * 在 english.db 副本上跑 Flyway，确认 user_id=1 的存量数据完整归入默认家庭。
 * 只读原库，所有写入都发生在临时副本上。
 */
class FamilyMigrationTest {

    /** 带 user_id 的业务表（来自 V85 时的 english.db） */
    private static final String[] USER_TABLES = {
            "user_progress", "wrong_answer", "daily_study_time", "growth_measurement",
            "child_photo", "lesson_study_session", "character_progress", "character_attempt",
            "study_time_event", "learning_attempt"};

    @TempDir
    Path tempDir;

    @Test
    void should_保留存量行数并归入默认家庭_当_在库副本上迁移() throws Exception {
        Path source = Path.of("english.db");
        assumeTrue(Files.exists(source), "english.db 不存在，跳过");
        Path copy = tempDir.resolve("english-copy.db");
        Files.copy(source, copy);
        String url = "jdbc:sqlite:" + copy;

        Map<String, Long> before = countUserOneRows(url);

        Flyway.configure().dataSource(url, null, null)
                .locations("classpath:db/migration").outOfOrder(true)
                .load().migrate();

        Map<String, Long> after = countUserOneRows(url);
        assertEquals(before, after, "迁移前后 user_id=1 各表行数应一致");

        try (Connection c = DriverManager.getConnection(url); var st = c.createStatement()) {
            try (var rs = st.executeQuery("SELECT COUNT(*) FROM family WHERE id = 1")) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1), "应建默认家庭 id=1");
            }
            try (var rs = st.executeQuery("SELECT family_id FROM app_user WHERE id = 1")) {
                assertTrue(rs.next(), "app_user.id=1 应保留");
                assertEquals(1L, rs.getLong(1), "app_user.id=1 应归入默认家庭");
            }
            try (var rs = st.executeQuery("SELECT COUNT(*) FROM app_user WHERE family_id IS NULL")) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1), "不应有未归属家庭的孩子档案");
            }
        }
    }

    private Map<String, Long> countUserOneRows(String url) throws Exception {
        Map<String, Long> counts = new LinkedHashMap<>();
        try (Connection c = DriverManager.getConnection(url); var st = c.createStatement()) {
            for (String table : USER_TABLES) {
                try (var rs = st.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE user_id = 1")) {
                    rs.next();
                    counts.put(table, rs.getLong(1));
                }
            }
            try (var rs = st.executeQuery("SELECT COUNT(*) FROM app_user WHERE id = 1")) {
                rs.next();
                counts.put("app_user", rs.getLong(1));
            }
        }
        return counts;
    }
}
