package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.LearningAttemptRequestDTO;
import com.englishapp.dto.TodayTaskResponseDTO;
import com.englishapp.service.LearningAttemptService;
import com.englishapp.service.LearningInsightsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {
    private final LearningInsightsService insights;
    private final LearningAttemptService attempts;
    public LearningController(LearningInsightsService insights, LearningAttemptService attempts) {
        this.insights = insights;
        this.attempts = attempts;
    }
    @GetMapping("/today")
    public Result<TodayTaskResponseDTO> today() { return Result.success(insights.today()); }
    @PostMapping("/attempts")
    public Result<Void> record(@Valid @RequestBody LearningAttemptRequestDTO request) {
        attempts.record(request);
        return Result.success(null);
    }
}
