package com.englishapp.service;

import com.englishapp.dto.RecordWrongAnswerRequest;
import com.englishapp.dto.WrongAnswerDto;
import com.englishapp.dto.WrongAnswerStatsDto;

import java.util.List;

/**
 * 错题业务接口
 * <p>
 * 定义错题集核心业务能力,包括记录错题、查询错题列表、统计错题、
 * 标记已掌握与删除错题。
 * </p>
 *
 * @author englishapp
 * @since 1.2.0
 */
public interface WrongAnswerService {

    /**
     * 记录错题
     * <p>
     * 若同一用户、同一课时、同一题号的错题已存在,则累加错误次数(wrongCount + 1)
     * 并更新最近答错时间、用户答案、正确答案、题目快照,同时将掌握状态重置为未掌握;
     * 否则创建新的错题记录。
     * </p>
     *
     * @param request 错题记录请求(含课时、题号、类型、答案等)
     * @param userId  用户 ID(为 null 时使用默认用户)
     */
    void recordWrongAnswer(RecordWrongAnswerRequest request, Integer userId);

    /**
     * 查询错题列表
     *
     * @param userId   用户 ID(为 null 时使用默认用户)
     * @param resolved 掌握状态筛选:null 返回全部,true 返回已掌握,false 返回未掌握
     * @return 错题 DTO 列表(按最近答错时间降序)
     */
    List<WrongAnswerDto> getWrongAnswers(Integer userId, Boolean resolved);

    /**
     * 获取错题统计
     * <p>
     * 返回错题总数、未掌握数、已掌握数,以及按题目类型的分类统计。
     * </p>
     *
     * @param userId 用户 ID(为 null 时使用默认用户)
     * @return 错题统计 DTO
     */
    WrongAnswerStatsDto getWrongAnswerStats(Integer userId);

    /**
     * 将指定错题标记为已掌握
     *
     * @param id     错题记录 ID
     * @param userId 用户 ID(用于权限校验,确保只能操作自己的错题)
     * @throws IllegalArgumentException 错题不存在或不属于该用户时抛出
     */
    void markAsResolved(Integer id, Integer userId);

    /**
     * 删除指定错题
     *
     * @param id     错题记录 ID
     * @param userId 用户 ID(用于权限校验,确保只能操作自己的错题)
     * @throws IllegalArgumentException 错题不存在或不属于该用户时抛出
     */
    void deleteWrongAnswer(Integer id, Integer userId);
}
