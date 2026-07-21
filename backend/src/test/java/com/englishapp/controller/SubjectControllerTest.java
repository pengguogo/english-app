package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.SubjectDto;
import com.englishapp.service.SubjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 学科控制器单元测试
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("学科控制器测试")
class SubjectControllerTest {

    @Mock
    private SubjectService subjectService;

    @InjectMocks
    private SubjectController subjectController;

    @Test
    @DisplayName("should_returnSubjects_when_调用getAllSubjects")
    void should_returnSubjects_when_调用getAllSubjects() {
        SubjectDto dto = new SubjectDto(1, "英语", "ENGLISH", null, "#6B7CFF", 1, false);
        when(subjectService.getAllSubjects()).thenReturn(List.of(dto));

        Result<List<SubjectDto>> result = subjectController.getAllSubjects();

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).name()).isEqualTo("英语");
    }
}
