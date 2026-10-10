package com.englishapp.service;

import com.englishapp.domain.LearningAttempt;
import com.englishapp.dto.LearningAttemptRequestDTO;
import com.englishapp.repository.LearningAttemptRepository;
import com.englishapp.repository.LessonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class LearningAttemptService {
    private final LearningAttemptRepository attempts;
    private final LessonRepository lessons;
    private final ObjectMapper mapper;
    public LearningAttemptService(LearningAttemptRepository attempts, LessonRepository lessons, ObjectMapper mapper) {
        this.attempts = attempts;
        this.lessons = lessons;
        this.mapper = mapper;
    }

    @Transactional
    public void record(LearningAttemptRequestDTO request) {
        var lesson = lessons.findById(request.lessonId()).orElseThrow(() -> new IllegalArgumentException("课时不存在"));
        if (!java.util.Set.of("QUIZ", "CALCULATE").contains(lesson.getType().name())) throw new IllegalArgumentException("课型不支持答题统计");
        try {
            if (request.questionIndex() >= mapper.readTree(lesson.getContent()).path("items").size()) throw new IllegalArgumentException("题号无效");
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) { throw new IllegalArgumentException("课时内容无效", e); }
        if (request.firstCorrect() && request.assistedCorrect()) throw new IllegalArgumentException("首次答对与辅助答对不能同时成立");
        LearningAttempt attempt = attempts.findById(request.eventId()).orElse(null);
        if (attempt == null) {
            attempt = new LearningAttempt();
            attempt.setId(request.eventId());
            attempt.setUserId(1);
            attempt.setLessonId(request.lessonId());
            attempt.setQuestionIndex(request.questionIndex());
            attempt.setStudyDate(LocalDate.now());
            attempt.setFirstCorrect(request.firstCorrect());
            attempt.setAssistedCorrect(false);
        } else if (attempt.getUserId() != 1 || !attempt.getLessonId().equals(request.lessonId())
                || !attempt.getQuestionIndex().equals(request.questionIndex()) || !attempt.getFirstCorrect().equals(request.firstCorrect())) {
            throw new IllegalArgumentException("答题事件与原请求不一致");
        }
        attempt.setAssistedCorrect(attempt.getAssistedCorrect() || request.assistedCorrect());
        attempts.save(attempt);
    }
}
