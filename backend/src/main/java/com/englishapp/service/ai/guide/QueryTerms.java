package com.englishapp.service.ai.guide;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询切分：归一化 → 去口语填充词 → 中文 2-gram + 单字 + 英文单词。
 * 同义词组在归一化后的原句上匹配（不去填充词），避免“看病”“讲故事”这类词被拆坏。
 */
final class QueryTerms {

    /** 口语填充词，长的在前，按顺序整体删除 */
    private static final List<String> FILLERS = List.of(
            "我想看看", "我想听听", "我想学学", "我想要看", "我想看", "我想学", "我想听", "我想玩", "我想要",
            "我要看", "我要学", "我要听", "我要玩", "有没有", "能不能", "可不可以", "可以", "给我", "带我",
            "关于", "有关", "一下", "今天", "现在", "等会", "好不好", "怎么样", "讲讲", "看看", "听听", "学学", "想看", "想学", "想听", "想玩", "想要",
            "我想", "我要", "什么", "东西", "一些", "的课", "课程", "小老鼠", "米米", "mimi");

    /** 可以安全删掉的单字（语气词、代词、结构助词、明显的动词） */
    private static final String FILLER_CHARS = "吗呢啊呀吧哦嘛的了我你他她讲去看听玩要想";

    private static final Pattern NON_TEXT = Pattern.compile("[\\p{P}\\p{S}\\s]+");
    private static final Pattern ENGLISH = Pattern.compile("[a-z]{3,}");
    private static final Pattern CJK = Pattern.compile("\\p{IsHan}+");

    final String normalized;
    final Set<String> grams;
    final Set<String> chars;

    private QueryTerms(String normalized, Set<String> grams, Set<String> chars) {
        this.normalized = normalized;
        this.grams = grams;
        this.chars = chars;
    }

    static String normalize(String text) {
        return text == null ? "" : NON_TEXT.matcher(text.toLowerCase(Locale.ROOT)).replaceAll("");
    }

    static QueryTerms of(String text) {
        String normalized = normalize(text);
        String core = normalized;
        for (String filler : FILLERS) {
            core = core.replace(filler, " ");
        }
        Set<String> grams = new LinkedHashSet<>();
        Set<String> chars = new LinkedHashSet<>();
        Matcher en = ENGLISH.matcher(core);
        while (en.find()) {
            grams.add(en.group());
        }
        Matcher han = CJK.matcher(core);
        while (han.find()) {
            String run = stripFillerChars(han.group());
            for (int i = 0; i + 2 <= run.length(); i++) {
                grams.add(run.substring(i, i + 2));
            }
            for (int i = 0; i < run.length(); i++) {
                chars.add(run.substring(i, i + 1));
            }
        }
        return new QueryTerms(normalized, grams, chars);
    }

    /** 只从连续汉字片段的首尾剥填充字，中间的字保留（“火车的零件”里的“的”不影响 2-gram 以外的匹配） */
    private static String stripFillerChars(String run) {
        int start = 0;
        int end = run.length();
        while (start < end && FILLER_CHARS.indexOf(run.charAt(start)) >= 0) start++;
        while (end > start && FILLER_CHARS.indexOf(run.charAt(end - 1)) >= 0) end--;
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            char c = run.charAt(i);
            // 中间的“的/了”去掉，让“火车的零件”与“火车零件”切出相同的词
            if (c != '的' && c != '了') sb.append(c);
        }
        return sb.toString();
    }
}
