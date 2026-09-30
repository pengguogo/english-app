package com.englishapp.dto;

import java.time.LocalDate;

/**
 * 儿童成长照片响应。
 *
 * @param id 照片 id
 * @param takenAt 拍摄日期
 * @param caption 说明文字
 * @param category 分类标签(可为空)
 * @param imageUrl 原图访问地址
 * @param thumbnailUrl 缩略图访问地址
 */
public record ChildPhotoResponseDTO(Integer id, LocalDate takenAt, String caption, String category,
        String imageUrl, String thumbnailUrl) { }
