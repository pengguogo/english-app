package com.englishapp.service.ai.record;

import java.text.Normalizer;
import java.util.regex.Pattern;

/** 孩子原话入库前脱敏：邮箱、身份证、手机号与其他 ≥7 位长数字一律换成 ＊＊＊ */
final class Redactor {

    static final String MASK = "＊＊＊";
    static final String PERSONAL_INFO_PLACEHOLDER = "（孩子分享了个人信息，已隐藏）";

    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+");
    /** 身份证（末位可为 X）要先于长数字匹配，否则 X 会残留 */
    private static final Pattern ID_CARD = Pattern.compile("\\d{17}[\\dXx]");
    /** 手机号、座机、门牌等连续长数字；允许中间夹空格或短横线 */
    private static final Pattern LONG_DIGITS = Pattern.compile("\\d(?:[\\s-]?\\d){6,}");

    private Redactor() {
    }

    static String redact(String text) {
        if (text == null) return null;
        // 全角数字、全角 @ 先归一，避免绕过
        String s = Normalizer.normalize(text, Normalizer.Form.NFKC);
        s = EMAIL.matcher(s).replaceAll(MASK);
        s = ID_CARD.matcher(s).replaceAll(MASK);
        return LONG_DIGITS.matcher(s).replaceAll(MASK);
    }
}
