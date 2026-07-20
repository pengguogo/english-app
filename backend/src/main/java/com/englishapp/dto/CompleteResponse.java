package com.englishapp.dto;

/**
 * 完成课时响应(含下一课解锁信息)
 * @author englishapp
 * @since 1.0.0
 */
public class CompleteResponse {
    private Integer nextLessonId;
    private Boolean unlocked;

    public CompleteResponse(Integer nextLessonId, Boolean unlocked) {
        this.nextLessonId = nextLessonId;
        this.unlocked = unlocked;
    }

    public Integer getNextLessonId() { return nextLessonId; }
    public Boolean getUnlocked() { return unlocked; }
}
