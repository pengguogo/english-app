package com.englishapp.dto;

/**
 * 学科数据传输对象
 *
 * @param id 学科 ID
 * @param name 学科名称
 * @param code 学科代码
 * @param iconUrl 学科图标 URL
 * @param color 学科主题色
 * @param sortOrder 排序序号
 * @param isLocked 是否锁定
 * @author TRAE Agent
 * @since 2026-07-21
 */
public record SubjectDto(
    Integer id,
    String name,
    String code,
    String iconUrl,
    String color,
    Integer sortOrder,
    Boolean isLocked
) {}
