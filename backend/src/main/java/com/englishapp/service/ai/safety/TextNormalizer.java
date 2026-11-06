package com.englishapp.service.ai.safety;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 输入归一：NFKC（全角转半角）→ 小写 → 只保留字母和数字（去空格、标点、表情、零宽字符）→ 同音 / 拆字替换。
 * 替换表来自 keywords.yml 的 normalize.replacements，是扩展点。
 */
final class TextNormalizer {

    private final Map<String, String> replacements;

    TextNormalizer(Map<String, String> replacements) {
        this.replacements = new LinkedHashMap<>(replacements);
    }

    /** NFKC + 小写，保留标点；给邮箱、链接这类依赖符号的规则用 */
    static String fold(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
    }

    String normalize(String text) {
        String folded = fold(text);
        StringBuilder sb = new StringBuilder(folded.length());
        folded.codePoints().filter(Character::isLetterOrDigit).forEach(sb::appendCodePoint);
        String out = sb.toString();
        for (Map.Entry<String, String> e : replacements.entrySet()) {
            out = out.replace(e.getKey(), e.getValue());
        }
        return out;
    }
}
