package com.englishapp.service.ai.record;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.englishapp.domain.AgentInteraction;
import com.englishapp.domain.AgentMessage;
import com.englishapp.domain.AgentSession;
import com.englishapp.domain.ReviewSuggestion;
import com.englishapp.repository.AgentInteractionRepository;
import com.englishapp.repository.AgentMessageRepository;
import com.englishapp.repository.AgentSessionRepository;
import com.englishapp.repository.ReviewSuggestionRepository;
import com.englishapp.security.IntegrationTestBase;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** 保留期 180 天：第 179 天保留、第 181 天删除（级联消息、答题，失去依据的建议同步删除） */
class RetentionJobTest extends IntegrationTestBase {

    private static final ZonedDateTime NOW = ZonedDateTime.of(2026, 11, 6, 3, 0, 0, 0, RecordTime.ZONE);

    @Autowired
    private AgentSessionRepository sessions;
    @Autowired
    private AgentMessageRepository messages;
    @Autowired
    private AgentInteractionRepository interactions;
    @Autowired
    private ReviewSuggestionRepository suggestions;
    @Autowired
    private AgentRecordService records;

    @Test
    void should_删除181天前会话并保留179天的_当_每日清理() throws Exception {
        Actor a = newFamilyWithChild("小朋友");
        AgentSession keep = session(a, NOW.minusDays(179).toLocalDateTime());
        AgentSession old = session(a, NOW.minusDays(181).toLocalDateTime());
        // 旧会话里两道错题 → 有一条复习建议
        answer(old, a.childId(), false);
        answer(old, a.childId(), false);
        AgentMessage m = new AgentMessage();
        m.setSessionId(old.getId());
        m.setRole("CHILD");
        m.setText("霸王龙");
        m.setModeration("NONE");
        m.setCreatedAt(old.getCreatedAt());
        messages.save(m);
        ReviewSuggestion r = new ReviewSuggestion();
        r.setChildId(a.childId());
        r.setThemeId(1);
        r.setBasis("最近 2 次答题错了 2 次");
        r.setRoute("/lesson/1");
        r.setStatus("OPEN");
        r.setCreatedAt(old.getCreatedAt());
        suggestions.save(r);

        RetentionJob job = new RetentionJob(sessions, records, Clock.fixed(NOW.toInstant(), RecordTime.ZONE));
        assertEquals(1, job.purge());

        assertTrue(sessions.findById(keep.getId()).isPresent());
        assertFalse(sessions.findById(old.getId()).isPresent());
        assertEquals(0, messages.findBySessionIdOrderByIdAsc(old.getId()).size());
        assertEquals(0, interactions.findBySessionIdOrderByIdAsc(old.getId()).size());
        assertEquals(0, suggestions.findByChildIdAndThemeId(a.childId(), 1).size());
    }

    private AgentSession session(Actor a, LocalDateTime createdAt) {
        AgentSession s = new AgentSession();
        s.setChildId(a.childId());
        s.setFamilyId(a.familyId());
        s.setSourcePage("FREE");
        s.setCreatedAt(createdAt);
        s.setUpdatedAt(createdAt);
        return sessions.save(s);
    }

    private void answer(AgentSession s, int childId, boolean correct) {
        AgentInteraction i = new AgentInteraction();
        i.setSessionId(s.getId());
        i.setChildId(childId);
        i.setThemeId(1);
        i.setQuestion("题目");
        i.setOptionsJson("[]");
        i.setChosen("a");
        i.setCorrect(correct);
        i.setQuizTokenHash(UUID.randomUUID().toString());
        i.setCreatedAt(s.getCreatedAt());
        interactions.save(i);
    }
}
