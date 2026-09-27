package com.englishapp.service;

import com.englishapp.domain.DailyStudyTime;
import com.englishapp.dto.GameAccessDto;
import com.englishapp.dto.GameUnlockDto;
import com.englishapp.repository.DailyStudyTimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameAccessServiceImplTest {
    @Mock
    private DailyStudyTimeRepository repository;
    private GameAccessServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GameAccessServiceImpl(repository);
    }

    @Test
    void should_保持锁定_当_今日学习不足五分钟() {
        DailyStudyTime record = studyTime(240);
        when(repository.findByUserIdAndStudyDate(1, LocalDate.now()))
                .thenReturn(Optional.of(record));

        GameAccessDto access = service.getTodayAccess(1);

        assertFalse(access.unlocked());
        assertEquals(60, access.remainingSeconds());
    }

    @Test
    void should_解锁游戏_当_累计学习达到五分钟() {
        DailyStudyTime record = studyTime(290);
        when(repository.findByUserIdAndStudyDate(1, LocalDate.now()))
                .thenReturn(Optional.of(record));

        GameAccessDto access = service.recordStudyTime(1, 15);

        assertTrue(access.unlocked());
        assertEquals(300, access.studiedSeconds());
        verify(repository).save(record);
    }

    @Test
    void should_新建今日记录_当_首次学习() {
        when(repository.findByUserIdAndStudyDate(1, LocalDate.now()))
                .thenReturn(Optional.empty());

        GameAccessDto access = service.recordStudyTime(1, 15);

        assertEquals(15, access.studiedSeconds());
        verify(repository).save(any(DailyStudyTime.class));
    }

    @Test
    void should_密码解锁今日游戏_当_密码正确() {
        DailyStudyTime record = studyTime(30);
        when(repository.findByUserIdAndStudyDate(1, LocalDate.now()))
                .thenReturn(Optional.of(record));

        GameUnlockDto result = service.unlockWithPassword(1, "000000");

        assertTrue(result.success());
        assertTrue(result.access().unlocked());
        assertEquals(300, record.getSeconds());
        verify(repository).save(record);
    }

    @Test
    void should_拒绝解锁_当_密码错误() {
        when(repository.findByUserIdAndStudyDate(1, LocalDate.now()))
                .thenReturn(Optional.empty());

        GameUnlockDto result = service.unlockWithPassword(1, "123456");

        assertFalse(result.success());
        assertFalse(result.access().unlocked());
        verify(repository, never()).save(any());
    }

    private DailyStudyTime studyTime(int seconds) {
        DailyStudyTime record = new DailyStudyTime();
        record.setUserId(1);
        record.setStudyDate(LocalDate.now());
        record.setSeconds(seconds);
        return record;
    }
}
