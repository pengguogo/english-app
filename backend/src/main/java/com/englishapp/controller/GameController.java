package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.GameAccessDto;
import com.englishapp.dto.StudyTimeRequest;
import com.englishapp.dto.GameUnlockDto;
import com.englishapp.dto.GameUnlockRequest;
import com.englishapp.security.CurrentChild;
import com.englishapp.service.GameAccessService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    private final GameAccessService gameAccessService;
    private final CurrentChild currentChild;

    public GameController(GameAccessService gameAccessService, CurrentChild currentChild) {
        this.gameAccessService = gameAccessService;
        this.currentChild = currentChild;
    }

    @GetMapping("/access")
    public Result<GameAccessDto> getAccess() {
        return Result.success(gameAccessService.getTodayAccess(currentChild.requireChildId()));
    }

    @PostMapping("/study-time")
    public Result<GameAccessDto> recordStudyTime(
            @Valid @RequestBody StudyTimeRequest request) {
        return Result.success(gameAccessService.recordPendingStudyTime(currentChild.requireChildId(), request.getLessonId(), request.getSeconds(), request.getEventId()));
    }

    @PostMapping("/unlock")
    public Result<GameUnlockDto> unlock(
            @Valid @RequestBody GameUnlockRequest request) {
        return Result.success(gameAccessService.unlockWithPassword(currentChild.requireChildId(), request.getPassword()));
    }
}
