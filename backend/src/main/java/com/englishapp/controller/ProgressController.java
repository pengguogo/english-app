package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.ProgressDto;
import com.englishapp.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 进度 REST 控制器
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    /**
     * 获取某课时进度
     * @param lessonId 课时 ID
     * @param userId 用户 ID(可选,默认 1)
     * @return 进度信息
     */
    @GetMapping("/lesson/{lessonId}")
    public Result<ProgressDto> getLessonProgress(
            @PathVariable Integer lessonId,
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        return Result.success(progressService.getLessonProgress(lessonId, userId));
    }

    /**
     * 完成课时并解锁下一课
     * @param lessonId 课时 ID
     * @param request 完成请求(含星星和分数)
     * @param userId 用户 ID(可选,默认 1)
     * @return 下一课解锁信息
     */
    @PostMapping("/lesson/{lessonId}/complete")
    public Result<CompleteResponse> completeLesson(
            @PathVariable Integer lessonId,
            @Valid @RequestBody CompleteRequest request,
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        return Result.success(progressService.completeLesson(lessonId, request, userId));
    }
}
