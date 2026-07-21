package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.SubjectDto;
import com.englishapp.service.SubjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学科 REST 控制器
 * <p>
 * 提供学科列表查询接口，是多学科学习平台的入口。
 * </p>
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@RestController
@RequestMapping("/api/v1/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * 构造学科控制器
     * @param subjectService 学科业务接口
     */
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /**
     * 获取所有学科列表
     * @return 统一响应，包含学科 DTO 列表
     */
    @GetMapping
    public Result<List<SubjectDto>> getAllSubjects() {
        return Result.success(subjectService.getAllSubjects());
    }
}
