package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.ReviewQuestionDto;
import com.englishapp.dto.ReviewResultDto;
import com.englishapp.dto.ReviewSubmitRequest;
import com.englishapp.service.LessonReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons/{lessonId}/review")
public class LessonReviewController {
    private final LessonReviewService reviews;

    public LessonReviewController(LessonReviewService reviews) { this.reviews = reviews; }

    @GetMapping
    public Result<List<ReviewQuestionDto>> questions(@PathVariable Integer lessonId) {
        return Result.success(reviews.questions(lessonId));
    }

    @PostMapping
    public Result<ReviewResultDto> submit(@PathVariable Integer lessonId, @RequestParam(defaultValue = "1") Integer userId,
                                          @Valid @RequestBody ReviewSubmitRequest request) {
        return Result.success(reviews.submit(userId, lessonId, request.answers()));
    }
}
