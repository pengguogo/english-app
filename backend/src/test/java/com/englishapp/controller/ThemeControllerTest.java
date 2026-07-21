package com.englishapp.controller;

import com.englishapp.domain.Theme;
import com.englishapp.repository.ThemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ThemeRepository themeRepository;

    @BeforeEach
    void setUp() {
        themeRepository.deleteAll();
        Theme theme = new Theme();
        theme.setName("水果乐园");
        theme.setSortOrder(1);
        theme.setIsLocked(false);
        themeRepository.save(theme);
    }

    @Test
    void shouldReturnThemes() throws Exception {
        mockMvc.perform(get("/api/v1/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("水果乐园"));
    }
}
