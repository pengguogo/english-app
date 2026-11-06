package com.englishapp.service.ai.safety;

/** 敏感话题分类，对应设计 v0.5 第 6.1 节。 */
public enum SafetyCategory {
    /** 未命中任何敏感分类 */
    NONE,
    /** 自然知识类的死亡（恐龙灭绝、落叶），允许温和回答 */
    DEATH_NATURE,
    /** 家人或宠物去世 */
    DEATH_FAMILY,
    /** 受伤与疾病 */
    INJURY,
    /** 危险行为 */
    DANGER,
    /** 身体隐私、生育相关 */
    BODY_PRIVACY,
    /** 地址、电话、学校等个人信息 */
    PERSONAL_INFO,
    /** 陌生人 */
    STRANGER,
    /** 暴力、恐怖、不雅语言 */
    VIOLENCE_PROFANITY,
    /** 情绪困扰 */
    EMOTION,
    /** 政治、宗教、新闻事件 */
    POLITICS_RELIGION,
    /** 提示注入 */
    PROMPT_INJECTION
}
