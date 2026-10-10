package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.repository.LessonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CharacterCatalogTest {
    @Test
    void should_解析认字字段并忽略普通WORD_当_获取汉字目录() {
        var lessons = mock(LessonRepository.class);
        var images = mock(WordImageResolver.class);
        var lesson = new Lesson();
        lesson.setContent("{\"items\":[{\"word\":\"山\",\"recognition\":true,\"phonetic\":\"shān\",\"image\":\"hanzi-pilot/hanzi-shan\",\"exampleWord\":\"小山\",\"exampleSentence\":\"小山很高。\"},{\"word\":\"水\"}]}");
        when(lessons.findById(68)).thenReturn(Optional.of(lesson));
        when(images.resolveUrl("hanzi-pilot/hanzi-shan")).thenReturn("/images/hanzi-pilot/hanzi-shan.jpg");
        var items = new CharacterCatalog(lessons, new ObjectMapper(), images).items();
        assertEquals(1, items.size());
        assertEquals("山", items.get(0).word());
        assertEquals("小山", items.get(0).exampleWord());
        assertTrue(items.get(0).image().endsWith(".jpg"));
    }
}
