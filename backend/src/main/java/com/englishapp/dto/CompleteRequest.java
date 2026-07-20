package com.englishapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 完成课时请求体
 * @author englishapp
 * @since 1.0.0
 */
public class CompleteRequest {
    @NotNull
    @Min(0) @Max(3)
    private Integer stars;

    @NotNull
    @Min(0) @Max(100)
    private Integer score;

    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
