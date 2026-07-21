package com.englishapp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 学科实体
 * <p>
 * 多学科学习平台的最高层级，Theme 隶属于 Subject。
 * 目前包含英语、语文、数学、课外四个学科。
 * </p>
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 学科名称：英语/语文/数学/课外 */
    private String name;

    /** 学科代码：ENGLISH/CHINESE/MATH/EXTRACURRICULAR */
    private String code;

    /** 学科图标 URL */
    private String iconUrl;

    /** 学科主题色（前端卡片配色，如 #6B7CFF） */
    private String color;

    /** 排序序号 */
    private Integer sortOrder;

    /** 是否锁定 */
    private Boolean isLocked;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
