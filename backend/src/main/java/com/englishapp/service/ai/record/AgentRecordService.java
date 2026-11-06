package com.englishapp.service.ai.record;

import com.englishapp.domain.AgentInteraction;
import com.englishapp.domain.AgentMessage;
import com.englishapp.domain.AgentSession;
import com.englishapp.dto.AgentChatResponseDTO;
import com.englishapp.dto.AgentChatResponseDTO.Source;
import com.englishapp.dto.AgentChatResponseDTO.Type;
import com.englishapp.repository.AgentInteractionRepository;
import com.englishapp.repository.AgentMessageRepository;
import com.englishapp.repository.AgentSessionRepository;
import com.englishapp.service.ai.explore.LearningItemIndex;
import com.englishapp.service.ai.safety.ContentModerator;
import com.englishapp.service.ai.safety.ModerationResult;
import com.englishapp.service.ai.safety.SafetyCategory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学习伙伴对话与答题落库。调用方保证：已登录、已选孩子、家庭已同意；本类不记日志正文。
 * 调用方须 catch 本类异常：记录失败不能影响孩子得到回答。
 */
@Service
public class AgentRecordService {

    static final Duration SESSION_IDLE = Duration.ofMinutes(30);
    static final int TOPIC_MAX = 30;
    static final int TEXT_MAX = 500;

    private final AgentSessionRepository sessions;
    private final AgentMessageRepository messages;
    private final AgentInteractionRepository interactions;
    private final LearningTopicService topics;
    private final ContentModerator moderator;
    private final LearningItemIndex items;
    private final ObjectMapper mapper;
    private final Clock clock;

    public AgentRecordService(AgentSessionRepository sessions, AgentMessageRepository messages,
                              AgentInteractionRepository interactions, LearningTopicService topics,
                              ContentModerator moderator, LearningItemIndex items, ObjectMapper mapper, Clock clock) {
        this.sessions = sessions;
        this.messages = messages;
        this.interactions = interactions;
        this.topics = topics;
        this.moderator = moderator;
        this.items = items;
        this.mapper = mapper;
        this.clock = clock;
    }

    /**
     * 记录一轮对话：孩子原话（脱敏）+ 实际展示的回答 + 下发的题目（未作答）。
     *
     * @return 本轮所属会话 id
     */
    @Transactional
    public Long recordChat(long familyId, int childId, Long sessionId, String childText, Integer ctxLessonId,
                           AgentChatResponseDTO resp) {
        LocalDateTime now = RecordTime.now(clock);
        ModerationResult in = moderator.checkInput(childText);
        String saved = in.category() == SafetyCategory.PERSONAL_INFO
                ? Redactor.PERSONAL_INFO_PLACEHOLDER : clip(Redactor.redact(childText.trim()), TEXT_MAX);

        AgentSession s = reusable(sessionId, childId, now);
        if (s == null) {
            s = new AgentSession();
            s.setChildId(childId);
            s.setFamilyId(familyId);
            s.setTopic(clip(saved, TOPIC_MAX));
            s.setSourcePage(ctxLessonId == null ? "FREE" : "LESSON");
            s.setLessonId(ctxLessonId);
            s.setCreatedAt(now);
        }
        if (in.flagParent() && !s.isFlagged()) {
            s.setFlagged(true);
            s.setFlagCategory(in.category().name());
        }
        s.setUpdatedAt(now);
        s = sessions.save(s);

        String category = in.category() == SafetyCategory.NONE ? null : in.category().name();
        String inputModeration = in.allowed() ? "NONE" : "SAFE_REPLY";
        messages.save(message(s.getId(), "CHILD", saved, inputModeration, category, null, now));

        // 输入放行、结果却是安全话术 → 生成内容被拦截，只存安全话术
        String agentModeration = !in.allowed() ? "SAFE_REPLY"
                : resp.type() == Type.SAFE_REPLY ? "OUTPUT_BLOCKED" : "NONE";
        LearningItemIndex.Item item = lessonItem(resp);
        messages.save(message(s.getId(), "AGENT", clip(agentText(resp), TEXT_MAX), agentModeration, category,
                item == null ? null : item.themeId(), now));

        if (resp.quiz() != null && resp.quiz().quizToken() != null) {
            interactions.save(interaction(s.getId(), childId, item, resp, now));
        }
        return s.getId();
    }

    /** 记录首次作答；之后重选只判分不改库。sessionId 必须与下发题目时的会话一致 */
    @Transactional
    public void recordAnswer(int childId, Long sessionId, String quizToken, String optionId, boolean correct) {
        if (sessionId == null) return;
        AgentInteraction i = interactions.findByQuizTokenHashAndChildId(sha256(quizToken), childId).orElse(null);
        if (i == null || !sessionId.equals(i.getSessionId()) || i.getChosen() != null) return;
        if (!hasOption(i.getOptionsJson(), optionId)) return; // 不存在的选项不记录
        LocalDateTime now = RecordTime.now(clock);
        i.setChosen(optionId);
        i.setCorrect(correct);
        interactions.save(i);
        sessions.findById(sessionId).ifPresent(s -> {
            s.setUpdatedAt(now);
            sessions.save(s);
        });
        if (i.getThemeId() != null) topics.recompute(childId, i.getThemeId());
    }

    /**
     * 级联删除会话（消息、答题），再按 child + theme 重算建议；同一事务，失败整体回滚。
     * SQLite 未开外键约束，级联由这里显式执行。
     */
    @Transactional
    public void deleteSessions(List<AgentSession> list) {
        if (list.isEmpty()) return;
        List<Long> ids = list.stream().map(AgentSession::getId).toList();
        Set<ChildTheme> affected = new LinkedHashSet<>();
        for (AgentInteraction i : interactions.findBySessionIdIn(ids)) {
            if (i.getThemeId() != null) affected.add(new ChildTheme(i.getChildId(), i.getThemeId()));
        }
        for (AgentSession s : list) {
            for (AgentMessage m : messages.findBySessionIdInAndThemeIdIsNotNull(List.of(s.getId()))) {
                affected.add(new ChildTheme(s.getChildId(), m.getThemeId()));
            }
        }
        messages.deleteBySessionIds(ids);
        interactions.deleteBySessionIds(ids);
        sessions.deleteAllInBatch(list);
        affected.forEach(ct -> topics.recompute(ct.childId(), ct.themeId()));
    }

    private record ChildTheme(Integer childId, Integer themeId) { }

    /** 会话属于当前孩子且 30 分钟内有活动才续用 */
    private AgentSession reusable(Long sessionId, int childId, LocalDateTime now) {
        if (sessionId == null) return null;
        return sessions.findById(sessionId)
                .filter(s -> s.getChildId() == childId)
                .filter(s -> !s.getUpdatedAt().plus(SESSION_IDLE).isBefore(now))
                .orElse(null);
    }

    /** 课程原文讲解对应的学习项（模型生成的讲解没有主题） */
    private LearningItemIndex.Item lessonItem(AgentChatResponseDTO resp) {
        AgentChatResponseDTO.Explain e = resp.explain();
        if (e == null || e.source() != Source.LESSON) return null;
        return items.get(e.lessonId(), e.itemIndex());
    }

    /** 实际展示的内容：有讲解时前缀「【标题】」 */
    private static String agentText(AgentChatResponseDTO resp) {
        String speech = resp.speech() == null ? "" : resp.speech();
        return resp.explain() == null ? speech : "【" + resp.explain().title() + "】" + speech;
    }

    private AgentInteraction interaction(Long sessionId, int childId, LearningItemIndex.Item item,
                                         AgentChatResponseDTO resp, LocalDateTime now) {
        AgentInteraction i = new AgentInteraction();
        i.setSessionId(sessionId);
        i.setChildId(childId);
        if (item != null) {
            i.setThemeId(item.themeId());
            i.setLessonId(item.lessonId());
            i.setItemIndex(item.itemIndex());
        }
        i.setQuestion(clip(resp.quiz().question(), 200));
        try {
            i.setOptionsJson(mapper.writeValueAsString(resp.quiz().options()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("选项序列化失败", e);
        }
        i.setQuizTokenHash(sha256(resp.quiz().quizToken()));
        i.setCreatedAt(now);
        return i;
    }

    private static AgentMessage message(Long sessionId, String role, String text, String moderation,
                                        String category, Integer themeId, LocalDateTime now) {
        AgentMessage m = new AgentMessage();
        m.setSessionId(sessionId);
        m.setRole(role);
        m.setText(text);
        m.setModeration(moderation);
        m.setCategory(category);
        m.setThemeId(themeId);
        m.setCreatedAt(now);
        return m;
    }

    private boolean hasOption(String optionsJson, String optionId) {
        try {
            for (var o : mapper.readTree(optionsJson)) {
                if (o.path("id").asText().equals(optionId)) return true;
            }
            return false;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    static String sha256(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String clip(String s, int max) {
        return s == null || s.length() <= max ? s : s.substring(0, max);
    }
}
