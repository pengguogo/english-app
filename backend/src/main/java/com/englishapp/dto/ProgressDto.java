package com.englishapp.dto;

/**
 * 课时进度 DTO
 * @author englishapp
 * @since 1.0.0
 */
public class ProgressDto {
    private Integer lessonId;
    private String status;  // LOCKED / IN_PROGRESS / COMPLETED
    private Integer stars;
    private Integer score;

    public ProgressDto(Integer lessonId, String status, Integer stars, Integer score) {
        this.lessonId = lessonId;
        this.status = status;
        this.stars = stars;
        this.score = score;
    }

    public Integer getLessonId() { return lessonId; }
    public String getStatus() { return status; }
    public Integer getStars() { return stars; }
    public Integer getScore() { return score; }
}
