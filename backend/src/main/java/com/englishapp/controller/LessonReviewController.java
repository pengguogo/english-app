package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.ReviewQuestionDto;
import com.englishapp.dto.ReviewResultDto;
import com.englishapp.dto.ReviewSubmitRequest;
import com.englishapp.security.CurrentChild;
import com.englishapp.service.LessonReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons/{lessonId}/review")
public class LessonReviewController {
    private final LessonReviewService reviews;
    private final CurrentChild currentChild;

    public LessonReviewController(LessonReviewService reviews, CurrentChild currentChild) {
        this.reviews = reviews;
        this.currentChild = currentChild;
    }

    @GetMapping
    public Result<List<ReviewQuestionDto>> questions(@PathVariable Integer lessonId) {
        return Result.success(reviews.questions(lessonId));
    }

    /** 公开接口:游客只判分不落库,已选孩子时记录本课复习通过 */
    @PostMapping
    public Result<ReviewResultDto> submit(@PathVariable Integer lessonId,
                                          @Valid @RequestBody ReviewSubmitRequest request,
                                          HttpServletRequest http) {
        Integer childId = currentChild.currentChildId().orElse(null);
        ReviewResultDto result = reviews.submit(childId, lessonId, request.answers());
        if (childId == null && result.passed()) {
            GuidedLearningController.confirmGuestReview(http.getSession(false), lessonId);
        }
        return Result.success(result);
    }
}
