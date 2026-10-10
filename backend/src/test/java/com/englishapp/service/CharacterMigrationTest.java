package com.englishapp.service;

import com.englishapp.domain.CharacterProgress;
import com.englishapp.repository.CharacterProgressRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.core.io.ClassPathResource;
import java.nio.file.Path;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CharacterMigrationTest {
    @TempDir Path directory;
    @Test
    void should_交付24字配图并支持幂等写入_当_执行真实SQLite迁移() throws Exception {
        var dataSource = new DriverManagerDataSource("jdbc:sqlite:" + directory.resolve("test.db"));
        var jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("CREATE TABLE lesson(id INTEGER PRIMARY KEY, content TEXT)");
        for (int id = 68; id <= 73; id++) jdbc.update("INSERT INTO lesson(id) VALUES (?)", id);
        try (var connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection,
                    new org.springframework.core.io.support.EncodedResource(
                            new ClassPathResource("db/migration/V82__strengthen_character_recognition.sql"), "UTF-8"));
        }
        var mapper = new ObjectMapper();
        var words = new java.util.HashSet<String>();
        for (String content : jdbc.queryForList("SELECT content FROM lesson", String.class)) {
            var items = mapper.readTree(content).path("items");
            assertEquals(4, items.size());
            for (var item : items) {
                assertTrue(item.path("recognition").asBoolean());
                assertTrue(words.add(item.path("word").asText()));
                assertTrue(item.path("exampleWord").asText().contains(item.path("word").asText()));
                assertTrue(item.path("exampleSentence").asText().contains(item.path("word").asText()));
                assertTrue(new ClassPathResource("static/images/" + item.path("image").asText() + ".jpg").exists());
            }
        }
        assertEquals(24, words.size());
        var repository = new CharacterProgressRepository(jdbc);
        var today = LocalDate.of(2026, 10, 10);
        assertTrue(repository.insertAttempt("retry", "山", "WRONG", today));
        assertFalse(repository.insertAttempt("retry", "山", "WRONG", today));
        repository.save(new CharacterProgress("山", 0, null, 1, 0, "WRONG", today.plusDays(1)));
        repository.save(new CharacterProgress("山", 1, today, 1, 0, "INDEPENDENT", today.plusDays(1)));
        assertEquals(1, repository.findAll().size());
        assertEquals(1, repository.findAll().get(0).wrongCount());
        assertEquals(today, repository.findAll().get(0).lastIndependentDate());
    }
}
