package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.WeekReportResponseDTO;
import com.englishapp.security.CurrentChild;
import com.englishapp.service.LearningInsightsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parent-settings")
public class ParentReportController {
    private final LearningInsightsService insights;
    private final CurrentChild currentChild;
    public ParentReportController(LearningInsightsService insights, CurrentChild currentChild) {
        this.insights = insights;
        this.currentChild = currentChild;
    }
    /** 家长区:先校验家长二次验证 */
    @GetMapping("/week")
    public Result<WeekReportResponseDTO> week() {
        currentChild.requireParentVerified();
        return Result.success(insights.week(currentChild.requireChildId()));
    }
}
