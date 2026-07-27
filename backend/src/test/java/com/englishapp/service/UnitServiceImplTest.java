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
 * 重点验证取消单元锁定后,所有单元均可自由进入,
 * 同时仍正确返回每个单元的学习进度。
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

        // Assert:取消锁定后两个单元均可学习
        assertEquals(2, result.size());
        assertFalse(result.get(0).getIsLocked(), "首单元应解锁");
        assertFalse(result.get(1).getIsLocked(), "第二单元也应可学习");
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
     * 前一单元部分完成时,后一单元仍可学习
     * <p>
     * 验证:取消锁定后,学习进度不再限制单元入口。
     * </p>
     */
    @Test
    void should_keepNextUnitAvailable_when_previousUnitPartiallyCompleted() {
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

        // Assert:第一单元未全部完成,第二单元仍可学习
        assertFalse(result.get(0).getIsLocked());
        assertFalse(result.get(1).getIsLocked(), "前一单元未完成也不应锁定第二单元");
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
     * 中间单元未完成时,第三个单元仍可学习
     * <p>
     * 验证:取消锁定后,用户可以跳过未完成单元。
     * </p>
     */
    @Test
    void should_keepThirdUnitAvailable_when_middleUnitNotCompleted() {
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

        // Assert:第二单元未完成,第三单元仍可学习
        assertFalse(result.get(0).getIsLocked());
        assertFalse(result.get(1).getIsLocked(), "第一单元完成后第二单元应可学");
        assertFalse(result.get(2).getIsLocked(), "第二单元未完成也不应锁定第三单元");
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
