package com.englishapp.repository;

import com.englishapp.domain.GrowthMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GrowthMeasurementRepository extends JpaRepository<GrowthMeasurement, Integer> {
    List<GrowthMeasurement> findByUserIdOrderByMeasuredAtAscIdAsc(Integer userId);
    Optional<GrowthMeasurement> findByIdAndUserId(Integer id, Integer userId);
}
