package com.englishapp.repository;

import com.englishapp.domain.DailyStudyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyStudyTimeRepository extends JpaRepository<DailyStudyTime, Integer> {
    Optional<DailyStudyTime> findByUserIdAndStudyDate(Integer userId, LocalDate studyDate);
}
