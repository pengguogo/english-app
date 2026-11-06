package com.englishapp.service.ai.record;

import com.englishapp.domain.AgentInteraction;
import com.englishapp.domain.ReviewSuggestion;
import com.englishapp.repository.AgentInteractionRepository;
import com.englishapp.repository.ReviewSuggestionRepository;
import com.englishapp.service.ai.explore.LearningItemIndex;
import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 主题学习状态（可解释，不判“掌握”）与复习建议：
 * 有过讲解 → SEEN；答过题 → ANSWERED；最近 3 次答题错 ≥2 → PRACTICE，并生成复习建议。
 * 只统计已作答的题；建议随依据变化重算，失去依据即删除。
 */
@Service
public class LearningTopicService {

    public enum Status { SEEN, ANSWERED, PRACTICE }

    static final int RECENT = 3;
    static final int PRACTICE_WRONG = 2;

    private final AgentInteractionRepository interactions;
    private final ReviewSuggestionRepository suggestions;
    private final LearningItemIndex items;
    private final Clock clock;

    public LearningTopicService(AgentInteractionRepository interactions, ReviewSuggestionRepository suggestions,
                                LearningItemIndex items, Clock clock) {
        this.interactions = interactions;
        this.suggestions = suggestions;
        this.items = items;
        this.clock = clock;
    }

    /** @param answered 已作答的题，按作答顺序；为空时只看有没有讲解 */
    static Status status(List<AgentInteraction> answered, boolean seen) {
        if (answered.isEmpty()) return seen ? Status.SEEN : null;
        return wrongInRecent(answered) >= PRACTICE_WRONG ? Status.PRACTICE : Status.ANSWERED;
    }

    static long wrongInRecent(List<AgentInteraction> answered) {
        return answered.subList(Math.max(0, answered.size() - RECENT), answered.size()).stream()
                .filter(i -> !Boolean.TRUE.equals(i.getCorrect())).count();
    }

    /** 按 child + theme 重算复习建议：PRACTICE 时保证有一条 OPEN 建议并刷新依据，否则删掉该主题的建议 */
    @Transactional
    public void recompute(Integer childId, Integer themeId) {
        List<AgentInteraction> answered =
                interactions.findByChildIdAndThemeIdAndChosenIsNotNullOrderByIdAsc(childId, themeId);
        List<ReviewSuggestion> existing = suggestions.findByChildIdAndThemeId(childId, themeId);
        if (status(answered, false) != Status.PRACTICE) {
            suggestions.deleteAllInBatch(existing);
            return;
        }
        int recent = Math.min(RECENT, answered.size());
        String basis = "最近 " + recent + " 次答题错了 " + wrongInRecent(answered) + " 次";
        String route = routeOfLastWrong(answered);
        ReviewSuggestion open = existing.stream().filter(s -> "OPEN".equals(s.getStatus())).findFirst().orElse(null);
        if (open == null && !existing.isEmpty()) return; // 家长已处理过（DONE / DISMISSED），不重复提醒
        if (open == null) {
            open = new ReviewSuggestion();
            open.setChildId(childId);
            open.setThemeId(themeId);
            open.setStatus("OPEN");
            open.setCreatedAt(RecordTime.now(clock));
        }
        open.setBasis(basis);
        open.setRoute(route);
        suggestions.save(open);
    }

    private String routeOfLastWrong(List<AgentInteraction> answered) {
        for (int k = answered.size() - 1; k >= 0; k--) {
            AgentInteraction i = answered.get(k);
            if (Boolean.TRUE.equals(i.getCorrect())) continue;
            LearningItemIndex.Item item = items.get(i.getLessonId(), i.getItemIndex());
            if (item != null) return item.route();
            if (i.getLessonId() != null) return "/lesson/" + i.getLessonId();
        }
        return "/";
    }
}
