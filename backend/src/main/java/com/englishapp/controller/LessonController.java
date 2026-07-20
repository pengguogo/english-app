package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.LessonDto;
import com.englishapp.service.LessonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课时 REST 控制器
 * <p>
 * 提供课时列表与课时详情查询接口,所有响应统一使用 {@link Result} 包装。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/lessons")
public class LessonController {

    private final LessonService lessonService;

    /**
     * 构造课时控制器
     *
     * @param lessonService 课时业务接口
     */
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    /**
     * 获取某单元下的所有课时
     *
     * @param unitId 单元 ID
     * @return 统一响应,包含课时 DTO 列表
     */
    @GetMapping("/unit/{unitId}")
    public Result<List<LessonDto>> getLessonsByUnit(@PathVariable Integer unitId) {
        return Result.success(lessonService.getLessonsByUnitId(unitId));
    }

    /**
     * 根据 ID 获取课时详情(含学习内容 JSON)
     *
     * @param id 课时 ID
     * @return 统一响应,包含课时 DTO
     */
    @GetMapping("/{id}")
    public Result<LessonDto> getLessonById(@PathVariable Integer id) {
        return Result.success(lessonService.getLessonById(id));
    }
}
