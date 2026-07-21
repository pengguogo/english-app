package com.englishapp.service;

import com.englishapp.config.WordImageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * WordImageResolver 多子目录解析单元测试
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("图片URL多子目录解析测试")
class WordImageResolverMultiDirTest {

    @Mock
    private WordImageProperties properties;

    @InjectMocks
    private WordImageResolver resolver;

    @BeforeEach
    void setUp() {
        // baseUrl 和 extension 在所有测试中均被调用，使用普通 stub
        when(properties.getBaseUrl()).thenReturn("http://localhost:8080");
        // pathPrefix 仅在不带斜杠的 key 测试中被调用，使用 lenient 避免严格模式报错
        lenient().when(properties.getPathPrefix()).thenReturn("/images/words");
        when(properties.getExtension()).thenReturn(".jpg");
    }

    @Test
    @DisplayName("should_resolveWordsUrl_when_key不带斜杠")
    void should_resolveWordsUrl_when_key不带斜杠() {
        String url = resolver.resolveUrl("car");
        assertThat(url).isEqualTo("http://localhost:8080/images/words/car.jpg");
    }

    @Test
    @DisplayName("should_resolveTrainUrl_when_key带train前缀")
    void should_resolveTrainUrl_when_key带train前缀() {
        String url = resolver.resolveUrl("train/steam-locomotive");
        assertThat(url).isEqualTo("http://localhost:8080/images/train/steam-locomotive.jpg");
    }

    @Test
    @DisplayName("should_resolveChineseUrl_when_key带chinese前缀")
    void should_resolveChineseUrl_when_key带chinese前缀() {
        String url = resolver.resolveUrl("chinese/pinyin-a");
        assertThat(url).isEqualTo("http://localhost:8080/images/chinese/pinyin-a.jpg");
    }

    @Test
    @DisplayName("should_resolveMathUrl_when_key带math前缀")
    void should_resolveMathUrl_when_key带math前缀() {
        String url = resolver.resolveUrl("math/triangle");
        assertThat(url).isEqualTo("http://localhost:8080/images/math/triangle.jpg");
    }
}
