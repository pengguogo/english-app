package com.englishapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

class GradeOneCharacterMigrationTest {
    @TempDir Path directory;

    @Test
    void should_补齐教材280字并保留旧记录_当_升级SQLite课程库() throws Exception {
        var source = new DriverManagerDataSource("jdbc:sqlite:" + directory.resolve("grade-one.db"));
        var jdbc = new JdbcTemplate(source);
        jdbc.execute("CREATE TABLE unit(id INTEGER PRIMARY KEY,theme_id INTEGER,name TEXT,sort_order INTEGER,is_locked INTEGER)");
        jdbc.execute("CREATE TABLE lesson(id INTEGER PRIMARY KEY,unit_id INTEGER,name TEXT,type TEXT,content TEXT,sort_order INTEGER,star_reward INTEGER)");
        jdbc.execute("INSERT INTO unit VALUES(20,10,'自然汉字',1,0),(23,10,'生活汉字',2,0)");
        for (int id = 68; id <= 73; id++) jdbc.update("INSERT INTO lesson(id,unit_id,type) VALUES (?,?,'WORD')", id, id < 71 ? 20 : 23);
        migrate(source, "V82__strengthen_character_recognition.sql");
        jdbc.execute("INSERT INTO character_progress(user_id,word,independent_days,last_outcome,due_date) VALUES(1,'山',2,'INDEPENDENT','2026-10-11')");
        migrate(source, "V83__complete_grade_one_character_recognition.sql");
        var words = new HashSet<String>();
        int notes = 0;
        for (var content : jdbc.queryForList("SELECT content FROM lesson", String.class)) {
            var root = new ObjectMapper().readTree(content);
            assertEquals("WORD", root.path("type").asText());
            var items = root.path("items");
            assertTrue(items.size() >= 2 && items.size() <= 4);
            for (var item : items) {
                var word = item.path("word").asText();
                assertEquals(1, word.codePointCount(0, word.length()));
                assertTrue(words.add(word), "重复字：" + word);
                assertTrue(item.path("recognition").asBoolean());
                assertFalse(item.path("phonetic").asText().isBlank());
                assertTrue(item.path("exampleWord").asText().contains(word));
                assertTrue(item.path("exampleSentence").asText().contains(word));
                if (!item.path("readingNote").asText().isBlank()) notes++;
                var image = new ClassPathResource("static/images/" + item.path("image").asText() + ".jpg");
                try (var stream = image.getInputStream()) {
                    var decoded = ImageIO.read(stream);
                    assertNotNull(decoded, "无法读取配图：" + word);
                    assertTrue(decoded.getWidth() > 100 && decoded.getHeight() > 100);
                }
            }
        }
        var expected = new ClassPathResource("grade-one-2024-characters.txt");
        try (var stream = expected.getInputStream()) {
            var text = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).strip();
            assertEquals(280, text.length());
            assertEquals(new HashSet<>(text.chars().mapToObj(c -> String.valueOf((char)c)).toList()), words);
        }
        assertEquals(4, notes);
        assertEquals(2, jdbc.queryForObject("SELECT independent_days FROM character_progress WHERE word='山'", Integer.class));
        assertEquals(8, jdbc.queryForObject("SELECT COUNT(*) FROM unit WHERE sort_order>2", Integer.class));
    }

    private void migrate(DriverManagerDataSource source, String name) throws Exception {
        try (var connection = source.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new EncodedResource(new ClassPathResource("db/migration/" + name), "UTF-8"));
        }
    }
}
