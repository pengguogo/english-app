package com.englishapp.service.ai.safety;

/**
 * 内容审核（阶段 2：关键词过滤；以后可换阿里云 AI 安全护栏）。
 * 实现必须线程安全、不抛异常；拿不准一律从严；日志只记分类，不记原文。
 */
public interface ContentModerator {

    /** 审核孩子输入。命中 SAFE_REPLY 时不得把原文发给模型。null/空串返回 ALLOW。 */
    ModerationResult checkInput(String text);

    /**
     * 审核要展示的生成内容（讲解、题目、选项等，一起传入）。
     * 比输入更严：除 DEATH_NATURE 外任何分类命中、或含链接/电话/邮箱/引导联系，均为 SAFE_REPLY。
     */
    ModerationResult checkOutput(String... texts);
}
