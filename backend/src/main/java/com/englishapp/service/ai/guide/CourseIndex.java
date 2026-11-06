package com.englishapp.service.ai.guide;

import com.englishapp.config.WordImageProperties;
import com.englishapp.domain.Lesson;
import com.englishapp.domain.Subject;
import com.englishapp.domain.Theme;
import com.englishapp.domain.Unit;
import com.englishapp.domain.enums.LessonType;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.SubjectRepository;
import com.englishapp.repository.ThemeRepository;
import com.englishapp.repository.UnitRepository;
import com.englishapp.service.WordImageResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
 * 课程检索内存索引：启动时从 subject/theme/unit/lesson 表建一次，不新建表、不调模型。
 * <p>
 * 只收“可学”的课：所属单元/主题/学科存在，content 能解析且 items 非空，绘本扩展校验通过。
 * 不按 is_locked 过滤：锁定机制已取消（见 UnitServiceImpl.computeLocked，恒为 false），
 * 线上所有主题都能直接进入，按字段过滤会把“恐龙世界”等可学主题挡掉。
 * <p>
 * 打分：每个查询概念（同义词组 / 中文 2-gram / 英文词）取命中字段的最高权重，
 * 主题名 > 课程名 > 单元名 > 学科名/学习项（标题、单词、翻译）；孩子的话包含完整课程名再加分；
 * 单字只在主题名、课程名上低权重计分，用于打破平局。
 * route 与 image 在建索引时生成，image 指向的文件在 classpath:static 下检查一次并缓存。
 */
@Component
public class CourseIndex {

    private static final Logger log = LoggerFactory.getLogger(CourseIndex.class);

    static final double W_THEME = 6;
    static final double W_LESSON = 5;
    static final double W_UNIT = 4;
    static final double W_ITEM = 2;
    /** 同义词组命中是完整词，比任意 2-gram 更可信 */
    static final double SYNONYM_BOOST = 1.5;
    /** 孩子的话里包含完整课程名 / 单元名 / 主题名（≥2 字），说明点名要它 */
    static final double W_TITLE_EXACT = 12;
    /** 问答课是巩固用的，同等相关时先推知识课 */
    static final double QUIZ_FACTOR = 0.9;
    /** 单字只在名称字段上计分，权重很低，只用于打破平局 */
    static final double CHAR_FACTOR = 0.2;

    /** 单字匹配时排除的高频字，避免“小”“大”把所有课程拉进来 */
    private static final String WEAK_CHARS = "小大一个是有么和在吧好不们这那上下多少说做";

    private final SubjectRepository subjects;
    private final ThemeRepository themes;
    private final UnitRepository units;
    private final LessonRepository lessons;
    private final WordImageResolver imageResolver;
    private final WordImageProperties imageProperties;
    private final ObjectMapper mapper;
    private final SynonymDictionary synonyms;

    private volatile Snapshot snapshot = new Snapshot(List.of(), Map.of());

    public CourseIndex(SubjectRepository subjects, ThemeRepository themes, UnitRepository units,
                       LessonRepository lessons, WordImageResolver imageResolver,
                       WordImageProperties imageProperties, ObjectMapper mapper, SynonymDictionary synonyms) {
        this.subjects = subjects;
        this.themes = themes;
        this.units = units;
        this.lessons = lessons;
        this.imageResolver = imageResolver;
        this.imageProperties = imageProperties;
        this.mapper = mapper;
        this.synonyms = synonyms;
    }

    /** 主题入口信息（topics 用） */
    record ThemeEntry(int themeId, String name, String image, String route, List<Integer> orderKey) { }

    /** 一次检索的打分结果 */
    record Hit(CourseDoc doc, double score) { }

    private record Snapshot(List<CourseDoc> docs, Map<Integer, ThemeEntry> themes) { }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        rebuild();
    }

    /** 重新从数据库建索引；课程只通过迁移变更，启动时建一次即可 */
    public synchronized void rebuild() {
        long start = System.nanoTime();
        Map<Integer, Subject> subjectById = byId(subjects.findAll(), Subject::getId);
        Map<Integer, Theme> themeById = byId(themes.findAll(), Theme::getId);
        Map<Integer, Unit> unitById = byId(units.findAll(), Unit::getId);
        Map<String, Boolean> fileCache = new HashMap<>();

        List<CourseDoc> docs = new ArrayList<>();
        Map<Integer, String> firstImageOfTheme = new HashMap<>();
        int skipped = 0;
        for (Lesson lesson : lessons.findAll()) {
            Unit unit = unitById.get(lesson.getUnitId());
            Theme theme = unit == null ? null : themeById.get(unit.getThemeId());
            Subject subject = theme == null ? null : subjectById.get(theme.getSubjectId());
            JsonNode root = subject == null ? null : parseLearnable(lesson);
            if (root == null) {
                skipped++;
                continue;
            }
            String themeIcon = existingUrl(theme.getIconUrl(), fileCache);
            String image = firstExistingItemImage(root, fileCache);
            if (image != null) firstImageOfTheme.putIfAbsent(theme.getId(), image);
            // 绘本课名（看图听故事 / 跟着说一说）各书重复，展示时带上书名
            String bookTitle = root.path("bookTitle").asText("");
            String title = bookTitle.isBlank() ? lesson.getName() : bookTitle + " · " + lesson.getName();
            docs.add(new CourseDoc(
                    lesson.getId(), title, unit.getId(), theme.getId(), theme.getName(),
                    lesson.getType() == LessonType.QUIZ,
                    route(lesson, unit, theme, subject, root),
                    image != null ? image : themeIcon,
                    List.of(nz(subject.getSortOrder()), nz(theme.getSortOrder()), nz(unit.getSortOrder()),
                            nz(lesson.getSortOrder()), lesson.getId()),
                    QueryTerms.normalize(lesson.getName()),
                    QueryTerms.normalize(theme.getName()),
                    QueryTerms.normalize(lesson.getName() + " " + root.path("bookTitle").asText("")),
                    QueryTerms.normalize(unit.getName()),
                    QueryTerms.normalize(subject.getName() + " " + itemText(lesson.getType(), root))));
        }
        docs.sort(Comparator.comparing(CourseDoc::orderKey, CourseIndex::compareKeys));

        Map<Integer, ThemeEntry> themeEntries = new LinkedHashMap<>();
        for (CourseDoc d : docs) {
            themeEntries.computeIfAbsent(d.themeId(), id -> {
                Theme t = themeById.get(id);
                String icon = existingUrl(t.getIconUrl(), fileCache);
                return new ThemeEntry(id, t.getName(), icon != null ? icon : firstImageOfTheme.get(id),
                        "/theme/" + id + "?subjectId=" + t.getSubjectId(),
                        List.of(nz(subjectById.get(t.getSubjectId()).getSortOrder()), nz(t.getSortOrder())));
            });
        }
        snapshot = new Snapshot(List.copyOf(docs), Map.copyOf(themeEntries));
        log.info("课程检索索引已建立：{} 节课、{} 个主题，跳过 {} 节，耗时 {}ms",
                docs.size(), themeEntries.size(), skipped, (System.nanoTime() - start) / 1_000_000);
    }

    /** 检索：返回得分 > 0 的课程，按分数降序；同分时非问答课在前，再按课程顺序（P90 约 15ms，717 节课线性扫描） */
    List<Hit> search(String text) {
        QueryTerms q = QueryTerms.of(text);
        List<Set<String>> groups = synonyms.groupsIn(q.normalized);
        List<String> looseGrams = q.grams.stream()
                .filter(g -> groups.stream().noneMatch(grp -> grp.stream().anyMatch(t -> t.contains(g))))
                .toList();
        List<String> chars = q.chars.stream().filter(c -> WEAK_CHARS.indexOf(c.charAt(0)) < 0).toList();

        List<Hit> hits = new ArrayList<>();
        for (CourseDoc d : snapshot.docs()) {
            double score = 0;
            for (Set<String> group : groups) {
                score += SYNONYM_BOOST * fieldScore(d, group);
            }
            for (String g : looseGrams) {
                score += fieldScore(d, Set.of(g));
            }
            for (String c : chars) {
                score += CHAR_FACTOR * nameScore(d, c);
            }
            if (containsName(q.normalized, d.titleText()) || containsName(q.normalized, d.unitText())) {
                score += W_TITLE_EXACT;
            }
            // 点主题入口时前端把主题名当 text 发来，必须落到该主题
            if (containsName(q.normalized, d.themeText())) {
                score += W_TITLE_EXACT;
            }
            if (d.quiz()) score *= QUIZ_FACTOR;
            if (score > 0) hits.add(new Hit(d, score));
        }
        hits.sort(Comparator.comparingDouble(Hit::score).reversed()
                .thenComparing(h -> h.doc().quiz())
                .thenComparing(h -> h.doc().orderKey(), CourseIndex::compareKeys));
        return hits;
    }

    /** 某主题的入口信息；主题下没有可学课程时为 null */
    ThemeEntry theme(int themeId) {
        return snapshot.themes().get(themeId);
    }

    /** 按名称找主题入口（热门主题用），找不到为 null */
    ThemeEntry themeByName(String name) {
        return snapshot.themes().values().stream()
                .filter(t -> QueryTerms.normalize(t.name()).equals(QueryTerms.normalize(name)))
                .findFirst().orElse(null);
    }

    /** 全部主题入口，按学科、主题顺序 */
    List<ThemeEntry> allThemes() {
        return snapshot.themes().values().stream()
                .sorted(Comparator.comparing(ThemeEntry::orderKey, CourseIndex::compareKeys)).toList();
    }

    int size() {
        return snapshot.docs().size();
    }

    // ---------- 打分 ----------

    /** 一个查询概念（同义词组或 2-gram）只取命中字段里权重最高的一项，避免长文本课程靠累加取胜 */
    private static double fieldScore(CourseDoc d, Set<String> terms) {
        if (containsAny(d.themeText(), terms)) return W_THEME;
        if (containsAny(d.lessonText(), terms)) return W_LESSON;
        if (containsAny(d.unitText(), terms)) return W_UNIT;
        if (containsAny(d.itemText(), terms)) return W_ITEM;
        return 0;
    }

    private static double nameScore(CourseDoc d, String c) {
        double s = 0;
        if (d.themeText().contains(c)) s += W_THEME;
        if (d.lessonText().contains(c)) s += W_LESSON;
        return s;
    }

    /** 名称 ≥2 字且被查询完整包含（绘本的书名就是单元名，如“小海龟回家”） */
    private static boolean containsName(String query, String name) {
        return name.length() >= 2 && query.contains(name);
    }

    private static boolean containsAny(String field, Set<String> terms) {
        for (String t : terms) {
            if (field.contains(t)) return true;
        }
        return false;
    }

    // ---------- 建索引 ----------

    /** 可学的课返回解析后的 content，否则 null（解析失败、items 为空、绘本校验不过） */
    private JsonNode parseLearnable(Lesson lesson) {
        String content = lesson.getContent();
        if (content == null || content.isBlank() || lesson.getType() == null) return null;
        try {
            JsonNode root = mapper.readTree(content);
            JsonNode items = root.path("items");
            if (!items.isArray() || items.isEmpty()) return null;
            // 复用学习页同一套解析与绘本校验，校验失败抛 IllegalArgumentException
            imageResolver.resolveContent(content);
            return root;
        } catch (Exception e) {
            log.warn("课程 {} 内容异常，不进入检索索引：{}", lesson.getId(), e.getMessage());
            return null;
        }
    }

    /** route 只由服务端生成；绘本课跳绘本页并定位到该课，其余跳课程页（带主题上下文，供学完返回） */
    private static String route(Lesson lesson, Unit unit, Theme theme, Subject subject, JsonNode root) {
        if (root.path("picturebook").asBoolean(false)) {
            return "/picturebooks/" + unit.getId() + "?lesson=" + lesson.getId();
        }
        return "/lesson/" + lesson.getId() + "?themeId=" + theme.getId() + "&unitId=" + unit.getId()
                + "&subjectId=" + subject.getId();
    }

    private static String itemText(LessonType type, JsonNode root) {
        if (type != LessonType.READING && type != LessonType.WORD) return "";
        StringBuilder sb = new StringBuilder();
        for (JsonNode item : root.path("items")) {
            for (String f : List.of("title", "word", "translation")) {
                String v = item.path(f).asText("");
                if (!v.isBlank()) sb.append(v).append(' ');
            }
        }
        return sb.toString();
    }

    private String firstExistingItemImage(JsonNode root, Map<String, Boolean> fileCache) {
        for (JsonNode item : root.path("items")) {
            String key = item.path("image").asText("");
            if (key.isBlank()) continue;
            String url = imageResolver.resolveUrl(key);
            if (fileExists(stripBase(url), fileCache)) return url;
        }
        return null;
    }

    /** 主题图标等站内绝对路径：文件存在才返回 */
    private String existingUrl(String path, Map<String, Boolean> fileCache) {
        if (path == null || path.isBlank() || !path.startsWith("/")) return null;
        return fileExists(path, fileCache) ? path : null;
    }

    private String stripBase(String url) {
        String base = Objects.requireNonNullElse(imageProperties.getBaseUrl(), "");
        return url.startsWith(base) ? url.substring(base.length()) : url;
    }

    private static boolean fileExists(String path, Map<String, Boolean> cache) {
        if (path.contains("..")) return false;
        return cache.computeIfAbsent(path, p -> new ClassPathResource("static" + p).exists());
    }

    private static <T> Map<Integer, T> byId(List<T> list, Function<T, Integer> id) {
        return list.stream().collect(Collectors.toMap(id, Function.identity(), (a, b) -> a));
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }

    static int compareKeys(List<Integer> a, List<Integer> b) {
        for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
            int c = Integer.compare(a.get(i), b.get(i));
            if (c != 0) return c;
        }
        return Integer.compare(a.size(), b.size());
    }
}
