package com.englishapp.service;

import com.englishapp.domain.Theme;
import com.englishapp.dto.ThemeDto;
import com.englishapp.repository.ThemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 主题服务
 * <p>
 * 提供主题查询能力，支持按学科筛选或获取全部主题。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    /**
     * 构造主题服务
     * @param themeRepository 主题数据访问接口
     */
    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    /**
     * 获取所有主题列表（兼容旧接口，已标记废弃）
     * @return 主题 DTO 列表
     */
    @Deprecated
    public List<ThemeDto> getAllThemes() {
        return themeRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 按学科 ID 获取主题列表
     * @param subjectId 学科 ID
     * @return 该学科下的主题 DTO 列表
     */
    public List<ThemeDto> getThemesBySubjectId(Integer subjectId) {
        return themeRepository.findBySubjectIdOrderBySortOrderAsc(subjectId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 实体转 DTO
     * @param theme 主题实体
     * @return 主题 DTO
     */
    private ThemeDto toDto(Theme theme) {
        return new ThemeDto(
                theme.getId(),
                theme.getName(),
                theme.getIconUrl(),
                theme.getSortOrder(),
                theme.getIsLocked(),
                theme.getSubjectId()
        );
    }
}
