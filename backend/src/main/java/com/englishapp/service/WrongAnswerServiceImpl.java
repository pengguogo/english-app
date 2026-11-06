package com.englishapp.service;

import com.englishapp.domain.WrongAnswer;
import com.englishapp.dto.RecordWrongAnswerRequest;
import com.englishapp.dto.WrongAnswerDto;
import com.englishapp.dto.WrongAnswerStatsDto;
import com.englishapp.dto.WrongAnswerStatsDto.TypeCount;
import com.englishapp.repository.WrongAnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 错题业务实现
 * <p>
 * 基于 {@link WrongAnswerRepository} 实现错题的记录、查询、统计、标记掌握与删除。
 * 核心设计要点:
 * <ul>
 *     <li>幂等累加:同一用户+课时+题号的错题只保留一条,重复答错时 wrongCount 累加</li>
 *     <li>权限隔离:标记掌握与删除按 id + userId 查询,他人错题视为不存在(404)</li>
 *     <li>统计分组:按题目类型分组统计,使用 Java Stream groupBy 实现</li>
 * </ul>
 * </p>
 *
 * @author englishapp
 * @since 1.2.0
 */
@Service
public class WrongAnswerServiceImpl implements WrongAnswerService {

    private final WrongAnswerRepository wrongAnswerRepository;

    /**
     * 构造错题业务实现
     *
     * @param wrongAnswerRepository 错题仓库
     */
    public WrongAnswerServiceImpl(WrongAnswerRepository wrongAnswerRepository) {
        this.wrongAnswerRepository = wrongAnswerRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void recordWrongAnswer(RecordWrongAnswerRequest request, Integer userId) {
        LocalDateTime now = LocalDateTime.now();

        // 先查找是否已存在同一题的错题记录(用于重复答错累加)
        Optional<WrongAnswer> existing = wrongAnswerRepository
                .findByUserIdAndLessonIdAndQuestionIndex(
                        userId, request.getLessonId(), request.getQuestionIndex());

        if (existing.isPresent()) {
            // 已存在:累加错误次数,更新最新答题信息,重置为未掌握
            WrongAnswer wa = existing.get();
            wa.setWrongCount(wa.getWrongCount() + 1);
            wa.setLastWrongAt(now);
            wa.setUserAnswer(request.getUserAnswer());
            wa.setCorrectAnswer(request.getCorrectAnswer());
            wa.setQuestionSnapshot(request.getQuestionSnapshot());
            // 重新答错说明尚未掌握,重置为未掌握状态
            wa.setIsResolved(false);
            wa.setUpdatedAt(now);
            wrongAnswerRepository.save(wa);
        } else {
            // 不存在:创建新的错题记录
            WrongAnswer wa = new WrongAnswer();
            wa.setUserId(userId);
            wa.setLessonId(request.getLessonId());
            wa.setLessonName(request.getLessonName());
            wa.setQuestionIndex(request.getQuestionIndex());
            wa.setQuestionType(request.getQuestionType());
            wa.setQuestionSnapshot(request.getQuestionSnapshot());
            wa.setUserAnswer(request.getUserAnswer());
            wa.setCorrectAnswer(request.getCorrectAnswer());
            wa.setWrongCount(1);
            wa.setIsResolved(false);
            wa.setLastWrongAt(now);
            wa.setCreatedAt(now);
            wa.setUpdatedAt(now);
            wrongAnswerRepository.save(wa);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WrongAnswerDto> getWrongAnswers(Integer userId, Boolean resolved) {
        List<WrongAnswer> list;
        if (resolved == null) {
            // null:返回全部错题
            list = wrongAnswerRepository.findByUserIdOrderByLastWrongAtDesc(userId);
        } else {
            // true:已掌握;false:未掌握
            list = wrongAnswerRepository
                    .findByUserIdAndIsResolvedOrderByLastWrongAtDesc(userId, resolved);
        }
        return list.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WrongAnswerStatsDto getWrongAnswerStats(Integer userId) {

        Long unresolved = wrongAnswerRepository.countByUserIdAndIsResolved(userId, false);
        Long resolved = wrongAnswerRepository.countByUserIdAndIsResolved(userId, true);
        Long total = unresolved + resolved;

        // 按题目类型分组统计:从全部错题中手动 groupBy
        List<WrongAnswer> all = wrongAnswerRepository.findByUserIdOrderByLastWrongAtDesc(userId);
        Map<String, Long> typeMap = all.stream()
                .collect(Collectors.groupingBy(WrongAnswer::getQuestionType, Collectors.counting()));
        List<TypeCount> byType = typeMap.entrySet().stream()
                .map(e -> new TypeCount(e.getKey(), e.getValue()))
                .toList();

        return new WrongAnswerStatsDto(total, unresolved, resolved, byType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void markAsResolved(Integer id, Integer userId) {
        // 按 id + user_id 查询:不属于当前孩子与不存在同样返回 404
        WrongAnswer wa = wrongAnswerRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("错题不存在"));
        wa.setIsResolved(true);
        wa.setUpdatedAt(LocalDateTime.now());
        wrongAnswerRepository.save(wa);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteWrongAnswer(Integer id, Integer userId) {
        // 按 id + user_id 查询:不属于当前孩子与不存在同样返回 404
        WrongAnswer wa = wrongAnswerRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("错题不存在"));
        wrongAnswerRepository.delete(wa);
    }

    /**
     * 将错题实体转换为 DTO
     *
     * @param wa 错题实体
     * @return 错题 DTO
     */
    private WrongAnswerDto toDto(WrongAnswer wa) {
        return new WrongAnswerDto(
                wa.getId(),
                wa.getLessonId(),
                wa.getLessonName(),
                wa.getQuestionIndex(),
                wa.getQuestionType(),
                wa.getQuestionSnapshot(),
                wa.getUserAnswer(),
                wa.getCorrectAnswer(),
                wa.getWrongCount(),
                wa.getIsResolved(),
                wa.getLastWrongAt(),
                wa.getCreatedAt()
        );
    }
}
