package com.englishapp.dto;

/**
 * 主题数据传输对象
 *
 * @param id 主题 ID
 * @param name 主题名称
 * @param iconUrl 主题图标 URL
 * @param sortOrder 排序序号
 * @param isLocked 是否锁定
 * @param subjectId 所属学科 ID
 * @author englishapp
 * @since 1.0.0
 */
public record ThemeDto(
    Integer id,
    String name,
    String iconUrl,
    Integer sortOrder,
    Boolean isLocked,
    Integer subjectId
) {}
