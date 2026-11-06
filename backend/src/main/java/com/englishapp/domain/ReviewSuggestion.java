package com.englishapp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** 复习建议：status = OPEN | DONE | DISMISSED；依据消失时由 LearningTopicService 重新计算并删除 */
@Entity
@Table(name = "review_suggestion")
public class ReviewSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer childId;
    private Integer themeId;
    private String basis;
    private String route;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getChildId() { return childId; }
    public void setChildId(Integer childId) { this.childId = childId; }
    public Integer getThemeId() { return themeId; }
    public void setThemeId(Integer themeId) { this.themeId = themeId; }
    public String getBasis() { return basis; }
    public void setBasis(String basis) { this.basis = basis; }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
