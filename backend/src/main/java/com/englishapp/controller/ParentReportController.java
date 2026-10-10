package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.WeekReportResponseDTO;
import com.englishapp.service.LearningInsightsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parent-settings")
public class ParentReportController {
    private final LearningInsightsService insights;
    public ParentReportController(LearningInsightsService insights) { this.insights = insights; }
    @GetMapping("/week")
    public Result<WeekReportResponseDTO> week() { return Result.success(insights.week()); }
}
