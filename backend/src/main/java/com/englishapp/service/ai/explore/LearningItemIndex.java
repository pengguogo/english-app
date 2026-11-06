package com.englishapp.service.ai.explore;

import com.englishapp.config.WordImageProperties;
import com.englishapp.domain.Lesson;
import com.englishapp.domain.Theme;
import com.englishapp.domain.Unit;
import com.englishapp.domain.enums.LessonType;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.ThemeRepository;
import com.englishapp.repository.UnitRepository;
import com.englishapp.service.WordImageResolver;
import com.englishapp.service.ai.guide.SynonymDictionary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * 阅读类学习项（READING items：title + content + image）的内存检索索引，作为自由探索与答疑的知识来源。
 * 不新建表；课程只经迁移变更，启动时建一次。打分：同义词组 / 2-gram 在标题上权重高，正文次之。
 */
@Component
public class LearningItemIndex {

    private static final Logger log = LoggerFactory.getLogger(LearningItemIndex.class);
    static final double W_TITLE = 4;
    static final double W_TEXT = 1;
    static final double SYNONYM_BOOST = 1.5;
    /** 低于此分视为没找到可讲的学习项 */
    static final double MIN_SCORE = 4;
    /** 查询概念（同义词组 + 2-gram）至少一半要落在标题上，避免“洞里”把“黑洞”带到“水晶洞里的救援” */
    static final double MIN_TITLE_COVERAGE = 0.5;
    private static final String FILLER = "吗呢啊呀吧哦嘛的了我你他她它讲去看听玩要想为什么怎么是会有个一些这那什";

    /** 一个学习项：正文是人工审过的课程原文 */
    public record Item(int lessonId, int itemIndex, int unitId, int themeId, String themeName,
                       String lessonName, String title, String text, String image, String route,
                       String norm) { }

    public record Hit(Item item, double score) { }

    private final LessonRepository lessons;
    private final UnitRepository units;
    private final ThemeRepository themes;
    private final WordImageResolver imageResolver;
    private final WordImageProperties imageProperties;
    private final SynonymDictionary synonyms;
    private final ObjectMapper mapper;

    private volatile List<Item> items = List.of();
    private volatile Map<String, Item> byKey = Map.of();

    public LearningItemIndex(LessonRepository lessons, UnitRepository units, ThemeRepository themes,
                             WordImageResolver imageResolver, WordImageProperties imageProperties,
                             SynonymDictionary synonyms, ObjectMapper mapper) {
        this.lessons = lessons;
        this.units = units;
        this.themes = themes;
        this.imageResolver = imageResolver;
        this.imageProperties = imageProperties;
        this.synonyms = synonyms;
        this.mapper = mapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public synchronized void rebuild() {
        long start = System.nanoTime();
        Map<Integer, Unit> unitById = units.findAll().stream().collect(Collectors.toMap(Unit::getId, Function.identity()));
        Map<Integer, Theme> themeById = themes.findAll().stream().collect(Collectors.toMap(Theme::getId, Function.identity()));
        Map<String, Boolean> fileCache = new HashMap<>();
        List<Item> list = new ArrayList<>();
        for (Lesson lesson : lessons.findAll()) {
            if (lesson.getType() != LessonType.READING) continue;
            Unit unit = unitById.get(lesson.getUnitId());
            Theme theme = unit == null ? null : themeById.get(unit.getThemeId());
            if (theme == null) continue;
            JsonNode root;
            try {
                root = mapper.readTree(lesson.getContent());
            } catch (Exception e) {
                continue;
            }
            // 绘本是故事页，不适合做知识讲解
            if (root.path("picturebook").asBoolean(false)) continue;
            JsonNode arr = root.path("items");
            for (int i = 0; i < arr.size(); i++) {
                JsonNode it = arr.get(i);
                String title = it.path("title").asText("").trim();
                String text = it.path("content").asText("").trim();
                if (title.isEmpty() || text.isEmpty()) continue;
                String route = "/lesson/" + lesson.getId() + "?themeId=" + theme.getId() + "&unitId=" + unit.getId()
                        + "&subjectId=" + theme.getSubjectId();
                list.add(new Item(lesson.getId(), i, unit.getId(), theme.getId(), theme.getName(), lesson.getName(),
                        title, text, image(it.path("image").asText(""), fileCache), route,
                        normalize(theme.getName() + " " + lesson.getName() + " " + title)));
            }
        }
        items = List.copyOf(list);
        byKey = list.stream().collect(Collectors.toMap(i -> i.lessonId() + ":" + i.itemIndex(), Function.identity()));
        log.info("学习项检索索引已建立：{} 个学习项，耗时 {}ms", list.size(), (System.nanoTime() - start) / 1_000_000);
    }

    /** 课程中答疑：按 lessonId + itemIndex 取原文；不存在为 null（不信任前端传来的文本） */
    public Item get(Integer lessonId, Integer itemIndex) {
        if (lessonId == null || itemIndex == null) return null;
        return byKey.get(lessonId + ":" + itemIndex);
    }

    /** 检索：返回最高分学习项（≥ MIN_SCORE），同分按课程顺序 */
    public Hit best(String query) {
        return best(query, MIN_TITLE_COVERAGE);
    }

    /**
     * @param minCoverage 查询概念落在标题上的最低比例；不能用模型判断相关性时（游客、未同意）传 1.0，
     *                    宁可不讲也不拿不对题的课程原文
     */
    public Hit best(String query, double minCoverage) {
        String q = normalize(query);
        if (q.isEmpty()) return null;
        List<Set<String>> groups = synonyms.groupsIn(q);
        // 已被同义词组覆盖的 2-gram 不重复计数（“彩色”既是同义词又是 2-gram 时只算一次）
        List<String> grams = grams(q).stream()
                .filter(g -> groups.stream().noneMatch(grp -> grp.stream().anyMatch(t -> t.contains(g))))
                .toList();
        int concepts = groups.size() + grams.size();
        if (concepts == 0) return null;
        Hit best = null;
        for (Item item : items) {
            double s = 0;
            int onTitle = 0;
            for (Set<String> g : groups) {
                double f = fieldScore(item, g);
                s += SYNONYM_BOOST * f;
                if (f == W_TITLE) onTitle++;
            }
            for (String g : grams) {
                double f = fieldScore(item, Set.of(g));
                s += f;
                if (f == W_TITLE) onTitle++;
            }
            if (s < MIN_SCORE || onTitle < concepts * minCoverage) continue;
            if (best == null || s > best.score()) best = new Hit(item, s);
        }
        return best;
    }

    int size() {
        return items.size();
    }

    private static double fieldScore(Item item, Set<String> terms) {
        for (String t : terms) if (item.norm().contains(t)) return W_TITLE;
        for (String t : terms) if (item.text().contains(t)) return W_TEXT;
        return 0;
    }

    /** 去标点空白、转小写；去掉语气和疑问虚词后取 2-gram */
    static String normalize(String s) {
        return s == null ? "" : s.toLowerCase().replaceAll("[\\p{P}\\p{S}\\s]+", "");
    }

    static List<String> grams(String normalized) {
        StringBuilder core = new StringBuilder();
        for (char c : normalized.toCharArray()) {
            if (FILLER.indexOf(c) < 0) core.append(c);
            else core.append(' ');
        }
        List<String> out = new ArrayList<>();
        for (String run : core.toString().trim().split("\\s+")) {
            if (run.length() == 1) continue;
            for (int i = 0; i + 2 <= run.length(); i++) out.add(run.substring(i, i + 2));
        }
        return out.stream().distinct().toList();
    }

    private String image(String key, Map<String, Boolean> cache) {
        if (key.isBlank()) return null;
        String url = imageResolver.resolveUrl(key);
        String base = Objects.requireNonNullElse(imageProperties.getBaseUrl(), "");
        String path = url.startsWith(base) ? url.substring(base.length()) : url;
        if (path.contains("..")) return null;
        return cache.computeIfAbsent(path, p -> new ClassPathResource("static" + p).exists()) ? url : null;
    }

    /** 测试用：按顺序取前 n 个 */
    List<Item> first(int n) {
        return items.stream().sorted(Comparator.comparingInt(Item::lessonId)).limit(n).toList();
    }
}
