package com.englishapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

class PreschoolPicturebookContentTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void should_提供三本无字化听读绘本_当_安装V73迁移() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite::memory:");
             var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE theme(id INTEGER PRIMARY KEY, name TEXT, icon_url TEXT, sort_order INT, is_locked INT, subject_id INT)");
            statement.execute("CREATE TABLE unit(id INTEGER PRIMARY KEY, theme_id INT, name TEXT, sort_order INT, is_locked INT)");
            statement.execute("CREATE TABLE lesson(id INTEGER PRIMARY KEY, unit_id INT, name TEXT, type TEXT, content TEXT, sort_order INT, star_reward INT)");
            executeMigration(statement, "/db/migration/V72__add_picturebook_train.sql");
            executeMigration(statement, "/db/migration/V73__add_preschool_picturebooks.sql");

            var units = statement.executeQuery("SELECT COUNT(*) FROM unit");
            assertThat(units.getInt(1)).isEqualTo(4);
            var rows = statement.executeQuery("SELECT type, content FROM lesson ORDER BY id");
            int lessons = 0;
            int newItems = 0;
            while (rows.next()) {
                var root = mapper.readTree(rows.getString("content"));
                PicturebookContentValidator.validate(root);
                if (!"溪边秋千 · 绘本1".equals(root.path("bookTitle").asText())) {
                    newItems += root.path("items").size();
                    for (var item : root.path("items")) {
                        assertThat(getClass().getResource("/static/images/" + item.path("image").asText() + ".jpg")).isNotNull();
                        if ("READING".equals(rows.getString("type"))) {
                            assertThat(item.path("content").asText().split("\\s+").length).isLessThanOrEqualTo(6);
                        } else {
                            for (var option : item.path("options")) {
                                assertThat(option.path("showText").asBoolean(true)).isFalse();
                                assertThat(getClass().getResource("/static/images/" + option.path("image").asText() + ".jpg")).isNotNull();
                            }
                        }
                    }
                }
                lessons++;
            }
            assertThat(lessons).isEqualTo(12);
            assertThat(newItems).isEqualTo(36);
        }
    }

    private void executeMigration(java.sql.Statement statement, String path) throws Exception {
        try (var input = getClass().getResourceAsStream(path)) {
            assertThat(input).isNotNull();
            for (String sql : new String(input.readAllBytes(), StandardCharsets.UTF_8).split(";")) {
                if (!sql.isBlank()) statement.execute(sql);
            }
        }
    }
}
