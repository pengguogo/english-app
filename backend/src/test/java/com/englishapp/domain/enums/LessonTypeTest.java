package com.englishapp.domain.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 课时类型枚举 {@link LessonType} 的单元测试
 * <p>
 * 验证枚举值的完整性与 valueOf 行为，确保新增的
 * PHONICS（自然拼读）与 DIALOGUE（情景对话）类型可被正确识别。
 * </p>
 *
 * @author englishapp
 * @since 1.1.0
 */
class LessonTypeTest {

    /**
     * 验证枚举包含全部 9 个预期值（原有 7 个 + 新增 PHONICS / DIALOGUE）。
     * <p>
     * 使用 assertThat 直接断言数量与具体值，避免遗漏新增类型。
     * </p>
     */
    @Test
    void should_containAllExpectedTypes_when_enumValuesChecked() {
        // Arrange: 定义全部预期枚举值
        List<LessonType> expected = Arrays.asList(
                LessonType.WORD,
                LessonType.SENTENCE,
                LessonType.READING,
                LessonType.QUIZ,
                LessonType.CALCULATE,
                LessonType.GAME,
                LessonType.SPEAK,
                LessonType.PHONICS,
                LessonType.DIALOGUE
        );

        // Act: 获取枚举全部值
        LessonType[] actual = LessonType.values();

        // Assert: 数量与内容均需匹配
        assertThat(actual).hasSize(9);
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    /**
     * 验证通过字符串 "PHONICS" 可正确解析为对应枚举实例。
     */
    @Test
    void should_returnPhonicsEnum_when_valueOfPhonicsCalled() {
        // Act
        LessonType result = LessonType.valueOf("PHONICS");

        // Assert
        assertThat(result).isEqualTo(LessonType.PHONICS);
    }

    /**
     * 验证通过字符串 "DIALOGUE" 可正确解析为对应枚举实例。
     */
    @Test
    void should_returnDialogueEnum_when_valueOfDialogueCalled() {
        // Act
        LessonType result = LessonType.valueOf("DIALOGUE");

        // Assert
        assertThat(result).isEqualTo(LessonType.DIALOGUE);
    }
}
