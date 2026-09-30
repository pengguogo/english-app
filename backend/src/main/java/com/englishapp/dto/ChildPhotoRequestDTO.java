package com.englishapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 儿童成长照片修改请求体。
 *
 * @param category 照片分类(可选,最长 50 字)
 */
public record ChildPhotoRequestDTO(@NotNull @PastOrPresent LocalDate takenAt,
        @Size(max = 200) String caption,
        @Size(max = 50) String category) { }
