package com.englishapp.service.ai.safety;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** 一个分类的词表：子串词 + 正则，都在归一化文本上匹配。 */
record KeywordRule(List<String> terms, List<Pattern> patterns) {

    static KeywordRule of(Map<String, Object> node) {
        List<Pattern> ps = YamlResource.list(node, "patterns").stream().map(Pattern::compile).toList();
        return new KeywordRule(YamlResource.list(node, "terms"), ps);
    }

    boolean matches(String normalized) {
        for (String t : terms) {
            if (normalized.contains(t)) {
                return true;
            }
        }
        for (Pattern p : patterns) {
            if (p.matcher(normalized).find()) {
                return true;
            }
        }
        return false;
    }

    static boolean containsAny(String text, List<String> words) {
        for (String w : words) {
            if (text.contains(w)) {
                return true;
            }
        }
        return false;
    }
}
