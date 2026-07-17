package com.englishapp.repository;

import com.englishapp.domain.Theme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ThemeRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    void shouldSaveAndFindTheme() {
        Theme theme = new Theme();
        theme.setName("水果乐园");
        theme.setSortOrder(1);
        theme.setIsLocked(false);

        Theme saved = themeRepository.save(theme);
        List<Theme> themes = themeRepository.findAllByOrderBySortOrderAsc();

        assertThat(themes).hasSize(1);
        assertThat(themes.get(0).getName()).isEqualTo("水果乐园");
    }
}
