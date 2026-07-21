package com.englishapp.domain.enums;

/**
 * 课时类型枚举
 * <p>
 * 定义不同的学习交互模式，前端根据类型分发到对应模板组件。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
public enum LessonType {
    /** 看图认字→听音→跟读评分（英语单词/语文汉字/数学数字） */
    WORD,
    /** 听音→跟读朗诵评分（英语句型/语文古诗） */
    SENTENCE,
    /** 图文翻页阅读（语文故事/课外科普） */
    READING,
    /** 选择题判对错（数学图形/应用题/课外问答） */
    QUIZ,
    /** 计算题判对错（数学加减运算） */
    CALCULATE,
    /** 保留，暂未使用 */
    GAME,
    /** 保留，暂未使用 */
    SPEAK
}
