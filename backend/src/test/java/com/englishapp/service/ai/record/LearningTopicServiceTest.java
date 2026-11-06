package com.englishapp.service.ai.record;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.englishapp.domain.AgentInteraction;
import com.englishapp.service.ai.record.LearningTopicService.Status;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** 状态规则：有讲解 → SEEN；答过题 → ANSWERED；最近 3 次错 ≥2 → PRACTICE */
class LearningTopicServiceTest {

    private static List<AgentInteraction> answers(boolean... correct) {
        List<AgentInteraction> list = new ArrayList<>();
        for (boolean c : correct) {
            AgentInteraction i = new AgentInteraction();
            i.setChosen("a");
            i.setCorrect(c);
            list.add(i);
        }
        return list;
    }

    @Test
    void should_为SEEN_当_只有讲解() {
        assertEquals(Status.SEEN, LearningTopicService.status(List.of(), true));
        assertNull(LearningTopicService.status(List.of(), false));
    }

    @Test
    void should_为ANSWERED_当_只答对一次() {
        assertEquals(Status.ANSWERED, LearningTopicService.status(answers(true), true));
    }

    @Test
    void should_为ANSWERED_当_只答过一次且答错() {
        assertEquals(Status.ANSWERED, LearningTopicService.status(answers(false), true));
    }

    @Test
    void should_为PRACTICE_当_最近三次错两次() {
        assertEquals(Status.PRACTICE, LearningTopicService.status(answers(true, false, false), true));
        assertEquals(Status.PRACTICE, LearningTopicService.status(answers(false, false), true));
    }

    @Test
    void should_回到ANSWERED_当_更早的错题滑出最近三次() {
        assertEquals(Status.ANSWERED, LearningTopicService.status(answers(false, false, true, true, false), true));
    }
}
