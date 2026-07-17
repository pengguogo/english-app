package com.englishapp.repository;

import com.englishapp.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByUnitIdOrderBySortOrderAsc(Integer unitId);
}
