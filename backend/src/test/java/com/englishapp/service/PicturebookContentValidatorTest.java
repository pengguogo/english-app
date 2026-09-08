package com.englishapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PicturebookContentValidatorTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private ObjectNode quiz() throws Exception {
        return (ObjectNode) mapper.readTree("""
                {"type":"QUIZ","picturebook":true,"bookTitle":"看图找朋友","items":[
                  {"image":"picturebooks/train-station","audioLanguage":"en","audioText":"Find the rabbit.",
                   "options":[{"text":"小兔","image":"pet/rabbit","showText":false},"A train."],"answer":0}
                ]}
                """);
    }

    @Test
    void should_兼容图片与旧文字选项_当_选项完整() throws Exception {
        var root = quiz();

        assertThatCode(() -> PicturebookContentValidator.validate(root)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "../pet/rabbit", "https://example.com/rabbit.jpg", "pet/rabbit.jpg"})
    void should_拒绝无法解析的图片选项_当_配图无效(String image) throws Exception {
        var root = quiz();
        ((ObjectNode) root.path("items").get(0).path("options").get(0)).put("image", image);

        assertThatThrownBy(() -> PicturebookContentValidator.validate(root))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("绘本选项配图 key 无效");
    }

    @Test
    void should_拒绝无法试听的图片选项_当_说明为空() throws Exception {
        var root = quiz();
        ((ObjectNode) root.path("items").get(0).path("options").get(0)).put("text", " ");

        assertThatThrownBy(() -> PicturebookContentValidator.validate(root))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("绘本图片选项必须有语音说明");
    }
}
