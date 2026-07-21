package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.domain.enums.LessonType;
import com.englishapp.dto.LessonDto;
import com.englishapp.repository.LessonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * LessonService 单元测试
 * <p>
 * 验证 PHONICS/DIALOGUE 新课型课时详情接口的正确性，
 * 以及小砾工程 Lesson 35 官方角色替换结果。
 * </p>
 *
 * @author english-app
 * @since 2026-07-21
 */
@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private WordImageResolver wordImageResolver;

    @InjectMocks
    private LessonService lessonService;

    @Test
    void should_returnPhonicsContent_when_getLessonByIdForPhonics() {
        // Arrange:构造 PHONICS 类型课时,content 包含字母发音规则
        String phonicsContent = "{\"type\":\"PHONICS\",\"letter\":\"F\","
                + "\"pronunciation\":\"/ef/\",\"sound\":\"/f/\","
                + "\"tip\":\"上齿咬下唇,气流从牙缝中挤出\","
                + "\"items\":[{\"word\":\"fish\",\"image\":\"fish\"},"
                + "{\"word\":\"fan\",\"image\":\"fan\"}]}";
        Lesson lesson = buildLesson(113, 11, "字母 F 自然拼读",
                LessonType.PHONICS, phonicsContent, 3, 3);

        when(lessonRepository.findById(113)).thenReturn(Optional.of(lesson));
        // resolveContent 原样返回传入的 content(详情接口的图片 URL 解析在此测试中不关注)
        when(wordImageResolver.resolveContent(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LessonDto dto = lessonService.getLessonById(113);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo("PHONICS");
        assertThat(dto.content()).contains("letter");
        assertThat(dto.content()).contains("sound");
    }

    @Test
    void should_returnDialogueContent_when_getLessonByIdForDialogue() {
        // Arrange:构造 DIALOGUE 类型课时,content 包含情景对话
        String dialogueContent = "{\"type\":\"DIALOGUE\",\"scene\":\"水果店\","
                + "\"tip\":\"模仿店员和顾客的语气进行对话\","
                + "\"items\":[{\"speaker\":\"Mimi\",\"text\":\"Can I have an apple?\","
                + "\"translation\":\"我能要一个苹果吗?\",\"audio\":\"\"}]}";
        Lesson lesson = buildLesson(114, 11, "水果店对话",
                LessonType.DIALOGUE, dialogueContent, 4, 3);

        when(lessonRepository.findById(114)).thenReturn(Optional.of(lesson));
        when(wordImageResolver.resolveContent(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LessonDto dto = lessonService.getLessonById(114);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.type()).isEqualTo("DIALOGUE");
        assertThat(dto.content()).contains("speaker");
        assertThat(dto.content()).contains("scene");
    }

    @Test
    void should_returnOfficialCharacters_when_getLesson35AfterMigration() {
        // Arrange:小砾工程 Lesson 35 已迁移为官方角色(Rubble/Mix),不再含原创角色 Dozer
        String migratedContent = "{\"type\":\"WORD\",\"items\":["
                + "{\"word\":\"Rubble\",\"image\":\"rubble\",\"translation\":\"小砾\"},"
                + "{\"word\":\"Mix\",\"image\":\"mix\",\"translation\":\"小砺\"}]}";
        Lesson lesson = buildLesson(35, 7, "小砾工程",
                LessonType.WORD, migratedContent, 1, 3);

        when(lessonRepository.findById(35)).thenReturn(Optional.of(lesson));
        when(wordImageResolver.resolveContent(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LessonDto dto = lessonService.getLessonById(35);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.content()).contains("Rubble");
        assertThat(dto.content()).contains("Mix");
        assertThat(dto.content()).doesNotContain("Dozer");
    }

    /**
     * 构建测试用 Lesson 实体,集中设置字段避免重复代码。
     *
     * @param id         课时 ID
     * @param unitId     所属单元 ID
     * @param name       课时名称
     * @param type       课时类型
     * @param content    学习内容 JSON
     * @param sortOrder  排序
     * @param starReward 星星奖励
     * @return 已设置好字段的 Lesson 实体
     */
    private Lesson buildLesson(Integer id, Integer unitId, String name,
                               LessonType type, String content,
                               Integer sortOrder, Integer starReward) {
        Lesson lesson = new Lesson();
        lesson.setId(id);
        lesson.setUnitId(unitId);
        lesson.setName(name);
        lesson.setType(type);
        lesson.setContent(content);
        lesson.setSortOrder(sortOrder);
        lesson.setStarReward(starReward);
        return lesson;
    }
}
