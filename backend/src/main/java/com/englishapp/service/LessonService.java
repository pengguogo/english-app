package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.dto.LessonDto;
import com.englishapp.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final WordImageResolver wordImageResolver;

    /**
     * 构造课时服务
     * @param lessonRepository 课时仓库
     * @param wordImageResolver 单词配图 URL 解析器
     */
    public LessonService(LessonRepository lessonRepository, WordImageResolver wordImageResolver) {
        this.lessonRepository = lessonRepository;
        this.wordImageResolver = wordImageResolver;
    }

    /**
     * 获取某单元下的所有课时(列表接口)。
     * 不解析 content 中的 image 字段,列表场景无需图片 URL,避免无谓开销。
     * @param unitId 单元 ID
     * @return 课时 DTO 列表
     */
    public List<LessonDto> getLessonsByUnitId(Integer unitId) {
        return lessonRepository.findByUnitIdOrderBySortOrderAsc(unitId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 根据 ID 获取课时详情(含学习内容 JSON)。
     * 详情接口会解析 content,把 image 的纯 key 改写为完整 URL,前端可直接使用。
     * @param id 课时 ID
     * @return 课时 DTO(image 字段已是完整 URL)
     */
    public LessonDto getLessonById(Integer id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + id));
        LessonDto dto = toDto(lesson);
        // 详情接口:解析 content 并把 image key 转为完整 URL
        String resolvedContent = wordImageResolver.resolveContent(dto.content());
        return new LessonDto(
                dto.id(), dto.unitId(), dto.name(), dto.type(),
                resolvedContent, dto.sortOrder(), dto.starReward()
        );
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
