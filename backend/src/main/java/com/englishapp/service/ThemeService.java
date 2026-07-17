package com.englishapp.service;

import com.englishapp.domain.Theme;
import com.englishapp.dto.ThemeDto;
import com.englishapp.repository.ThemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<ThemeDto> getAllThemes() {
        return themeRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ThemeDto toDto(Theme theme) {
        return new ThemeDto(
                theme.getId(),
                theme.getName(),
                theme.getIconUrl(),
                theme.getSortOrder(),
                theme.getIsLocked()
        );
    }
}
