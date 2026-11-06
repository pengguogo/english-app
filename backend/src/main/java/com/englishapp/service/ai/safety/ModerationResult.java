package com.englishapp.service.ai.safety;

/**
 * 审核结果。
 *
 * @param category   命中的分类，未命中为 NONE
 * @param action     ALLOW 可继续；SAFE_REPLY 必须改用 reply 作为固定话术，不展示/不发送原内容
 * @param reply      固定话术（≤40 字），ALLOW 时为 null
 * @param flagParent 是否需要标记给家长（阶段 2 只返回，不落库）
 */
public record ModerationResult(SafetyCategory category, Action action, String reply, boolean flagParent) {

    public enum Action { ALLOW, SAFE_REPLY }

    private static final ModerationResult ALLOW_NONE =
            new ModerationResult(SafetyCategory.NONE, Action.ALLOW, null, false);

    public static ModerationResult allow() {
        return ALLOW_NONE;
    }

    /** 允许但带分类（目前只有 DEATH_NATURE：允许温和回答） */
    public static ModerationResult allow(SafetyCategory category) {
        return new ModerationResult(category, Action.ALLOW, null, false);
    }

    public static ModerationResult safeReply(SafetyCategory category, String reply, boolean flagParent) {
        return new ModerationResult(category, Action.SAFE_REPLY, reply, flagParent);
    }

    public boolean allowed() {
        return action == Action.ALLOW;
    }
}
