package com.englishapp.dto;

import java.util.List;

/**
 * 已学课时统计 DTO
 * <p>
 * 用于"我学过的"统计接口,返回用户已学课时的汇总数据,包含已学总数、
 * 累计星星数、平均分数,以及按学科和课时类型的分类统计。
 * </p>
 *
 * @param totalLearned  已学课时总数
 * @param totalStars    累计星星总数
 * @param averageScore  平均分数(取整)
 * @param bySubject     按学科分类的已学课时数量列表
 * @param byType        按课时类型分类的已学课时数量列表
 * @author englishapp
 * @since 1.2.0
 */
public record LearnedStatsDto(
        Long totalLearned,
        Integer totalStars,
        Integer averageScore,
        List<SubjectCount> bySubject,
        List<TypeCount> byType
) {
    /**
     * 单个学科的已学课时数量
     *
     * @param subjectId   学科 ID
     * @param subjectName 学科名称
     * @param count       该学科的已学课时数量
     */
    public record SubjectCount(Integer subjectId, String subjectName, Long count) {}

    /**
     * 单个课时类型的已学课时数量
     *
     * @param type  课时类型
     * @param count 该类型的已学课时数量
     */
    public record TypeCount(String type, Long count) {}
}
