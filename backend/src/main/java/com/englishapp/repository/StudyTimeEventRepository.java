package com.englishapp.repository;

import com.englishapp.domain.StudyTimeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyTimeEventRepository extends JpaRepository<StudyTimeEvent, String> {
    java.util.List<StudyTimeEvent> findByUserIdAndStudyDateBetween(Integer userId, java.time.LocalDate start, java.time.LocalDate end);
}
