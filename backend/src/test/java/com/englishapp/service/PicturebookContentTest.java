package com.englishapp.service;

import com.englishapp.config.WordImageProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import static org.assertj.core.api.Assertions.*;

/** 验证真实迁移、扩展字段和本地素材，避免只测试前端硬编码示例。 */
class PicturebookContentTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void should_保留原文和有效配图_当_安装绘本迁移() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite::memory:");
             var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE theme(id INTEGER PRIMARY KEY, name TEXT, icon_url TEXT, sort_order INT, is_locked INT, subject_id INT)");
            statement.execute("CREATE TABLE unit(id INTEGER PRIMARY KEY, theme_id INT, name TEXT, sort_order INT, is_locked INT)");
            statement.execute("CREATE TABLE lesson(id INTEGER PRIMARY KEY, unit_id INT, name TEXT, type TEXT, content TEXT, sort_order INT, star_reward INT)");
            try (var input = getClass().getResourceAsStream("/db/migration/V72__add_picturebook_train.sql")) {
                assertThat(input).isNotNull();
                for (String sql : new String(input.readAllBytes(), StandardCharsets.UTF_8).split(";")) {
                    if (!sql.isBlank()) statement.execute(sql);
                }
            }
            var rows = statement.executeQuery("SELECT type, content FROM lesson ORDER BY sort_order");
            int count = 0;
            int itemCount = 0;
            while (rows.next()) {
                var root = mapper.readTree(rows.getString("content"));
                PicturebookContentValidator.validate(root);
                assertThat(root.path("type").asText()).isEqualTo(rows.getString("type"));
                for (var item : root.path("items")) {
                    assertThat(getClass().getResource("/static/images/" + item.path("image").asText() + ".jpg")).isNotNull();
                    for (var option : item.path("options")) {
                        if (option.has("image")) assertThat(getClass().getResource("/static/images/" + option.path("image").asText() + ".jpg")).isNotNull();
                    }
                    itemCount++;
                }
                if (count == 0) {
                    assertThat(root.path("items").size()).isEqualTo(13);
                    assertThat(root.path("items").get(12).path("content").asText()).isEqualTo("Splash! Oh no!");
                }
                var resolved = new WordImageResolver(new WordImageProperties(), mapper).resolveContent(rows.getString("content"));
                assertThat(resolved).contains("/images/picturebooks/").contains("\"picturebook\":true");
                count++;
            }
            assertThat(count).isEqualTo(3);
            assertThat(itemCount).isEqualTo(26);
        }
    }

    @Test
    void should_拒绝越界答案和缺失配图_当_绘本数据损坏() throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree("""
                {"type":"QUIZ","picturebook":true,"bookTitle":"测试绘本","items":[
                {"image":"picturebooks/train-station","audioLanguage":"en","audioText":"Stop!","options":["Go","Stop"],"answer":2}]}
                """);
        assertThatThrownBy(() -> PicturebookContentValidator.validate(root)).isInstanceOf(IllegalArgumentException.class);
        ObjectNode item = (ObjectNode) root.path("items").get(0);
        item.put("answer", 1);
        assertThatCode(() -> PicturebookContentValidator.validate(root)).doesNotThrowAnyException();
        item.put("image", "");
        assertThatThrownBy(() -> PicturebookContentValidator.validate(root)).isInstanceOf(IllegalArgumentException.class);
        item.put("image", "picturebooks/train-station");
        item.put("trainAction", "unknown");
        assertThatThrownBy(() -> PicturebookContentValidator.validate(root)).isInstanceOf(IllegalArgumentException.class);
        item.remove("trainAction");
        item.put("audioLanguage", "zh");
        assertThatThrownBy(() -> PicturebookContentValidator.validate(root)).isInstanceOf(IllegalArgumentException.class);
    }
}
