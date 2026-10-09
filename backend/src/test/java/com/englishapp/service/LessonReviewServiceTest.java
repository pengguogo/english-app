package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.domain.LessonStudySession;
import com.englishapp.domain.enums.LessonType;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.LessonStudySessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonReviewServiceTest {
    @Mock LessonRepository lessons;
    @Mock LessonStudySessionRepository sessions;
    @Mock GameAccessService gameAccess;
    LessonReviewService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new LessonReviewService(lessons, sessions, gameAccess, new ObjectMapper());
        Lesson lesson = new Lesson();
        lesson.setId(7);
        lesson.setType(LessonType.QUIZ);
        lesson.setContent("{\"items\":[{\"question\":\"2+2=?\",\"options\":[\"3\",\"4\"],\"answer\":1}]}");
        when(lessons.findById(7)).thenReturn(Optional.of(lesson));
    }

    @Test
    void should_保持锁定且不计时_当_复习答案错误() {
        var result = service.submit(1, 7, List.of(0));
        assertFalse(result.passed());
        assertEquals(List.of(0), result.wrongIndexes());
        verifyNoInteractions(gameAccess);
        verify(sessions, never()).save(any());
    }

    @Test
    void should_只结算待计时长_当_全部答对() {
        LessonStudySession session = new LessonStudySession();
        session.setPendingSeconds(90);
        session.setPassed(false);
        when(sessions.findByUserIdAndLessonIdAndStudyDate(1, 7, LocalDate.now())).thenReturn(Optional.of(session));

        var result = service.submit(1, 7, List.of(1));

        assertTrue(result.passed());
        assertEquals(90, result.creditedSeconds());
        assertEquals(0, session.getPendingSeconds());
        assertTrue(session.getPassed());
        verify(gameAccess).recordStudyTime(1, 90);
    }

    @Test
    void should_生成选择题_当_旧课时使用单词数组() {
        Lesson lesson = new Lesson();
        lesson.setId(8);
        lesson.setType(LessonType.WORD);
        lesson.setContent("{\"words\":[\"cat\",\"dog\"]}");
        when(lessons.findById(8)).thenReturn(Optional.of(lesson));

        var questions = service.questions(8);

        assertEquals(2, questions.size());
        assertTrue(questions.get(0).options().stream().anyMatch(option -> option.text().equals("cat")));
    }
}
