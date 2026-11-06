package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.UnitDto;
import com.englishapp.security.CurrentChild;
import com.englishapp.service.UnitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 单元 REST 控制器
 * <p>
 * 提供单元列表查询接口,返回的单元数据包含课程总数与已完成课程数。
 * 所有响应统一使用 {@link Result} 包装。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/units")
public class UnitController {

    private final UnitService unitService;
    private final CurrentChild currentChild;

    /**
     * 构造单元控制器
     *
     * @param unitService  单元业务接口
     * @param currentChild 当前登录孩子
     */
    public UnitController(UnitService unitService, CurrentChild currentChild) {
        this.unitService = unitService;
        this.currentChild = currentChild;
    }

    /**
     * 获取某主题下所有单元(带进度)
     * <p>
     * 公开接口:已选孩子时带该孩子进度,游客或未选孩子时进度为 0。
     * </p>
     *
     * @param themeId 主题 ID
     * @return 统一响应,包含单元 DTO 列表
     */
    @GetMapping("/theme/{themeId}")
    public Result<List<UnitDto>> getUnitsByTheme(
            @PathVariable Integer themeId) {
        return Result.success(unitService.getUnitsByTheme(themeId, currentChild.currentChildId().orElse(null)));
    }
}
