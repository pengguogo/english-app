package com.englishapp.repository;

import com.englishapp.domain.LessonStudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface LessonStudySessionRepository extends JpaRepository<LessonStudySession, Integer> {
    Optional<LessonStudySession> findByUserIdAndLessonIdAndStudyDate(Integer userId, Integer lessonId, LocalDate studyDate);
}
