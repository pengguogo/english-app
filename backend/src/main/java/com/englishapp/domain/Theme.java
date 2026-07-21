package com.englishapp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 学习主题实体
 * <p>
 * 主题隶属于学科（Subject），包含若干单元（Unit）。
 * 主题的锁定状态由业务层根据用户进度动态计算，不依赖数据库静态字段。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@Entity
@Table(name = "theme")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 主题名称 */
    private String name;

    /** 主题图标 URL */
    private String iconUrl;

    /** 排序序号 */
    private Integer sortOrder;

    /** 是否锁定（静态标记，实际锁定状态由业务层动态计算） */
    private Boolean isLocked;

    /** 所属学科 ID（外键，关联 subject 表） */
    private Integer subjectId;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
