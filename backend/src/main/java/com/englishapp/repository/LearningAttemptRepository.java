package com.englishapp.repository;

import com.englishapp.domain.LearningAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningAttemptRepository extends JpaRepository<LearningAttempt, String> {
    java.util.List<LearningAttempt> findByUserIdAndStudyDateBetween(Integer userId, java.time.LocalDate start, java.time.LocalDate end);
}
