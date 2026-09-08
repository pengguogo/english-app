package com.englishapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

class TrainPicturebookContentTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void should_提供火车主题听读绘本_当_安装V74迁移() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite::memory:");
             var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE theme(id INTEGER PRIMARY KEY, name TEXT, icon_url TEXT, sort_order INT, is_locked INT, subject_id INT)");
            statement.execute("CREATE TABLE unit(id INTEGER PRIMARY KEY, theme_id INT, name TEXT, sort_order INT, is_locked INT)");
            statement.execute("CREATE TABLE lesson(id INTEGER PRIMARY KEY, unit_id INT, name TEXT, type TEXT, content TEXT, sort_order INT, star_reward INT)");
            executeMigration(statement, "/db/migration/V72__add_picturebook_train.sql");
            executeMigration(statement, "/db/migration/V73__add_preschool_picturebooks.sql");
            executeMigration(statement, "/db/migration/V74__add_apple_train_picturebook.sql");

            var rows = statement.executeQuery("""
                    SELECT lesson.type, lesson.content
                    FROM lesson JOIN unit ON unit.id = lesson.unit_id
                    WHERE unit.name = '苹果坐上小火车'
                    ORDER BY lesson.sort_order
                    """);
            int lessonCount = 0;
            int itemCount = 0;
            while (rows.next()) {
                var root = mapper.readTree(rows.getString("content"));
                PicturebookContentValidator.validate(root);
                assertThat(root.path("bookTitle").asText()).isEqualTo("苹果坐上小火车");
                for (var item : root.path("items")) {
                    itemCount++;
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
                lessonCount++;
            }
            assertThat(lessonCount).isEqualTo(3);
            assertThat(itemCount).isEqualTo(12);
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
