package com.englishapp.dto;

import java.util.List;

/**
 * 错题统计 DTO
 * <p>
 * 用于错题统计接口返回汇总数据,包含错题总数、未掌握数、已掌握数,
 * 以及按题目类型的分类统计。
 * </p>
 *
 * @param total     错题总数
 * @param unresolved 未掌握错题数
 * @param resolved  已掌握错题数
 * @param byType    按题目类型分类的错题数量列表
 * @author englishapp
 * @since 1.2.0
 */
public record WrongAnswerStatsDto(
        Long total,
        Long unresolved,
        Long resolved,
        List<TypeCount> byType
) {
    /**
     * 单个题目类型的错题数量
     *
     * @param type  题目类型
     * @param count 该类型的错题数量
     */
    public record TypeCount(String type, Long count) {}
}
