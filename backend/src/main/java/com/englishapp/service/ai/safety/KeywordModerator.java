package com.englishapp.service.ai.safety;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 关键词版内容审核（classpath:agent/safety/keywords.yml + replies.yml）。
 * 输入判断顺序：陌生人 → 情绪 → 家人离世 → 个人信息 → 其余分类（按词表顺序）→ 自然死亡（放行）。
 * 输出更严：除 DEATH_NATURE 外任何分类命中，或含链接 / 邮箱 / 号码 / 引导联系，都判 SAFE_REPLY。
 * 日志只记分类，不记原文。
 */
@Component
public class KeywordModerator implements ContentModerator {

    private static final Logger log = LoggerFactory.getLogger(KeywordModerator.class);
    static final String RESOURCE = "agent/safety/keywords.yml";

    /** 链接：在保留符号的文本上匹配 */
    private static final Pattern URL = Pattern.compile(
            "https?://|www\\.|[a-z0-9-]+\\.(com|cn|net|org|io|cc|top|xyz|me|app|link|vip)\\b");

    private final SafeReplies replies;
    private final TextNormalizer normalizer;
    private final Map<SafetyCategory, KeywordRule> rules = new EnumMap<>(SafetyCategory.class);
    /** 词表里分类的书写顺序 = 判断顺序 */
    private final List<SafetyCategory> order;
    private final DeathRule death;
    private final KeywordRule outputContact;
    private final KeywordRule outputDanger;

    public KeywordModerator(SafeReplies replies) {
        this.replies = replies;
        Map<String, Object> root = YamlResource.load(RESOURCE);
        Map<String, Object> norm = YamlResource.map(root, "normalize");
        Map<String, String> repl = new LinkedHashMap<>();
        YamlResource.map(norm, "replacements").forEach((k, v) -> repl.put(String.valueOf(k), String.valueOf(v)));
        this.normalizer = new TextNormalizer(repl);

        Map<String, Object> cats = YamlResource.map(root, "categories");
        for (String name : cats.keySet()) {
            rules.put(SafetyCategory.valueOf(name), KeywordRule.of(YamlResource.map(cats, name)));
        }
        this.order = List.copyOf(rules.keySet().stream()
                .sorted((a, b) -> indexOf(cats, a) - indexOf(cats, b)).toList());
        this.death = new DeathRule(YamlResource.map(root, "death"), YamlResource.list(norm, "deathIdioms"));
        Map<String, Object> output = YamlResource.map(root, "output");
        this.outputContact = KeywordRule.of(YamlResource.map(output, "contact"));
        this.outputDanger = KeywordRule.of(YamlResource.map(output, "danger"));
    }

    @Override
    public ModerationResult checkInput(String text) {
        if (text == null || text.isBlank()) {
            return ModerationResult.allow();
        }
        SafetyCategory c = classify(text);
        if (c == SafetyCategory.NONE || c == SafetyCategory.DEATH_NATURE) {
            return ModerationResult.allow(c);
        }
        SafeReplies.Entry e = replies.forCategory(c);
        log.info("safety input blocked category={}", c);
        return ModerationResult.safeReply(c, e.reply(), e.flagParent());
    }

    @Override
    public ModerationResult checkOutput(String... texts) {
        if (texts == null) {
            return ModerationResult.allow();
        }
        SafetyCategory natural = SafetyCategory.NONE;
        for (String t : texts) {
            if (t == null || t.isBlank()) {
                continue;
            }
            SafetyCategory c = classify(t);
            if (c == SafetyCategory.NONE || c == SafetyCategory.DEATH_NATURE) {
                c = outputOnly(t, c);
            }
            if (c == SafetyCategory.DEATH_NATURE) {
                natural = c;
            } else if (c != SafetyCategory.NONE) {
                log.info("safety output blocked category={}", c);
                SafeReplies.Entry e = replies.outputBlocked();
                return ModerationResult.safeReply(c, e.reply(), e.flagParent());
            }
        }
        return ModerationResult.allow(natural);
    }

    /** 单段文本分类，不含输出专用规则 */
    SafetyCategory classify(String text) {
        String folded = TextNormalizer.fold(text);
        String n = normalizer.normalize(text);
        for (SafetyCategory c : List.of(SafetyCategory.STRANGER, SafetyCategory.EMOTION)) {
            if (rules.containsKey(c) && rules.get(c).matches(n)) {
                return c;
            }
        }
        SafetyCategory d = death.classify(n);
        if (d == SafetyCategory.DEATH_FAMILY) {
            return d;
        }
        if (PersonalInfoDetector.contains(folded, n)) {
            return SafetyCategory.PERSONAL_INFO;
        }
        for (SafetyCategory c : order) {
            if (rules.get(c).matches(n)) {
                return c;
            }
        }
        return d;
    }

    /** 输出专用规则：链接 / 邮箱 / 引导联系 → PERSONAL_INFO；危险物品 → DANGER；否则原样返回 */
    private SafetyCategory outputOnly(String text, SafetyCategory current) {
        // 去掉空白再匹配：防“w w w . x x . c o m”这类拆开写的链接
        String folded = TextNormalizer.fold(text).replaceAll("\\s+", "");
        String n = normalizer.normalize(text);
        if (URL.matcher(folded).find() || PersonalInfoDetector.EMAIL.matcher(folded).find() || outputContact.matches(n)) {
            return SafetyCategory.PERSONAL_INFO;
        }
        return outputDanger.matches(n) ? SafetyCategory.DANGER : current;
    }

    private static int indexOf(Map<String, Object> cats, SafetyCategory c) {
        return List.copyOf(cats.keySet()).indexOf(c.name());
    }
}
