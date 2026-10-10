package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.ParentPasswordRequestDTO;
import com.englishapp.service.ParentPasswordService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parent-settings")
public class ParentSettingsController {
    private final ParentPasswordService passwords;
    public ParentSettingsController(ParentPasswordService passwords) { this.passwords = passwords; }

    @GetMapping("/password")
    public Result<PasswordStatus> status() { return Result.success(new PasswordStatus(passwords.isConfigured())); }

    @PutMapping("/password")
    public Result<PasswordStatus> set(@Valid @RequestBody ParentPasswordRequestDTO request) {
        passwords.setPassword(request.password());
        return status();
    }

    public record PasswordStatus(boolean configured) {}
}
