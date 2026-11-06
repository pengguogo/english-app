package com.englishapp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** 学习伙伴会话：30 分钟无活动结束；sourcePage = LESSON | FREE */
@Entity
@Table(name = "agent_session")
public class AgentSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer childId;
    private Long familyId;
    private String topic;
    private String sourcePage;
    private Integer lessonId;
    private boolean flagged;
    private String flagCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getChildId() { return childId; }
    public void setChildId(Integer childId) { this.childId = childId; }
    public Long getFamilyId() { return familyId; }
    public void setFamilyId(Long familyId) { this.familyId = familyId; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getSourcePage() { return sourcePage; }
    public void setSourcePage(String sourcePage) { this.sourcePage = sourcePage; }
    public Integer getLessonId() { return lessonId; }
    public void setLessonId(Integer lessonId) { this.lessonId = lessonId; }
    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
    public String getFlagCategory() { return flagCategory; }
    public void setFlagCategory(String flagCategory) { this.flagCategory = flagCategory; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
