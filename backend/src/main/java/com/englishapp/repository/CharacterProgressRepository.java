package com.englishapp.repository;

import com.englishapp.domain.CharacterProgress;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public class CharacterProgressRepository {
    private final JdbcTemplate jdbc;
    public CharacterProgressRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<CharacterProgress> findAll(Integer userId) {
        return jdbc.query("SELECT * FROM character_progress WHERE user_id = ? ORDER BY due_date, word",
                (rs, row) -> new CharacterProgress(rs.getString("word"), rs.getInt("independent_days"),
                        rs.getString("last_independent_date") == null ? null : LocalDate.parse(rs.getString("last_independent_date")),
                        rs.getInt("wrong_count"), rs.getInt("assisted_count"), rs.getString("last_outcome"),
                        LocalDate.parse(rs.getString("due_date"))), userId);
    }

    public boolean insertAttempt(Integer userId, String eventId, String word, String outcome, LocalDate date) {
        return jdbc.update("INSERT INTO character_attempt(event_id,user_id,word,outcome,attempt_date) "
                + "VALUES (?,?,?,?,?) ON CONFLICT(user_id,event_id) DO NOTHING", eventId, userId, word, outcome, date.toString()) == 1;
    }

    public void save(Integer userId, CharacterProgress p) {
        jdbc.update("INSERT INTO character_progress(user_id,word,independent_days,last_independent_date,"
                + "wrong_count,assisted_count,last_outcome,due_date) VALUES (?,?,?,?,?,?,?,?) "
                + "ON CONFLICT(user_id,word) DO UPDATE SET independent_days=excluded.independent_days,"
                + "last_independent_date=excluded.last_independent_date,wrong_count=excluded.wrong_count,"
                + "assisted_count=excluded.assisted_count,last_outcome=excluded.last_outcome,due_date=excluded.due_date",
                userId, p.word(), p.independentDays(), p.lastIndependentDate() == null ? null : p.lastIndependentDate().toString(),
                p.wrongCount(), p.assistedCount(), p.lastOutcome(), p.dueDate().toString());
    }
}
