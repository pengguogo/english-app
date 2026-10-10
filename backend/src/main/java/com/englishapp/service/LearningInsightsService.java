package com.englishapp.service;

import com.englishapp.dto.TodayTaskResponseDTO;
import com.englishapp.dto.WeekReportResponseDTO;
import com.englishapp.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class LearningInsightsService {
    private final StudyTimeEventRepository times;
    private final LearningAttemptRepository attempts;
    private final LessonStudySessionRepository sessions;
    private final WrongAnswerRepository wrongAnswers;
    private final LessonRepository lessons;
    public LearningInsightsService(StudyTimeEventRepository times, LearningAttemptRepository attempts,
            LessonStudySessionRepository sessions, WrongAnswerRepository wrongAnswers, LessonRepository lessons) {
        this.times = times;
        this.attempts = attempts;
        this.sessions = sessions;
        this.wrongAnswers = wrongAnswers;
        this.lessons = lessons;
    }

    public TodayTaskResponseDTO today() {
        var wrong = wrongAnswers.findFirstByUserIdAndIsResolvedFalseAndQuestionTypeInOrderByLastWrongAtDesc(1, List.of("QUIZ", "CALCULATE"));
        if (wrong.isPresent()) return new TodayTaskResponseDTO("重练：" + wrong.get().getLessonName(),
                "先把一道还没掌握的题练会", "/wrong-answers/" + wrong.get().getId() + "/practice");
        var next = lessons.findNextUnfinishedEnglishLesson(1, org.springframework.data.domain.PageRequest.of(0, 1));
        if (!next.isEmpty()) return new TodayTaskResponseDTO(next.get(0).getName(), "开始一节还没完成的英语课", "/lesson/" + next.get(0).getId());
        return new TodayTaskResponseDTO("复习学过的课程", "英语课程已完成，选一课再练练", "/learned");
    }

    public WeekReportResponseDTO week() {
        LocalDate to = LocalDate.now(), from = to.minusDays(6);
        var events = times.findByUserIdAndStudyDateBetween(1, from, to);
        var answers = attempts.findByUserIdAndStudyDateBetween(1, from, to);
        var passed = sessions.findByUserIdAndStudyDateBetween(1, from, to).stream()
                .filter(s -> Boolean.TRUE.equals(s.getPassed())).toList();
        var days = IntStream.range(0, 7).mapToObj(i -> {
            LocalDate date = from.plusDays(i);
            int seconds = events.stream().filter(e -> date.equals(e.getStudyDate())).mapToInt(e -> e.getSeconds()).sum();
            int count = (int) passed.stream().filter(s -> date.equals(s.getStudyDate())).count();
            return new WeekReportResponseDTO.Day(date, seconds, count);
        }).toList();
        return new WeekReportResponseDTO(from, to, days.stream().mapToInt(WeekReportResponseDTO.Day::seconds).sum(),
                (int) days.stream().filter(d -> d.seconds() > 0).count(), passed.size(), answers.size(),
                (int) answers.stream().filter(a -> a.getFirstCorrect()).count(),
                (int) answers.stream().filter(a -> a.getAssistedCorrect()).count(), days);
    }
}
