package com.englishapp.service;

import com.englishapp.dto.CharacterItemResponseDTO;
import com.englishapp.repository.LessonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CharacterCatalog {
    private final LessonRepository lessons;
    private final ObjectMapper mapper;
    private final WordImageResolver images;

    public CharacterCatalog(LessonRepository lessons, ObjectMapper mapper, WordImageResolver images) {
        this.lessons = lessons;
        this.mapper = mapper;
        this.images = images;
    }

    public List<CharacterItemResponseDTO> items() {
        List<CharacterItemResponseDTO> result = new ArrayList<>();
        // 只接收汉字识读主题里显式标记的学习项，新增课程无需硬编码 ID。
        for (var lesson : lessons.findByThemeAndType(10, com.englishapp.domain.enums.LessonType.WORD)) {
            final int lessonId = lesson.getId();
            try {
                var items = mapper.readTree(lesson.getContent()).path("items");
                for (int i = 0; i < items.size(); i++) {
                    var item = items.get(i);
                    if (!item.path("recognition").asBoolean()) continue;
                    result.add(new CharacterItemResponseDTO(lessonId, i, item.path("word").asText(),
                            item.path("phonetic").asText(), images.resolveUrl(item.path("image").asText()),
                            item.path("exampleWord").asText(), item.path("exampleSentence").asText(),
                            item.path("imageChoice").asBoolean(true), item.path("readingNote").asText("")));
                }
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new IllegalArgumentException("汉字课程内容无效", e);
            }
        }
        return result;
    }
}
