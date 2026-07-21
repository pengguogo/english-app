package com.englishapp.repository;

import com.englishapp.domain.WrongAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 错题仓库
 * <p>
 * 提供对 {@link WrongAnswer} 实体的持久化访问,支持按用户、课时、掌握状态等维度查询错题。
 * </p>
 *
 * @author englishapp
 * @since 1.2.0
 */
@Repository
public interface WrongAnswerRepository extends JpaRepository<WrongAnswer, Integer> {

    /**
     * 查询某用户的全部错题,按最近答错时间降序排列(最新答错的排在最前)
     *
     * @param userId 用户 ID
     * @return 错题列表(按 lastWrongAt 降序)
     */
    List<WrongAnswer> findByUserIdOrderByLastWrongAtDesc(Integer userId);

    /**
     * 按用户 ID 与掌握状态查询错题,按最近答错时间降序排列
     *
     * @param userId     用户 ID
     * @param isResolved 是否已掌握(true=已掌握,false=未掌握)
     * @return 错题列表(按 lastWrongAt 降序)
     */
    List<WrongAnswer> findByUserIdAndIsResolvedOrderByLastWrongAtDesc(Integer userId, Boolean isResolved);

    /**
     * 查找特定错题记录(用于重复答错时累加 wrongCount)
     * <p>
     * 通过 user_id + lesson_id + question_index 唯一组合定位,
     * 对应数据库表上的 UNIQUE 约束。
     * </p>
     *
     * @param userId        用户 ID
     * @param lessonId      课时 ID
     * @param questionIndex 题目序号
     * @return 错题记录(可能为空)
     */
    Optional<WrongAnswer> findByUserIdAndLessonIdAndQuestionIndex(Integer userId, Integer lessonId, Integer questionIndex);

    /**
     * 统计某用户指定掌握状态的错题数量
     *
     * @param userId     用户 ID
     * @param isResolved 是否已掌握
     * @return 符合条件的错题数量
     */
    Long countByUserIdAndIsResolved(Integer userId, Boolean isResolved);
}
