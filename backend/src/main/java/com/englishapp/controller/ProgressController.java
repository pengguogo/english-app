package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.LearnedLessonDto;
import com.englishapp.dto.LearnedStatsDto;
import com.englishapp.dto.ProgressDto;
import com.englishapp.dto.UnitProgressDto;
import com.englishapp.security.CurrentChild;
import com.englishapp.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 进度 REST 控制器
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final CurrentChild currentChild;

    public ProgressController(ProgressService progressService, CurrentChild currentChild) {
        this.progressService = progressService;
        this.currentChild = currentChild;
    }

    /**
     * 获取某课时进度
     * @param lessonId 课时 ID
     * @return 进度信息
     */
    @GetMapping("/lesson/{lessonId}")
    public Result<ProgressDto> getLessonProgress(
            @PathVariable Integer lessonId) {
        return Result.success(progressService.getLessonProgress(lessonId, currentChild.currentChildId().orElse(null)));
    }

    /**
     * 批量获取某单元下所有课时的进度
     * <p>
     * 供前端课时列表页一次性渲染整单元的锁定/可学/已完成状态。
     * </p>
     *
     * @param unitId 单元 ID
     * @return 该单元所有课时的进度列表
     */
    @GetMapping("/unit/{unitId}")
    public Result<List<UnitProgressDto>> getUnitProgress(
            @PathVariable Integer unitId) {
        return Result.success(progressService.getUnitProgress(unitId, currentChild.currentChildId().orElse(null)));
    }

    /**
     * 完成课时并解锁下一课
     * @param lessonId 课时 ID
     * @param request 完成请求(含星星和分数)
     * @return 下一课解锁信息
     */
    @PostMapping("/lesson/{lessonId}/complete")
    public Result<CompleteResponse> completeLesson(
            @PathVariable Integer lessonId,
            @Valid @RequestBody CompleteRequest request) {
        return Result.success(progressService.completeLesson(lessonId, request, currentChild.requireChildId()));
    }

    /**
     * 获取用户已学过的课时列表
     * <p>
     * 返回用户所有已完成的课时,包含课时→单元→主题→学科完整层级信息,
     * 按完成时间降序排列。
     * </p>
     *
     * @return 已学课时 DTO 列表
     */
    @GetMapping("/learned")
    public Result<List<LearnedLessonDto>> getLearnedLessons() {
        return Result.success(progressService.getLearnedLessons(currentChild.requireChildId()));
    }

    /**
     * 获取用户已学课时的统计信息
     * <p>
     * 返回已学总数、累计星星数、平均分数,以及按学科和课时类型的分类统计。
     * </p>
     *
     * @return 已学课时统计 DTO
     */
    @GetMapping("/learned/stats")
    public Result<LearnedStatsDto> getLearnedStats() {
        return Result.success(progressService.getLearnedStats(currentChild.requireChildId()));
    }
}
