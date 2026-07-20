package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.ProgressDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.UserProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ProgressServiceImpl 单元测试
 * <p>
 * 覆盖进度查询、课时完成、顺序解锁与最高分保留等核心业务逻辑。
 * 重点验证枚举适配:由于 {@link ProgressStatus} 不含 {@code UNLOCKED},
 * 统一使用 {@code IN_PROGRESS} 表示"已解锁/可学习"语义。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
class ProgressServiceImplTest {

    @Mock
    private UserProgressRepository userProgressRepository;
    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 首课无进度记录时应返回 IN_PROGRESS(可学习)
     * <p>
     * 验证:当用户首次访问某单元 sortOrder 最小的课时且无进度记录时,
     * 系统应返回 IN_PROGRESS 状态(枚举无 UNLOCKED,用 IN_PROGRESS 表示可学习)。
     * </p>
     */
    @Test
    void should_returnInProgress_when_firstLessonAndNoProgress() {
        // Arrange:构造同单元两节课,首课 sortOrder=1
        Lesson lesson = new Lesson();
        lesson.setId(2);
        lesson.setUnitId(4);
        lesson.setSortOrder(1);

        Lesson lesson2 = new Lesson();
        lesson2.setId(3);
        lesson2.setUnitId(4);
        lesson2.setSortOrder(2);

        when(userProgressRepository.findByUserIdAndLessonId(1, 2))
                .thenReturn(Optional.empty());
        when(lessonRepository.findById(2)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findByUnitIdOrderBySortOrderAsc(4))
                .thenReturn(List.of(lesson, lesson2));

        // Act
        ProgressDto dto = progressService.getLessonProgress(2, 1);

        // Assert:首课应返回 IN_PROGRESS(而非 UNLOCKED,枚举适配)
        assertEquals("IN_PROGRESS", dto.getStatus());
        assertEquals(0, dto.getStars());
    }

    /**
     * 非首课无进度记录时应返回 LOCKED
     * <p>
     * 验证:当用户访问非首课(sortOrder 非最小)且无进度记录时,
     * 系统应返回 LOCKED 状态,表示该课时尚未解锁。
     * </p>
     */
    @Test
    void should_returnLocked_when_nonFirstLessonAndNoProgress() {
        // Arrange:lesson(sortOrder=2)为非首课,lesson2(sortOrder=1)为首课
        Lesson lesson = new Lesson();
        lesson.setId(3);
        lesson.setUnitId(4);
        lesson.setSortOrder(2);

        Lesson lesson2 = new Lesson();
        lesson2.setId(2);
        lesson2.setUnitId(4);
        lesson2.setSortOrder(1);

        when(userProgressRepository.findByUserIdAndLessonId(1, 3))
                .thenReturn(Optional.empty());
        when(lessonRepository.findById(3)).thenReturn(Optional.of(lesson));
        // 单元课时列表按 sortOrder 升序:首课在前,非首课在后
        when(lessonRepository.findByUnitIdOrderBySortOrderAsc(4))
                .thenReturn(List.of(lesson2, lesson));

        // Act
        ProgressDto dto = progressService.getLessonProgress(3, 1);

        // Assert:非首课应返回 LOCKED
        assertEquals("LOCKED", dto.getStatus());
        assertEquals(0, dto.getStars());
        assertEquals(0, dto.getScore());
    }

    /**
     * 已完成课时应返回 COMPLETED 状态及历史成绩
     * <p>
     * 验证:当用户在某课时已有完成记录时,系统应直接返回该记录的
     * 状态、星数与分数,而不查询课时信息。
     * </p>
     */
    @Test
    void should_returnCompleted_when_progressExists() {
        // Arrange:构造已完成的进度记录
        UserProgress existing = new UserProgress();
        existing.setUserId(1);
        existing.setLessonId(2);
        existing.setStatus(ProgressStatus.COMPLETED);
        existing.setStars(3);
        existing.setScore(90);

        when(userProgressRepository.findByUserIdAndLessonId(1, 2))
                .thenReturn(Optional.of(existing));

        // Act
        ProgressDto dto = progressService.getLessonProgress(2, 1);

        // Assert:应返回已完成状态及历史成绩
        assertEquals("COMPLETED", dto.getStatus());
        assertEquals(3, dto.getStars());
        assertEquals(90, dto.getScore());
    }

    /**
     * 完成课时应解锁同单元下一课时
     * <p>
     * 验证:完成当前课时后,系统应将状态置为 COMPLETED,
     * 并解锁同单元 sortOrder 更大的下一课时,返回下一课 ID 与解锁标识。
     * </p>
     */
    @Test
    void should_unlockNextLesson_when_completeCurrent() {
        // Arrange:构造当前课时与下一课时
        Lesson current = new Lesson();
        current.setId(2);
        current.setUnitId(4);
        current.setSortOrder(1);

        Lesson next = new Lesson();
        next.setId(3);
        next.setUnitId(4);
        next.setSortOrder(2);

        UserProgress existing = new UserProgress();
        existing.setUserId(1);
        existing.setLessonId(2);
        // 枚举适配:用 IN_PROGRESS 表示已解锁/可学习(而非 UNLOCKED)
        existing.setStatus(ProgressStatus.IN_PROGRESS);
        existing.setStars(0);
        existing.setScore(0);

        CompleteRequest request = new CompleteRequest();
        request.setStars(3);
        request.setScore(85);

        when(userProgressRepository.findByUserIdAndLessonId(1, 2))
                .thenReturn(Optional.of(existing));
        when(lessonRepository.findById(2)).thenReturn(Optional.of(current));
        when(lessonRepository.findByUnitIdOrderBySortOrderAsc(4))
                .thenReturn(List.of(current, next));
        // 下一课时无进度记录,解锁时会创建新记录
        when(userProgressRepository.findByUserIdAndLessonId(1, 3))
                .thenReturn(Optional.empty());

        // Act
        CompleteResponse response = progressService.completeLesson(2, request, 1);

        // Assert:应返回下一课 ID 并标记为已解锁
        assertEquals(3, response.getNextLessonId());
        assertTrue(response.getUnlocked());
        // 当前课时状态应更新为 COMPLETED
        verify(userProgressRepository).save(existing);
        assertEquals(ProgressStatus.COMPLETED, existing.getStatus());
    }

    /**
     * 最后一课完成后应返回 null nextLessonId 且不解锁
     * <p>
     * 验证:完成某单元最后一课(同单元无 sortOrder 更大的课时)时,
     * 系统应返回 nextLessonId 为 null、unlocked 为 false。
     * </p>
     */
    @Test
    void should_returnNullNextLessonId_when_completeLastLesson() {
        // Arrange:该单元仅有这一节课,完成后无下一课
        Lesson current = new Lesson();
        current.setId(5);
        current.setUnitId(4);
        current.setSortOrder(4);

        UserProgress existing = new UserProgress();
        existing.setUserId(1);
        existing.setLessonId(5);
        existing.setStatus(ProgressStatus.IN_PROGRESS);
        existing.setStars(0);
        existing.setScore(0);

        CompleteRequest request = new CompleteRequest();
        request.setStars(3);
        request.setScore(95);

        when(userProgressRepository.findByUserIdAndLessonId(1, 5))
                .thenReturn(Optional.of(existing));
        when(lessonRepository.findById(5)).thenReturn(Optional.of(current));
        // 单元内仅此一课,无 sortOrder 更大的下一课
        when(lessonRepository.findByUnitIdOrderBySortOrderAsc(4))
                .thenReturn(List.of(current));

        // Act
        CompleteResponse response = progressService.completeLesson(5, request, 1);

        // Assert:最后一课无下一课可解锁
        assertNull(response.getNextLessonId());
        assertFalse(response.getUnlocked());
        // 当前课时状态应更新为 COMPLETED
        assertEquals(ProgressStatus.COMPLETED, existing.getStatus());
    }

    /**
     * 重复挑战应保留历史最高分与最高星数
     * <p>
     * 验证:对已完成的课时再次提交更低分数时,系统应保留历史最高分,
     * 不被低分覆盖。
     * </p>
     */
    @Test
    void should_keepHighestScore_when_retryChallenge() {
        // Arrange:已有完成记录(90分、3星),本次提交更低成绩(75分、2星)
        Lesson current = new Lesson();
        current.setId(2);
        current.setUnitId(4);
        current.setSortOrder(1);

        UserProgress existing = new UserProgress();
        existing.setUserId(1);
        existing.setLessonId(2);
        existing.setStatus(ProgressStatus.COMPLETED);
        existing.setStars(3);
        existing.setScore(90);

        CompleteRequest request = new CompleteRequest();
        request.setStars(2);
        request.setScore(75);

        when(userProgressRepository.findByUserIdAndLessonId(1, 2))
                .thenReturn(Optional.of(existing));
        when(lessonRepository.findById(2)).thenReturn(Optional.of(current));
        when(lessonRepository.findByUnitIdOrderBySortOrderAsc(4))
                .thenReturn(List.of(current));

        // Act
        progressService.completeLesson(2, request, 1);

        // Assert:应保留历史最高分 90 而非本次 75,保留最高星数 3 而非 2
        assertEquals(90, existing.getScore());
        assertEquals(3, existing.getStars());
    }
}
