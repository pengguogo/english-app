package com.englishapp.service;

import com.englishapp.domain.Subject;
import com.englishapp.dto.SubjectDto;
import com.englishapp.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
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
 * 学科服务实现单元测试
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("学科服务测试")
class SubjectServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    private Subject englishSubject;
    private Subject chineseSubject;

    @BeforeEach
    void setUp() {
        englishSubject = new Subject();
        englishSubject.setId(1);
        englishSubject.setName("英语");
        englishSubject.setCode("ENGLISH");
        englishSubject.setColor("#6B7CFF");
        englishSubject.setSortOrder(1);
        englishSubject.setIsLocked(false);

        chineseSubject = new Subject();
        chineseSubject.setId(2);
        chineseSubject.setName("语文");
        chineseSubject.setCode("CHINESE");
        chineseSubject.setColor("#FF7043");
        chineseSubject.setSortOrder(2);
        chineseSubject.setIsLocked(false);
    }

    @Test
    @DisplayName("should_returnAllSubjects_when_调用getAllSubjects")
    void should_returnAllSubjects_when_调用getAllSubjects() {
        when(subjectRepository.findAllByOrderBySortOrderAsc())
                .thenReturn(List.of(englishSubject, chineseSubject));

        List<SubjectDto> result = subjectService.getAllSubjects();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("英语");
        assertThat(result.get(0).code()).isEqualTo("ENGLISH");
        assertThat(result.get(0).color()).isEqualTo("#6B7CFF");
        assertThat(result.get(1).name()).isEqualTo("语文");
    }

    @Test
    @DisplayName("should_returnEmptyList_when_无学科数据")
    void should_returnEmptyList_when_无学科数据() {
        when(subjectRepository.findAllByOrderBySortOrderAsc())
                .thenReturn(List.of());

        List<SubjectDto> result = subjectService.getAllSubjects();

        assertThat(result).isEmpty();
    }
}
