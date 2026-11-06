package com.englishapp.service.ai.record;

import com.englishapp.domain.AgentInteraction;
import com.englishapp.domain.AgentMessage;
import com.englishapp.domain.AgentSession;
import com.englishapp.domain.AppUser;
import com.englishapp.domain.ReviewSuggestion;
import com.englishapp.domain.Theme;
import com.englishapp.dto.AgentChatResponseDTO;
import com.englishapp.dto.ParentAgentSessionDTO;
import com.englishapp.dto.ParentAgentSessionDetailDTO;
import com.englishapp.dto.ParentAgentSessionPageDTO;
import com.englishapp.dto.ParentAgentTopicDTO;
import com.englishapp.repository.AgentInteractionRepository;
import com.englishapp.repository.AgentMessageRepository;
import com.englishapp.repository.AgentSessionRepository;
import com.englishapp.repository.AppUserRepository;
import com.englishapp.repository.ReviewSuggestionRepository;
import com.englishapp.repository.ThemeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 家长查看与删除：一律按 familyId 过滤，childId 只是筛选条件；不属于本家庭的资源 → 404「记录不存在」 */
@Service
public class ParentAgentService {

    static final String NOT_FOUND = "记录不存在";
    static final int MAX_SIZE = 50;
    static final int TOPIC_SESSIONS = 5;
    private static final Map<String, String> FLAG_LABELS = Map.of(
            "DEATH_FAMILY", "聊到了家人离开",
            "STRANGER", "提到了陌生人",
            "EMOTION", "孩子情绪低落");
    private static final TypeReference<List<AgentChatResponseDTO.Option>> OPTIONS = new TypeReference<>() { };

    private final AgentSessionRepository sessions;
    private final AgentMessageRepository messages;
    private final AgentInteractionRepository interactions;
    private final ReviewSuggestionRepository suggestions;
    private final AppUserRepository users;
    private final ThemeRepository themes;
    private final AgentRecordService records;
    private final ObjectMapper mapper;

    public ParentAgentService(AgentSessionRepository sessions, AgentMessageRepository messages,
                              AgentInteractionRepository interactions, ReviewSuggestionRepository suggestions,
                              AppUserRepository users, ThemeRepository themes, AgentRecordService records,
                              ObjectMapper mapper) {
        this.sessions = sessions;
        this.messages = messages;
        this.interactions = interactions;
        this.suggestions = suggestions;
        this.users = users;
        this.themes = themes;
        this.records = records;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ParentAgentSessionPageDTO list(long familyId, Integer childId, Boolean flagged, int page, int size) {
        int p = Math.max(0, page);
        int s = Math.min(MAX_SIZE, Math.max(1, size));
        Slice<AgentSession> slice = sessions.search(familyId, childId, flagged, PageRequest.of(p, s));
        Map<Integer, String> names = childNames(familyId);
        return new ParentAgentSessionPageDTO(slice.getContent().stream().map(x -> summary(x, names)).toList(),
                slice.hasNext());
    }

    @Transactional(readOnly = true)
    public ParentAgentSessionDetailDTO detail(long familyId, long id) {
        AgentSession s = owned(familyId, id);
        List<ParentAgentSessionDetailDTO.Message> ms = messages.findBySessionIdOrderByIdAsc(s.getId()).stream()
                .map(m -> new ParentAgentSessionDetailDTO.Message(m.getRole(), m.getText(), m.getModeration(),
                        RecordTime.iso(m.getCreatedAt())))
                .toList();
        List<ParentAgentSessionDetailDTO.Interaction> is = interactions.findBySessionIdOrderByIdAsc(s.getId())
                .stream()
                .map(i -> new ParentAgentSessionDetailDTO.Interaction(i.getQuestion(), options(i.getOptionsJson()),
                        i.getChosen(), i.getCorrect(), RecordTime.iso(i.getCreatedAt())))
                .toList();
        return new ParentAgentSessionDetailDTO(summary(s, childNames(familyId)), ms, is);
    }

    public void delete(long familyId, long id) {
        records.deleteSessions(List.of(owned(familyId, id)));
    }

    /** 主题学习情况：childId 为空时返回本家庭全部孩子；别的家庭的 childId → 空列表 */
    @Transactional(readOnly = true)
    public List<ParentAgentTopicDTO> topics(long familyId, Integer childId) {
        List<Integer> children = users.findByFamilyIdOrderByIdAsc(familyId).stream().map(AppUser::getId)
                .filter(id -> childId == null || id.equals(childId)).toList();
        if (children.isEmpty()) return List.of();
        Map<Long, AgentSession> byId = sessions.findByChildIdIn(children).stream()
                .collect(Collectors.toMap(AgentSession::getId, Function.identity()));
        if (byId.isEmpty()) return List.of();

        Map<Key, Acc> acc = new HashMap<>();
        for (AgentMessage m : messages.findBySessionIdInAndThemeIdIsNotNull(byId.keySet())) {
            Acc a = acc.computeIfAbsent(new Key(byId.get(m.getSessionId()).getChildId(), m.getThemeId()),
                    k -> new Acc());
            a.touch(m.getSessionId(), m.getCreatedAt());
        }
        for (AgentInteraction i : interactions.findBySessionIdIn(byId.keySet())) {
            if (i.getThemeId() == null) continue;
            Acc a = acc.computeIfAbsent(new Key(i.getChildId(), i.getThemeId()), k -> new Acc());
            a.touch(i.getSessionId(), i.getCreatedAt());
            if (i.getChosen() != null) a.answered.add(i);
        }

        Map<Key, ReviewSuggestion> open = suggestions.findByChildIdInAndStatus(children, "OPEN").stream()
                .collect(Collectors.toMap(r -> new Key(r.getChildId(), r.getThemeId()), Function.identity(),
                        (x, y) -> x));
        Map<Integer, String> themeNames = themes.findAllById(acc.keySet().stream().map(Key::themeId).toList())
                .stream().collect(Collectors.toMap(Theme::getId, Theme::getName));

        List<ParentAgentTopicDTO> out = new ArrayList<>();
        acc.forEach((k, a) -> {
            a.answered.sort(Comparator.comparing(AgentInteraction::getId));
            LearningTopicService.Status st = LearningTopicService.status(a.answered, true);
            int correct = (int) a.answered.stream().filter(i -> Boolean.TRUE.equals(i.getCorrect())).count();
            ReviewSuggestion r = st == LearningTopicService.Status.PRACTICE ? open.get(k) : null;
            out.add(new ParentAgentTopicDTO(k.childId(), k.themeId(), themeNames.get(k.themeId()), st.name(),
                    a.sessionIds.descendingSet().stream().limit(TOPIC_SESSIONS).toList(),
                    correct, a.answered.size() - correct, RecordTime.iso(a.lastAt),
                    r == null ? null : new ParentAgentTopicDTO.Suggestion(r.getId(), r.getBasis(), r.getRoute())));
        });
        out.sort(Comparator.comparing(ParentAgentTopicDTO::lastAt, Comparator.reverseOrder()));
        return out;
    }

    private record Key(Integer childId, Integer themeId) { }

    private static final class Acc {
        final TreeSet<Long> sessionIds = new TreeSet<>();
        final List<AgentInteraction> answered = new ArrayList<>();
        LocalDateTime lastAt;

        void touch(Long sessionId, LocalDateTime at) {
            sessionIds.add(sessionId);
            if (lastAt == null || at.isAfter(lastAt)) lastAt = at;
        }
    }

    private AgentSession owned(long familyId, long id) {
        return sessions.findByIdAndFamilyId(id, familyId).orElseThrow(() -> new NoSuchElementException(NOT_FOUND));
    }

    private Map<Integer, String> childNames(long familyId) {
        return users.findByFamilyIdOrderByIdAsc(familyId).stream()
                .collect(Collectors.toMap(AppUser::getId, u -> u.getNickname() == null ? "" : u.getNickname()));
    }

    private ParentAgentSessionDTO summary(AgentSession s, Map<Integer, String> names) {
        return new ParentAgentSessionDTO(s.getId(), s.getChildId(), names.get(s.getChildId()), s.getTopic(),
                s.getSourcePage(), s.isFlagged(), s.isFlagged() ? FLAG_LABELS.get(s.getFlagCategory()) : null,
                messages.countBySessionIdAndRole(s.getId(), "CHILD"),
                RecordTime.iso(s.getCreatedAt()), RecordTime.iso(s.getUpdatedAt()));
    }

    private List<AgentChatResponseDTO.Option> options(String json) {
        try {
            return mapper.readValue(json, OPTIONS);
        } catch (Exception e) {
            return List.of();
        }
    }
}
