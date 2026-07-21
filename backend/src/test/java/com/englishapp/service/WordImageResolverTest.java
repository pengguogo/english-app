package com.englishapp.service;

import com.englishapp.config.WordImageProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WordImageResolver 单元测试
 * <p>
 * 验证 content JSON 中 image 字段从纯 key 到完整 URL 的改写逻辑,
 * 覆盖正常、缺失、空值、多 item、非法 JSON 等场景。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
class WordImageResolverTest {

    private WordImageResolver resolver;

    @BeforeEach
    void setUp() {
        WordImageProperties properties = new WordImageProperties();
        properties.setBaseUrl("http://localhost:8080");
        properties.setPathPrefix("/images/words");
        properties.setExtension(".jpg");
        resolver = new WordImageResolver(properties, new ObjectMapper());
    }

    /**
     * 有 image 字段的 item 应被正确拼接为完整 URL。
     */
    @Test
    @DisplayName("有 image 字段时:改写为完整 URL")
    void should_returnFullUrl_when_imageKeyExists() throws Exception {
        // Arrange:模拟单词课 content,items 数组中 item 带 image key
        String content = "{\"type\":\"WORD\",\"items\":[" +
                "{\"word\":\"chase\",\"image\":\"chase\"}]}";

        // Act
        String result = resolver.resolveContent(content);

        // Assert:image 字段应为完整 URL
        assertTrue(result.contains("\"image\":\"http://localhost:8080/images/words/chase.jpg\""));
        assertTrue(result.contains("\"word\":\"chase\""));
    }

    /**
     * 无 image 字段的 item 应原样保留(不新增 image 字段)。
     */
    @Test
    @DisplayName("无 image 字段时:item 原样保留")
    void should_keepItemUnchanged_when_imageFieldMissing() {
        // Arrange:模拟句型课 content,item 无 image 字段
        String content = "{\"type\":\"SENTENCE\",\"items\":[" +
                "{\"sentence\":\"I have a car\",\"emoji\":\"🚗\"}]}";

        // Act
        String result = resolver.resolveContent(content);

        // Assert:不含 image 字段,其他字段不变
        assertFalse(result.contains("image"));
        assertTrue(result.contains("\"sentence\":\"I have a car\""));
    }

    /**
     * image 为空字符串的 item 应原样保留(不拼接 URL)。
     */
    @Test
    @DisplayName("image 为空字符串时:item 原样保留")
    void should_keepItemUnchanged_when_imageFieldEmpty() {
        // Arrange:item 的 image 为空字符串
        String content = "{\"type\":\"WORD\",\"items\":[" +
                "{\"word\":\"car\",\"image\":\"\"}]}";

        // Act
        String result = resolver.resolveContent(content);

        // Assert:image 仍为空字符串,未拼接 URL
        assertTrue(result.contains("\"image\":\"\""));
        assertFalse(result.contains("localhost"));
    }

    /**
     * 多个 item 应批量处理:有 image 的改写,无 image 的保留。
     */
    @Test
    @DisplayName("多个 item 时:批量处理")
    void should_handleMultipleItems_inOneContent() {
        // Arrange:两个 item,一个有 image 一个没有
        String content = "{\"type\":\"WORD\",\"items\":[" +
                "{\"word\":\"chase\",\"image\":\"chase\"}," +
                "{\"word\":\"team\",\"image\":\"team\"}]}";

        // Act
        String result = resolver.resolveContent(content);

        // Assert:两个 image 都被改写
        assertTrue(result.contains("http://localhost:8080/images/words/chase.jpg"));
        assertTrue(result.contains("http://localhost:8080/images/words/team.jpg"));
    }

    /**
     * JSON 解析失败时应返回原 content(降级,保证业务可用)。
     */
    @Test
    @DisplayName("非法 JSON 时:返回原 content(降级)")
    void should_returnOriginalContent_when_contentIsInvalidJson() {
        // Arrange:非法 JSON 字符串
        String invalidJson = "{not a valid json";

        // Act
        String result = resolver.resolveContent(invalidJson);

        // Assert:返回原字符串,不抛异常
        assertEquals(invalidJson, result);
    }

    /**
     * content 为 null 时应原样返回。
     */
    @Test
    @DisplayName("content 为 null 时:返回 null")
    void should_returnNull_when_contentIsNull() {
        assertNull(resolver.resolveContent(null));
    }

    /**
     * content 为空字符串时应原样返回。
     */
    @Test
    @DisplayName("content 为空字符串时:返回空字符串")
    void should_returnEmptyString_when_contentIsBlank() {
        assertEquals("", resolver.resolveContent(""));
    }

    /**
     * content 无 items 数组(如其他结构)时应原样返回。
     */
    @Test
    @DisplayName("无 items 数组时:原样返回")
    void should_returnOriginalContent_when_noItemsArray() {
        // Arrange:content 只有 words 数组(非标准 items 结构)
        String content = "{\"type\":\"WORD\",\"words\":[\"chase\",\"team\"]}";

        // Act
        String result = resolver.resolveContent(content);

        // Assert:原样返回
        assertEquals(content, result);
    }

    /**
     * resolveUrl 应正确拼接单个图片 URL。
     */
    @Test
    @DisplayName("resolveUrl:拼接单个图片 URL")
    void should_returnFullUrl_when_resolveUrlCalled() {
        // Act
        String url = resolver.resolveUrl("chase");

        // Assert
        assertEquals("http://localhost:8080/images/words/chase.jpg", url);
    }

    /**
     * PHONICS 类型 content 的 items 含 image 字段时,应被正确解析为完整 URL,
     * 同时空 image 字段的 item 不影响整体 JSON 结构。
     * <p>
     * 场景:自然拼读课 items 包含 2 个元素,第一个 image="fish" 应改写,
     * 第二个 image 为空字符串应原样保留。
     * </p>
     */
    @Test
    @DisplayName("PHONICS 类型:有 image 的 item 被改写,空 image 原样保留")
    void should_resolvePhonicsImageUrls_when_contentHasItemsWithImage() {
        // Arrange:模拟自然拼读课 content,两个 item 一个有 image 一个为空字符串
        String content = "{\"type\":\"PHONICS\",\"items\":[" +
                "{\"phonics\":\"sh\",\"image\":\"fish\"}," +
                "{\"phonics\":\"ch\",\"image\":\"\"}]}";

        // Act
        String result = resolver.resolveContent(content);

        // Assert:有 image 的被解析为完整 URL,空 image 不影响 JSON 结构
        assertNotNull(result);
        assertTrue(result.contains("/images/words/fish.jpg"),
                "PHONICS item 的 image key 应被解析为完整 URL");
        assertTrue(result.contains("\"image\":\"\""),
                "空 image 字段应原样保留为空字符串");
    }

    /**
     * DIALOGUE 类型 content 的 items 无 image 字段时,解析器不应报错,
     * 原始文本字段应原样返回。
     * <p>
     * 场景:对话课 items 仅含 speaker/text 字段,不含 image,解析后应保持不变。
     * </p>
     */
    @Test
    @DisplayName("DIALOGUE 类型:无 image 字段时不报错,原样返回")
    void should_notFail_when_dialogueContentHasNoImageField() {
        // Arrange:模拟对话课 content,item 只有 speaker/text 字段,无 image
        String content = "{\"type\":\"DIALOGUE\",\"items\":[" +
                "{\"speaker\":\"Tom\",\"text\":\"Hello, how are you?\"}]}";

        // Act
        String result = resolver.resolveContent(content);

        // Assert:不报错且原样返回,文本字段未被破坏
        assertNotNull(result, "DIALOGUE content 无 image 字段时不应报错");
        assertTrue(result.contains("Hello"),
                "对话文本应原样返回不被破坏");
    }
}
