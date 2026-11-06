package com.englishapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 新建孩子档案；头像只接受站内路径或 https 地址 */
public record ChildRequestDTO(
        @NotBlank @Size(max = 50) String nickname,
        @Size(max = 255) @Pattern(regexp = "^(/|https://).*") String avatarUrl) { }
