package com.englishapp.service.ai.guide;

import com.englishapp.dto.AgentGuideResponseDTO;
import com.englishapp.dto.AgentGuideResponseDTO.Candidate;
import com.englishapp.dto.AgentGuideResponseDTO.Type;
import com.englishapp.dto.AgentTopicDTO;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * 课程向导：检索 → 判定 RECOMMEND / CLARIFY / NO_MATCH → 固定模板生成 speech。
 * 不调外部服务、不计额度、不保存对话。
 */
@Service
public class GuideService {

    /** 最高分低于此值视为没听懂（约等于“只命中一个学习项单词”） */
    static final double MIN_SCORE = 3;
    /** 第二名所在主题的最高分 ≥ 第一名 × 该比例时，认为前几名分散，需要追问 */
    static final double CLARIFY_RATIO = 0.85;
    /** 同主题内保留为候选的最低分比例 */
    static final double KEEP_RATIO = 0.6;
    static final int MAX_CANDIDATES = 3;
    static final int NO_MATCH_TOPICS = 4;
    static final int TOPIC_ENTRIES = 6;
    static final int MAX_SPEECH = 40;

    private final CourseIndex index;
    private final SynonymDictionary synonyms;

    public GuideService(CourseIndex index, SynonymDictionary synonyms) {
        this.index = index;
        this.synonyms = synonyms;
    }

    public AgentGuideResponseDTO guide(String text, Integer currentLessonId) {
        List<CourseIndex.Hit> hits = index.search(text).stream()
                .filter(h -> !Objects.equals(h.doc().lessonId(), currentLessonId))
                .toList();
        if (hits.isEmpty() || hits.get(0).score() < MIN_SCORE) {
            return noMatch();
        }
        CourseIndex.Hit top = hits.get(0);
        List<CourseIndex.Hit> themeLeaders = leadersPerTheme(hits);
        boolean spread = themeLeaders.size() > 1
                && themeLeaders.get(1).score() >= top.score() * CLARIFY_RATIO;
        if (spread) {
            List<CourseDoc> picks = themeLeaders.stream()
                    .filter(h -> h.score() >= top.score() * CLARIFY_RATIO)
                    .limit(MAX_CANDIDATES).map(CourseIndex.Hit::doc).toList();
            return new AgentGuideResponseDTO(Type.CLARIFY, clarifySpeech(picks), toCandidates(picks), List.of());
        }
        List<CourseDoc> picks = hits.stream()
                .filter(h -> h.doc().themeId() == top.doc().themeId() && h.score() >= top.score() * KEEP_RATIO)
                .limit(MAX_CANDIDATES).map(CourseIndex.Hit::doc).toList();
        return new AgentGuideResponseDTO(Type.RECOMMEND, recommendSpeech(picks), toCandidates(picks), List.of());
    }

    /** 热门主题入口：先取配置的热门主题，不足再按主题顺序补齐 */
    public List<AgentTopicDTO> topics() {
        return hotTopics(TOPIC_ENTRIES);
    }

    // ---------- 判定 ----------

    /** 每个主题只保留最高分那节，按分数降序 */
    private static List<CourseIndex.Hit> leadersPerTheme(List<CourseIndex.Hit> hits) {
        List<CourseIndex.Hit> leaders = new ArrayList<>();
        Set<Integer> seen = new LinkedHashSet<>();
        for (CourseIndex.Hit h : hits) {
            if (seen.add(h.doc().themeId())) leaders.add(h);
        }
        return leaders;
    }

    private AgentGuideResponseDTO noMatch() {
        List<AgentTopicDTO> topics = hotTopics(NO_MATCH_TOPICS);
        return new AgentGuideResponseDTO(Type.NO_MATCH, "这个我还没学会，选一个你喜欢的主题吧", List.of(), topics);
    }

    private List<AgentTopicDTO> hotTopics(int limit) {
        List<CourseIndex.ThemeEntry> picked = new ArrayList<>();
        for (String name : synonyms.hotTopics()) {
            CourseIndex.ThemeEntry t = index.themeByName(name);
            if (t != null && !picked.contains(t)) picked.add(t);
        }
        for (CourseIndex.ThemeEntry t : index.allThemes()) {
            if (picked.size() >= limit) break;
            if (!picked.contains(t)) picked.add(t);
        }
        return picked.stream().limit(limit)
                .map(t -> new AgentTopicDTO(t.themeId(), t.name(), t.image(), t.route())).toList();
    }

    private static List<Candidate> toCandidates(List<CourseDoc> docs) {
        return docs.stream()
                .map(d -> new Candidate(d.lessonId(), d.title(), d.themeName(), d.image(), d.route()))
                .toList();
    }

    // ---------- speech 模板（≤40 字，超长时退到更短的模板） ----------

    static String recommendSpeech(List<CourseDoc> picks) {
        CourseDoc first = picks.get(0);
        String full = "找到啦！" + first.themeName() + "里有《" + first.title() + "》，点一下就出发";
        if (full.length() <= MAX_SPEECH) return full;
        String shortForm = "找到啦！《" + first.title() + "》，点一下就出发";
        return shortForm.length() <= MAX_SPEECH ? shortForm : "找到啦！点一下卡片就出发";
    }

    static String clarifySpeech(List<CourseDoc> picks) {
        List<String> names = picks.stream().map(CourseDoc::themeName).distinct().toList();
        String full = "你想看" + String.join("、", names.subList(0, names.size() - 1))
                + "，还是" + names.get(names.size() - 1) + "？";
        if (full.length() <= MAX_SPEECH) return full;
        String two = "你想看" + names.get(0) + "，还是" + names.get(1) + "？";
        return two.length() <= MAX_SPEECH ? two : "找到好几个，你想看哪一个？";
    }
}
