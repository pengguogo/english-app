package com.englishapp.dto;

/**
 * 单元课时进度 DTO
 * <p>
 * 用于批量返回某单元下所有课时的进度信息,供前端课时列表渲染锁定/可学/已完成状态。
 * 与 {@link ProgressDto} 的区别:本 DTO 专为"按单元批量查询"场景设计,
 * 前端一次请求即可拿到整单元进度,避免 N+1 查询。
 * </p>
 *
 * @author englishapp
 * @since 1.1.0
 */
public record UnitProgressDto(
        Integer lessonId,
        String status,   // LOCKED / IN_PROGRESS / COMPLETED
        Integer stars,
        Integer score
) {}
