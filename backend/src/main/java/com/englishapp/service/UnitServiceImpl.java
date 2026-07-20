package com.englishapp.service;

import com.englishapp.domain.Unit;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.UnitDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.UnitRepository;
import com.englishapp.repository.UserProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 单元业务实现
 * <p>
 * 通过组合 UnitRepository、LessonRepository 与 UserProgressRepository,
 * 为前端提供带进度统计的单元列表查询能力。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@Service
public class UnitServiceImpl implements UnitService {

    /** 默认用户 ID(在未传入 userId 时使用) */
    private static final Integer DEFAULT_USER_ID = 1;

    private final UnitRepository unitRepository;
    private final LessonRepository lessonRepository;
    private final UserProgressRepository userProgressRepository;

    /**
     * 构造单元业务实现
     *
     * @param unitRepository        单元仓库
     * @param lessonRepository      课程仓库
     * @param userProgressRepository 用户进度仓库
     */
    public UnitServiceImpl(UnitRepository unitRepository,
                           LessonRepository lessonRepository,
                           UserProgressRepository userProgressRepository) {
        this.unitRepository = unitRepository;
        this.lessonRepository = lessonRepository;
        this.userProgressRepository = userProgressRepository;
    }

    /**
     * 获取某主题下所有单元(带进度)
     * <p>
     * 当 userId 为 null 时回退到默认用户 ID,保证接口在匿名访问场景下也可用。
     * </p>
     *
     * @param themeId 主题 ID
     * @param userId  用户 ID(为 null 时使用默认用户)
     * @return 单元 DTO 列表,按排序序号升序
     */
    @Override
    public List<UnitDto> getUnitsByTheme(Integer themeId, Integer userId) {
        // 未指定用户时使用默认用户,便于匿名/调试场景
        Integer uid = userId != null ? userId : DEFAULT_USER_ID;
        List<Unit> units = unitRepository.findByThemeIdOrderBySortOrderAsc(themeId);
        return units.stream().map(unit -> toDto(unit, uid)).toList();
    }

    /**
     * 将单元实体转换为带进度的 DTO
     * <p>
     * 通过查询课程总数与该用户在该单元下已完成的课程数,组装进度信息。
     * </p>
     *
     * @param unit   单元实体
     * @param userId 用户 ID
     * @return 单元 DTO
     */
    private UnitDto toDto(Unit unit, Integer userId) {
        Integer total = lessonRepository.countByUnitId(unit.getId());
        Integer completed = userProgressRepository
                .countByUserIdAndLessonUnitIdAndStatus(userId, unit.getId(), ProgressStatus.COMPLETED);
        return new UnitDto(unit.getId(), unit.getName(), unit.getSortOrder(),
                unit.getIsLocked(), total, completed);
    }
}
