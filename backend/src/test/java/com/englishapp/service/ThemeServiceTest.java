package com.englishapp.service;

import com.englishapp.domain.Theme;
import com.englishapp.dto.ThemeDto;
import com.englishapp.repository.ThemeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @InjectMocks
    private ThemeService themeService;

    @Test
    void shouldReturnAllThemesSorted() {
        Theme theme = new Theme();
        theme.setId(1);
        theme.setName("水果乐园");
        theme.setSortOrder(1);
        theme.setIsLocked(false);

        when(themeRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(theme));

        List<ThemeDto> result = themeService.getAllThemes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("水果乐园");
    }
}
