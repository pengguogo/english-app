package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.ThemeDto;
import com.englishapp.service.ThemeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 主题 REST 控制器
 * <p>
 * 提供学习主题列表查询接口,所有响应统一使用 {@link Result} 包装。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/themes")
public class ThemeController {

    private final ThemeService themeService;

    /**
     * 构造主题控制器
     *
     * @param themeService 主题业务接口
     */
    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    /**
     * 获取所有学习主题列表
     *
     * @return 统一响应,包含主题 DTO 列表
     */
    @GetMapping
    public Result<List<ThemeDto>> getAllThemes() {
        return Result.success(themeService.getAllThemes());
    }
}
