package com.englishapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 注册 / 登录请求：邮箱格式与密码长度在入口校验（BCrypt 最多 72 字节） */
public record AuthRequestDTO(
        @NotBlank @Email @Pattern(regexp = ".+@.+\\..+") @Size(max = 254) String email,
        @NotNull @Size(min = 8, max = 72) String password) { }
