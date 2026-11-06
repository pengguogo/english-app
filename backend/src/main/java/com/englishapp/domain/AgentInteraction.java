package com.englishapp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** 答题记录：下发题目时插入（chosen/correct 为空 = 未作答），首次作答时写入，之后不改 */
@Entity
@Table(name = "agent_interaction")
public class AgentInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sessionId;
    private Integer childId;
    private Integer themeId;
    private Integer lessonId;
    private Integer itemIndex;
    private String question;
    /** [{id, text}] */
    private String optionsJson;
    private String chosen;
    private Boolean correct;
    /** sha256(quizToken) 十六进制 */
    private String quizTokenHash;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Integer getChildId() { return childId; }
    public void setChildId(Integer childId) { this.childId = childId; }
    public Integer getThemeId() { return themeId; }
    public void setThemeId(Integer themeId) { this.themeId = themeId; }
    public Integer getLessonId() { return lessonId; }
    public void setLessonId(Integer lessonId) { this.lessonId = lessonId; }
    public Integer getItemIndex() { return itemIndex; }
    public void setItemIndex(Integer itemIndex) { this.itemIndex = itemIndex; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getOptionsJson() { return optionsJson; }
    public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }
    public String getChosen() { return chosen; }
    public void setChosen(String chosen) { this.chosen = chosen; }
    public Boolean getCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    public String getQuizTokenHash() { return quizTokenHash; }
    public void setQuizTokenHash(String quizTokenHash) { this.quizTokenHash = quizTokenHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
