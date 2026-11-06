package com.englishapp.dto;

import jakarta.validation.constraints.NotBlank;

/** 家长二次验证：家长登录密码 */
public record ParentVerifyRequestDTO(@NotBlank String password) { }
