package com.englishapp.service.ai.safety;

import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 固定话术（classpath:agent/safety/replies.yml，照抄设计 6.1）。启动时校验每个需要话术的分类都有、且 ≤40 字。 */
@Component
public class SafeReplies {

    static final String RESOURCE = "agent/safety/replies.yml";
    static final int MAX_REPLY = 40;

    /** 话术条目 */
    public record Entry(String reply, boolean flagParent) {
    }

    private final Map<SafetyCategory, Entry> byCategory = new EnumMap<>(SafetyCategory.class);
    private final Entry outputBlocked;

    public SafeReplies() {
        Map<String, Object> root = YamlResource.load(RESOURCE);
        for (SafetyCategory c : SafetyCategory.values()) {
            if (c != SafetyCategory.NONE && c != SafetyCategory.DEATH_NATURE) {
                byCategory.put(c, entry(root, c.name()));
            }
        }
        this.outputBlocked = entry(root, "OUTPUT_BLOCKED");
    }

    /** 输入命中分类时的话术 */
    public Entry forCategory(SafetyCategory category) {
        Entry e = byCategory.get(category);
        if (e == null) {
            throw new IllegalArgumentException("分类没有固定话术：" + category);
        }
        return e;
    }

    /** 输出审核未通过时的统一话术 */
    public Entry outputBlocked() {
        return outputBlocked;
    }

    private static Entry entry(Map<String, Object> root, String key) {
        Map<String, Object> node = YamlResource.map(root, key);
        Object reply = node.get("reply");
        if (!(reply instanceof String s) || s.isBlank() || s.codePointCount(0, s.length()) > MAX_REPLY) {
            throw new IllegalStateException(RESOURCE + " 话术缺失或超过 " + MAX_REPLY + " 字：" + key);
        }
        return new Entry(s.trim(), Boolean.TRUE.equals(node.get("flagParent")));
    }
}
