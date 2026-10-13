package com.englishapp.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import static org.junit.jupiter.api.Assertions.*;

class PendingStudyTimeMigrationTest {
    @Test
    void should_按原日期补记且不重复累计_当_迁移旧暂存时长() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite::memory:");
             var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE app_user (id INTEGER PRIMARY KEY)");
            statement.execute("INSERT INTO app_user VALUES (1), (2)");
            executeResource(statement, "V77__add_daily_study_time.sql");
            executeResource(statement, "V81__add_lesson_review.sql");
            statement.execute("ALTER TABLE daily_study_time ADD COLUMN parent_unlocked BOOLEAN NOT NULL DEFAULT FALSE");
            statement.execute("INSERT INTO daily_study_time (user_id, study_date, seconds, parent_unlocked) VALUES (1, '2026-10-12', 240, TRUE)");
            statement.execute("INSERT INTO lesson_study_session (user_id, lesson_id, study_date, pending_seconds, passed) VALUES "
                    + "(1, 7, '2026-10-12', 40, FALSE), (1, 8, '2026-10-12', 50, FALSE), "
                    + "(1, 7, '2026-10-13', 25, FALSE), (2, 7, '2026-10-12', 15, FALSE), "
                    + "(1, 9, '2026-10-12', 0, TRUE)");

            executeResource(statement, "V85__credit_pending_study_time.sql");
            executeResource(statement, "V85__credit_pending_study_time.sql");

            try (var rows = statement.executeQuery("SELECT user_id, study_date, seconds, parent_unlocked FROM daily_study_time ORDER BY user_id, study_date")) {
                assertTrue(rows.next());
                assertEquals("2026-10-12", rows.getString("study_date"));
                assertEquals(300, rows.getInt("seconds"));
                assertTrue(rows.getBoolean("parent_unlocked"));
                assertTrue(rows.next());
                assertEquals("2026-10-13", rows.getString("study_date"));
                assertEquals(25, rows.getInt("seconds"));
                assertTrue(rows.next());
                assertEquals(2, rows.getInt("user_id"));
                assertEquals(15, rows.getInt("seconds"));
                assertFalse(rows.next());
            }
            try (var rows = statement.executeQuery("SELECT SUM(pending_seconds), SUM(passed) FROM lesson_study_session")) {
                assertTrue(rows.next());
                assertEquals(0, rows.getInt(1));
                assertEquals(1, rows.getInt(2));
            }
        }
    }

    private void executeResource(java.sql.Statement statement, String name) throws Exception {
        var resource = new ClassPathResource("db/migration/" + name);
        String sql;
        try (var stream = resource.getInputStream()) {
            sql = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        for (String command : sql.split(";")) {
            if (!command.isBlank()) statement.execute(command);
        }
    }
}
