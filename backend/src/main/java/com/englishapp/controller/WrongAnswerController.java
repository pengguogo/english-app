package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.RecordWrongAnswerRequest;
import com.englishapp.dto.WrongAnswerDto;
import com.englishapp.dto.WrongAnswerStatsDto;
import com.englishapp.service.WrongAnswerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 错题集 REST 控制器
 * <p>
 * 提供错题的记录、查询、统计、标记掌握与删除接口。
 * </p>
 *
 * @author englishapp
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/v1/wrong-answers")
public class WrongAnswerController {

    private final WrongAnswerService wrongAnswerService;

    /**
     * 构造错题集控制器
     *
     * @param wrongAnswerService 错题业务服务
     */
    public WrongAnswerController(WrongAnswerService wrongAnswerService) {
        this.wrongAnswerService = wrongAnswerService;
    }

    /**
     * 记录错题
     * <p>
     * 前端在用户答错题目时调用此接口,后端会创建或更新错题记录。
     * </p>
     *
     * @param request 错题记录请求体(含课时、题号、类型、答案等)
     * @param userId  用户 ID(可选,默认 1)
     * @return 操作结果(data 为 null)
     */
    @PostMapping("/record")
    public Result<Void> recordWrongAnswer(
            @Valid @RequestBody RecordWrongAnswerRequest request,
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        wrongAnswerService.recordWrongAnswer(request, userId);
        return Result.success(null);
    }

    /**
     * 查询错题列表
     *
     * @param userId   用户 ID(可选,默认 1)
     * @param resolved 掌握状态筛选(可选):null=全部,true=已掌握,false=未掌握
     * @return 错题 DTO 列表(按最近答错时间降序)
     */
    @GetMapping
    public Result<List<WrongAnswerDto>> getWrongAnswers(
            @RequestParam(required = false, defaultValue = "1") Integer userId,
            @RequestParam(required = false) Boolean resolved) {
        return Result.success(wrongAnswerService.getWrongAnswers(userId, resolved));
    }

    /**
     * 获取错题统计
     *
     * @param userId 用户 ID(可选,默认 1)
     * @return 错题统计 DTO(含总数、未掌握数、已掌握数、按类型分类)
     */
    @GetMapping("/stats")
    public Result<WrongAnswerStatsDto> getWrongAnswerStats(
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        return Result.success(wrongAnswerService.getWrongAnswerStats(userId));
    }

    /**
     * 将指定错题标记为已掌握
     *
     * @param id     错题记录 ID
     * @param userId 用户 ID(可选,默认 1)
     * @return 操作结果(data 为 null)
     */
    @PatchMapping("/{id}/resolve")
    public Result<Void> markAsResolved(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        wrongAnswerService.markAsResolved(id, userId);
        return Result.success(null);
    }

    /**
     * 删除指定错题
     *
     * @param id     错题记录 ID
     * @param userId 用户 ID(可选,默认 1)
     * @return 操作结果(data 为 null)
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteWrongAnswer(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        wrongAnswerService.deleteWrongAnswer(id, userId);
        return Result.success(null);
    }
}
