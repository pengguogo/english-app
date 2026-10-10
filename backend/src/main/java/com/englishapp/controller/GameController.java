package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.GameAccessDto;
import com.englishapp.dto.StudyTimeRequest;
import com.englishapp.dto.GameUnlockDto;
import com.englishapp.dto.GameUnlockRequest;
import com.englishapp.service.GameAccessService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    private final GameAccessService gameAccessService;

    public GameController(GameAccessService gameAccessService) {
        this.gameAccessService = gameAccessService;
    }

    @GetMapping("/access")
    public Result<GameAccessDto> getAccess(
            @RequestParam(defaultValue = "1") Integer userId) {
        return Result.success(gameAccessService.getTodayAccess(userId));
    }

    @PostMapping("/study-time")
    public Result<GameAccessDto> recordStudyTime(
            @Valid @RequestBody StudyTimeRequest request,
            @RequestParam(defaultValue = "1") Integer userId) {
        return Result.success(gameAccessService.recordPendingStudyTime(userId, request.getLessonId(), request.getSeconds(), request.getEventId()));
    }

    @PostMapping("/unlock")
    public Result<GameUnlockDto> unlock(
            @Valid @RequestBody GameUnlockRequest request,
            @RequestParam(defaultValue = "1") Integer userId) {
        return Result.success(gameAccessService.unlockWithPassword(userId, request.getPassword()));
    }
}
