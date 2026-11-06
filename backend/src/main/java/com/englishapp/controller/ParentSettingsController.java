package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.service.ParentPasswordService;
import org.springframework.web.bind.annotation.*;

/** 家长密码即家长登录密码，不再单独设置 */
@RestController
@RequestMapping("/api/v1/parent-settings")
public class ParentSettingsController {
    private final ParentPasswordService passwords;
    public ParentSettingsController(ParentPasswordService passwords) { this.passwords = passwords; }

    @GetMapping("/password")
    public Result<PasswordStatus> status() { return Result.success(new PasswordStatus(passwords.isConfigured())); }

    public record PasswordStatus(boolean configured) {}
}
