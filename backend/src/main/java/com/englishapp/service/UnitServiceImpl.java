package com.englishapp.service;

import com.englishapp.domain.Unit;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.UnitDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.UnitRepository;
import com.englishapp.repository.UserProgressRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 单元业务实现
 * <p>
 * 通过组合 UnitRepository、LessonRepository 与 UserProgressRepository,
 * 为前端提供带进度统计的单元列表查询能力。
 * </p>
 * <p>
 * 单元锁定状态采用"动态计算"策略:不依赖数据库中 unit 表的静态 is_locked 字段,
 * 而是根据用户进度实时推断。规则:同主题下 sortOrder 最小的单元(首单元)永远可学;
 * 后续单元仅在前一单元的所有课时均已完成时才解锁。这样避免了完成单元后需额外
 * 更新数据库的副作用,且锁定状态始终与进度保持一致。
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
     * 单元锁定状态在此方法中根据用户进度动态计算,不使用数据库静态字段。
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
        if (units.isEmpty()) {
            return List.of();
        }

        // 预计算每个单元的课时总数与已完成数,避免在循环中重复查询
        List<Integer> totals = new ArrayList<>(units.size());
        List<Integer> completeds = new ArrayList<>(units.size());
        for (Unit unit : units) {
            totals.add(lessonRepository.countByUnitId(unit.getId()));
            completeds.add(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(
                    uid, unit.getId(), ProgressStatus.COMPLETED));
        }

        // 按 sortOrder 顺序遍历,动态计算锁定状态:
        // 首单元永远可学;后续单元需前一单元所有课时完成才解锁
        List<UnitDto> result = new ArrayList<>(units.size());
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            boolean locked = computeLocked(i, totals, completeds);
            result.add(new UnitDto(unit.getId(), unit.getName(), unit.getSortOrder(),
                    locked, totals.get(i), completeds.get(i)));
        }
        return result;
    }

    /**
     * 根据索引与进度统计动态计算单元是否锁定。
     * <p>
     * 规则:
     * <ul>
     *     <li>索引 0(首单元):永远不锁定</li>
     *     <li>后续单元:前一单元课时总数 > 0 且已完成数等于总数时才解锁</li>
     * </ul>
     * </p>
     *
     * @param index      单元在排序列表中的索引
     * @param totals     各单元课时总数列表
     * @param completeds 各单元已完成课时数列表
     * @return true 表示锁定,false 表示可学
     */
    private boolean computeLocked(int index, List<Integer> totals, List<Integer> completeds) {
        // 首单元永远可学
        if (index == 0) {
            return false;
        }
        // 后续单元:前一单元所有课时完成才解锁
        int prevTotal = totals.get(index - 1);
        int prevCompleted = completeds.get(index - 1);
        // 前一单元无课时或未全部完成时,当前单元保持锁定
        return !(prevTotal > 0 && prevCompleted >= prevTotal);
    }
}
