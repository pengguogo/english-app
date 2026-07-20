package com.englishapp.service;

import com.englishapp.domain.Unit;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.UnitDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.UnitRepository;
import com.englishapp.repository.UserProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UnitServiceImpl 单元测试
 * <p>
 * 重点验证单元锁定状态的动态计算逻辑:不依赖数据库静态 is_locked 字段,
 * 而是根据用户进度实时推断——首单元永远可学,后续单元需前一单元全部完成才解锁。
 * </p>
 *
 * @author englishapp
 * @since 1.1.0
 */
class UnitServiceImplTest {

    @Mock
    private UnitRepository unitRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private UserProgressRepository userProgressRepository;

    @InjectMocks
    private UnitServiceImpl unitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 构造测试用 Unit 实体
     */
    private Unit makeUnit(int id, int themeId, String name, int sortOrder) {
        Unit u = new Unit();
        u.setId(id);
        u.setThemeId(themeId);
        u.setName(name);
        u.setSortOrder(sortOrder);
        // 数据库静态字段设为 true,验证动态计算不依赖它
        u.setIsLocked(true);
        return u;
    }

    /**
     * 首单元永远应解锁,即使数据库 is_locked=true
     * <p>
     * 验证:动态计算忽略数据库静态字段,首单元(sortOrder 最小)始终可学。
     * </p>
     */
    @Test
    void should_unlockFirstUnit_when_databaseSaysLocked() {
        // Arrange:主题2 下两个单元,数据库都标记为锁定
        Unit u1 = makeUnit(4, 2, "陆地交通", 1);
        Unit u2 = makeUnit(5, 2, "空中交通", 2);
        when(unitRepository.findByThemeIdOrderBySortOrderAsc(2))
                .thenReturn(List.of(u1, u2));
        when(lessonRepository.countByUnitId(4)).thenReturn(4);
        when(lessonRepository.countByUnitId(5)).thenReturn(3);
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 4, ProgressStatus.COMPLETED))
                .thenReturn(0);
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 5, ProgressStatus.COMPLETED))
                .thenReturn(0);

        // Act
        List<UnitDto> result = unitService.getUnitsByTheme(2, 1);

        // Assert:首单元应解锁(不锁定),第二单元应锁定
        assertEquals(2, result.size());
        assertFalse(result.get(0).getIsLocked(), "首单元应解锁");
        assertTrue(result.get(1).getIsLocked(), "第二单元未达解锁条件应锁定");
    }

    /**
     * 前一单元全部完成时,后一单元应自动解锁
     * <p>
     * 验证:当用户完成第一单元全部 4 课时后,第二单元应从锁定变为可学。
     * 这是本次改进的核心场景:完成一个单元后自动解锁下一个单元。
     * </p>
     */
    @Test
    void should_unlockNextUnit_when_previousUnitFullyCompleted() {
        // Arrange:主题2 下两个单元
        Unit u1 = makeUnit(4, 2, "陆地交通", 1);
        Unit u2 = makeUnit(5, 2, "空中交通", 2);
        when(unitRepository.findByThemeIdOrderBySortOrderAsc(2))
                .thenReturn(List.of(u1, u2));
        when(lessonRepository.countByUnitId(4)).thenReturn(4);
        when(lessonRepository.countByUnitId(5)).thenReturn(3);
        // 第一单元已完成全部 4 课时
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 4, ProgressStatus.COMPLETED))
                .thenReturn(4);
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 5, ProgressStatus.COMPLETED))
                .thenReturn(0);

        // Act
        List<UnitDto> result = unitService.getUnitsByTheme(2, 1);

        // Assert:第一单元已完成,第二单元应解锁
        assertFalse(result.get(0).getIsLocked(), "第一单元应可学");
        assertEquals(4, result.get(0).getCompletedLessons());
        assertFalse(result.get(1).getIsLocked(), "前一单元全部完成后,第二单元应解锁");
    }

    /**
     * 前一单元部分完成时,后一单元应保持锁定
     * <p>
     * 验证:第一单元 4 课时仅完成 3 课时(未全部完成),第二单元应保持锁定。
     * </p>
     */
    @Test
    void should_keepNextUnitLocked_when_previousUnitPartiallyCompleted() {
        // Arrange
        Unit u1 = makeUnit(4, 2, "陆地交通", 1);
        Unit u2 = makeUnit(5, 2, "空中交通", 2);
        when(unitRepository.findByThemeIdOrderBySortOrderAsc(2))
                .thenReturn(List.of(u1, u2));
        when(lessonRepository.countByUnitId(4)).thenReturn(4);
        when(lessonRepository.countByUnitId(5)).thenReturn(3);
        // 第一单元仅完成 3/4 课时
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 4, ProgressStatus.COMPLETED))
                .thenReturn(3);
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 5, ProgressStatus.COMPLETED))
                .thenReturn(0);

        // Act
        List<UnitDto> result = unitService.getUnitsByTheme(2, 1);

        // Assert:第一单元未全部完成,第二单元应锁定
        assertFalse(result.get(0).getIsLocked());
        assertTrue(result.get(1).getIsLocked(), "前一单元未全部完成时,第二单元应保持锁定");
    }

    /**
     * 连续解锁:前两单元全部完成时,第三单元也应解锁
     * <p>
     * 验证三个单元的连锁解锁:第一单元全部完成 → 第二单元解锁;
     * 第二单元也全部完成 → 第三单元解锁。
     * </p>
     */
    @Test
    void should_unlockThirdUnit_when_firstTwoUnitsCompleted() {
        // Arrange:三个单元
        Unit u1 = makeUnit(4, 2, "陆地交通", 1);
        Unit u2 = makeUnit(5, 2, "空中交通", 2);
        Unit u3 = makeUnit(6, 2, "水上交通", 3);
        when(unitRepository.findByThemeIdOrderBySortOrderAsc(2))
                .thenReturn(List.of(u1, u2, u3));
        when(lessonRepository.countByUnitId(4)).thenReturn(4);
        when(lessonRepository.countByUnitId(5)).thenReturn(3);
        when(lessonRepository.countByUnitId(6)).thenReturn(3);
        // 第一单元全部完成,第二单元全部完成
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 4, ProgressStatus.COMPLETED))
                .thenReturn(4);
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 5, ProgressStatus.COMPLETED))
                .thenReturn(3);
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 6, ProgressStatus.COMPLETED))
                .thenReturn(0);

        // Act
        List<UnitDto> result = unitService.getUnitsByTheme(2, 1);

        // Assert:三个单元都应解锁
        assertEquals(3, result.size());
        assertFalse(result.get(0).getIsLocked(), "第一单元应可学");
        assertFalse(result.get(1).getIsLocked(), "第一单元完成后第二单元应解锁");
        assertFalse(result.get(2).getIsLocked(), "前两单元全部完成后第三单元应解锁");
    }

    /**
     * 中间单元未完成时,第三个单元应保持锁定
     * <p>
     * 验证:第一单元完成、第二单元未全部完成时,第三单元应锁定(不能跳过)。
     * </p>
     */
    @Test
    void should_keepThirdUnitLocked_when_middleUnitNotCompleted() {
        // Arrange
        Unit u1 = makeUnit(4, 2, "陆地交通", 1);
        Unit u2 = makeUnit(5, 2, "空中交通", 2);
        Unit u3 = makeUnit(6, 2, "水上交通", 3);
        when(unitRepository.findByThemeIdOrderBySortOrderAsc(2))
                .thenReturn(List.of(u1, u2, u3));
        when(lessonRepository.countByUnitId(4)).thenReturn(4);
        when(lessonRepository.countByUnitId(5)).thenReturn(3);
        when(lessonRepository.countByUnitId(6)).thenReturn(3);
        // 第一单元完成,第二单元仅完成 1/3
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 4, ProgressStatus.COMPLETED))
                .thenReturn(4);
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 5, ProgressStatus.COMPLETED))
                .thenReturn(1);
        when(userProgressRepository.countByUserIdAndLessonUnitIdAndStatus(1, 6, ProgressStatus.COMPLETED))
                .thenReturn(0);

        // Act
        List<UnitDto> result = unitService.getUnitsByTheme(2, 1);

        // Assert:第二单元可学但未完成,第三单元应锁定
        assertFalse(result.get(0).getIsLocked());
        assertFalse(result.get(1).getIsLocked(), "第一单元完成后第二单元应可学");
        assertTrue(result.get(2).getIsLocked(), "第二单元未全部完成时第三单元应锁定");
    }

    /**
     * 空主题应返回空列表
     */
    @Test
    void should_returnEmptyList_when_themeHasNoUnits() {
        when(unitRepository.findByThemeIdOrderBySortOrderAsc(99))
                .thenReturn(List.of());

        List<UnitDto> result = unitService.getUnitsByTheme(99, 1);

        assertTrue(result.isEmpty());
    }
}
