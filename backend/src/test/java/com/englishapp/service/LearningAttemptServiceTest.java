package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.domain.LearningAttempt;
import com.englishapp.domain.enums.LessonType;
import com.englishapp.dto.LearningAttemptRequestDTO;
import com.englishapp.repository.LearningAttemptRepository;
import com.englishapp.repository.LessonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LearningAttemptServiceTest {
    @Test
    void should_保留首次错误且去重辅助答对_当_重试同一练习事件() {
        var repository = mock(LearningAttemptRepository.class);
        var lessons = mock(LessonRepository.class);
        var lesson = new Lesson(); lesson.setType(LessonType.QUIZ); lesson.setContent("{\"items\":[{}]}");
        when(lessons.findById(7)).thenReturn(Optional.of(lesson));
        var service = new LearningAttemptService(repository, lessons, new ObjectMapper());
        var event = new LearningAttempt(); event.setId("event"); event.setUserId(1); event.setLessonId(7);
        event.setQuestionIndex(0); event.setStudyDate(LocalDate.now()); event.setFirstCorrect(false); event.setAssistedCorrect(false);
        when(repository.findById("event")).thenReturn(Optional.of(event));
        service.record(new LearningAttemptRequestDTO("event", 7, 0, false, true));
        service.record(new LearningAttemptRequestDTO("event", 7, 0, false, false));
        assertFalse(event.getFirstCorrect());
        assertTrue(event.getAssistedCorrect());
        assertThrows(IllegalArgumentException.class, () -> service.record(new LearningAttemptRequestDTO("event", 7, 0, true, false)));
        assertThrows(IllegalArgumentException.class, () -> service.record(new LearningAttemptRequestDTO("new", 7, 1, true, false)));
    }

    @Test
    void should_创建首次答对样本_当_首次记录合法题目() {
        var repository = mock(LearningAttemptRepository.class);
        var lessons = mock(LessonRepository.class);
        var lesson = new Lesson(); lesson.setType(LessonType.CALCULATE); lesson.setContent("{\"items\":[{}]}");
        when(lessons.findById(7)).thenReturn(Optional.of(lesson));
        new LearningAttemptService(repository, lessons, new ObjectMapper()).record(new LearningAttemptRequestDTO("event", 7, 0, true, false));
        verify(repository).save(argThat(e -> e.getFirstCorrect() && !e.getAssistedCorrect()));
    }
}
