package com.englishapp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 会话消息：role = CHILD | AGENT；moderation = NONE | SAFE_REPLY | OUTPUT_BLOCKED。
 * CHILD 存脱敏后的原话；AGENT 存实际展示的 speech（有讲解时前缀「【标题】」）。themeId 只在课程原文讲解时填。
 */
@Entity
@Table(name = "agent_message")
public class AgentMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sessionId;
    private String role;
    private String text;
    private String moderation;
    private String category;
    private Integer themeId;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getModeration() { return moderation; }
    public void setModeration(String moderation) { this.moderation = moderation; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getThemeId() { return themeId; }
    public void setThemeId(Integer themeId) { this.themeId = themeId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
