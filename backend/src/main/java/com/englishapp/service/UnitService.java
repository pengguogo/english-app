package com.englishapp.service;

import com.englishapp.dto.UnitDto;

import java.util.List;

/**
 * 单元业务接口
 * <p>
 * 定义与单元相关的业务操作,主要提供按主题查询单元列表(带进度)的能力。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
public interface UnitService {

    /**
     * 获取某主题下所有单元(带进度)
     * <p>
     * 返回的每个单元包含课程总数与当前用户已完成课程数。
     * </p>
     *
     * @param themeId 主题 ID
     * @param userId  用户 ID(为 null 时使用默认用户)
     * @return 单元 DTO 列表,按排序序号升序
     */
    List<UnitDto> getUnitsByTheme(Integer themeId, Integer userId);
}
