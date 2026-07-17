package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.dto.LessonDto;
import com.englishapp.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<LessonDto> getLessonsByUnitId(Integer unitId) {
        return lessonRepository.findByUnitIdOrderBySortOrderAsc(unitId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public LessonDto getLessonById(Integer id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + id));
        return toDto(lesson);
    }

    private LessonDto toDto(Lesson lesson) {
        return new LessonDto(
                lesson.getId(),
                lesson.getUnitId(),
                lesson.getName(),
                lesson.getType().name(),
                lesson.getContent(),
                lesson.getSortOrder(),
                lesson.getStarReward()
        );
    }
}
