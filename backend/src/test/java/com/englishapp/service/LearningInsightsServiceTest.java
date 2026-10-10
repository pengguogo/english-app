package com.englishapp.service;

import com.englishapp.domain.*;
import com.englishapp.repository.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LearningInsightsServiceTest {
    private final StudyTimeEventRepository times = mock(StudyTimeEventRepository.class);
    private final LearningAttemptRepository attempts = mock(LearningAttemptRepository.class);
    private final LessonStudySessionRepository sessions = mock(LessonStudySessionRepository.class);
    private final WrongAnswerRepository wrong = mock(WrongAnswerRepository.class);
    private final LessonRepository lessons = mock(LessonRepository.class);
    private final LearningInsightsService service = new LearningInsightsService(times, attempts, sessions, wrong, lessons);

    @Test
    void should_优先复习错题否则学习下一课_当_查询今日任务() {
        var question = new WrongAnswer(); question.setLessonName("加法");
        // 主键没有setter，用测试工具设置。
        org.springframework.test.util.ReflectionTestUtils.setField(question, "id", 9);
        when(wrong.findFirstByUserIdAndIsResolvedFalseAndQuestionTypeInOrderByLastWrongAtDesc(eq(1), anyList())).thenReturn(Optional.of(question));
        assertEquals("/wrong-answers/9/practice", service.today().path());
        when(wrong.findFirstByUserIdAndIsResolvedFalseAndQuestionTypeInOrderByLastWrongAtDesc(eq(1), anyList())).thenReturn(Optional.empty());
        var lesson = new Lesson(); lesson.setId(7); lesson.setName("水果");
        when(lessons.findNextUnfinishedEnglishLesson(eq(1), any())).thenReturn(List.of(lesson));
        assertEquals("/lesson/7", service.today().path());
        when(lessons.findNextUnfinishedEnglishLesson(eq(1), any())).thenReturn(List.of());
        assertEquals("/learned", service.today().path());
    }

    @Test
    void should_统计完整七天且不限制五分钟_当_实际时长和答题样本存在() {
        var event = new StudyTimeEvent(); event.setStudyDate(LocalDate.now()); event.setSeconds(30);
        when(times.findByUserIdAndStudyDateBetween(eq(1), any(), any())).thenReturn(java.util.Collections.nCopies(20, event));
        var answer = new LearningAttempt(); answer.setFirstCorrect(false); answer.setAssistedCorrect(true);
        when(attempts.findByUserIdAndStudyDateBetween(eq(1), any(), any())).thenReturn(List.of(answer));
        var session = new LessonStudySession(); session.setStudyDate(LocalDate.now()); session.setPassed(true);
        when(sessions.findByUserIdAndStudyDateBetween(eq(1), any(), any())).thenReturn(List.of(session));
        var report = service.week();
        assertEquals(7, report.days().size()); assertEquals(600, report.totalSeconds());
        assertEquals(1, report.activeDays()); assertEquals(1, report.passedLessons());
        assertEquals(1, report.attempts()); assertEquals(0, report.firstCorrect()); assertEquals(1, report.assistedCorrect());
    }

    @Test
    void should_返回空样本_当_尚无新统计数据() {
        var report = service.week();
        assertEquals(0, report.totalSeconds()); assertEquals(0, report.attempts());
        assertEquals(7, report.days().size());
    }
}
