package com.englishapp.service.ai.guide;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

/**
 * 人工同义词表（classpath:agent/synonyms.yml）。
 * 孩子说出组内任一词，整组都参与匹配；表在启动时加载一次，之后只读。
 */
@Component
public class SynonymDictionary {

    private static final String RESOURCE = "agent/synonyms.yml";

    private final List<Set<String>> groups;
    private final List<String> hotTopics;

    public SynonymDictionary() {
        Map<String, Object> root = load();
        this.groups = parseGroups(root.get("groups"));
        this.hotTopics = parseList(root.get("hotTopics"));
    }

    /** 查询文本（已归一化为小写）里出现过的同义词组 */
    public List<Set<String>> groupsIn(String normalizedText) {
        List<Set<String>> hit = new ArrayList<>();
        for (Set<String> group : groups) {
            for (String term : group) {
                if (normalizedText.contains(term)) {
                    hit.add(group);
                    break;
                }
            }
        }
        return hit;
    }

    /** 热门主题名，顺序即展示顺序 */
    public List<String> hotTopics() {
        return hotTopics;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> load() {
        try (InputStream in = new ClassPathResource(RESOURCE).getInputStream()) {
            Object parsed = new Yaml().load(in);
            if (!(parsed instanceof Map<?, ?> map)) {
                throw new IllegalStateException(RESOURCE + " 格式错误：根节点必须是对象");
            }
            return (Map<String, Object>) map;
        } catch (IOException e) {
            throw new IllegalStateException("读取 " + RESOURCE + " 失败", e);
        }
    }

    private static List<Set<String>> parseGroups(Object raw) {
        if (!(raw instanceof List<?> list)) {
            throw new IllegalStateException(RESOURCE + " 缺少 groups 列表");
        }
        List<Set<String>> result = new ArrayList<>();
        for (Object g : list) {
            Set<String> group = new LinkedHashSet<>(parseList(g));
            if (group.size() >= 2) {
                result.add(Set.copyOf(group));
            }
        }
        return List.copyOf(result);
    }

    private static List<String> parseList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (Object o : list) {
            String s = String.valueOf(o).strip().toLowerCase(Locale.ROOT);
            if (!s.isEmpty()) {
                result.add(s);
            }
        }
        return List.copyOf(result);
    }
}
