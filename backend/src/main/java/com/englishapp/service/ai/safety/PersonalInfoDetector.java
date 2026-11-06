package com.englishapp.service.ai.safety;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 个人信息识别：手机号、长数字（身份证 / 座机 / 门牌）、邮箱、门牌地址、“我家在 / 我住在 / 我的学校是”这类说法。
 * 只回答“有没有”，不返回命中的内容，调用方也不得复述。
 */
final class PersonalInfoDetector {

    /** 归一化文本里的连续 ≥7 位数字：手机号、身份证、座机都会命中 */
    private static final Pattern LONG_DIGITS = Pattern.compile("\\d{7,}");
    /** 中文数字说的手机号（语音识别常见）：转成阿拉伯数字后匹配 */
    private static final Pattern MOBILE = Pattern.compile("1[3-9]\\d{9}");
    /** 邮箱：要在保留符号的文本上匹配 */
    static final Pattern EMAIL = Pattern.compile("[a-z0-9._%+-]+@[a-z0-9-]+(\\.[a-z0-9-]+)+");

    /** 归一化文本上的说法与地址规则 */
    private static final List<Pattern> PHRASES = List.of(
            Pattern.compile("我家(在|住)|我住在|我住的地方|我(家)?的?地址|家庭住址|门牌"),
            Pattern.compile("(我|我妈妈|我爸爸|妈妈|爸爸)的?(电话|手机)|电话号码是|手机号是|身份证"),
            Pattern.compile("我的(学校|幼儿园|小学|班级)|我们(学校|幼儿园|小学)(叫|是|在)|(学校|幼儿园|小学)(叫|在|名字)"),
            Pattern.compile("我(上|读)的(是)?.{0,8}(学校|幼儿园|小学)|我在.{1,8}(幼儿园|小学)(上学|读书)"),
            Pattern.compile("我叫(?!你|他|她|它|什么|啥|妈|爸)[\\u4e00-\\u9fa5]{2,4}|我的名字(是|叫)|我的全名"),
            Pattern.compile("小区|\\d+(号楼|栋|幢|室|号院)|[一二三四五六七八九十百零]+号楼|(路|街|巷|大道|胡同|里)[\\d一二三四五六七八九十百零]+号"));

    private PersonalInfoDetector() {
    }

    /**
     * @param folded     NFKC + 小写、保留符号的文本
     * @param normalized 归一化文本（无空格标点）
     */
    static boolean contains(String folded, String normalized) {
        if (EMAIL.matcher(folded).find() || LONG_DIGITS.matcher(normalized).find()) {
            return true;
        }
        if (MOBILE.matcher(chineseDigits(normalized)).find()) {
            return true;
        }
        for (Pattern p : PHRASES) {
            if (p.matcher(normalized).find()) {
                return true;
            }
        }
        return false;
    }

    /** 中文数字字符逐个换成阿拉伯数字（只用于手机号识别，“十”不换，避免“一二三…十”数数被误判） */
    static String chineseDigits(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            int d = "零一二三四五六七八九".indexOf(c);
            if (c == '〇') {
                d = 0;
            } else if (c == '幺') {
                d = 1;
            }
            sb.append(d >= 0 ? (char) ('0' + d) : c);
        }
        return sb.toString();
    }
}
