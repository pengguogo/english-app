package com.englishapp.service;

import com.englishapp.domain.DailyStudyTime;
import com.englishapp.dto.GameAccessDto;
import com.englishapp.dto.GameUnlockDto;
import com.englishapp.repository.DailyStudyTimeRepository;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.LessonStudySessionRepository;
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
    @Mock
    private LessonStudySessionRepository sessions;
    @Mock
    private LessonRepository lessons;
    @Mock private ParentPasswordService passwords;
    @Mock private com.englishapp.repository.StudyTimeEventRepository events;
    private GameAccessServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GameAccessServiceImpl(repository, sessions, lessons, passwords, events);
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
    void should_暂存时长且不解锁游戏_当_复习尚未通过() {
        when(lessons.existsById(7)).thenReturn(true);
        when(sessions.findByUserIdAndLessonIdAndStudyDate(1, 7, LocalDate.now())).thenReturn(Optional.empty());
        when(repository.findByUserIdAndStudyDate(1, LocalDate.now())).thenReturn(Optional.empty());

        GameAccessDto access = service.recordPendingStudyTime(1, 7, 15);

        assertFalse(access.unlocked());
        assertEquals(0, access.studiedSeconds());
        verify(sessions).save(argThat(session -> session.getPendingSeconds() == 15));
        verify(repository, never()).save(any());
    }

    @Test
    void should_密码解锁今日游戏_当_密码正确() {
        DailyStudyTime record = studyTime(30);
        when(repository.findByUserIdAndStudyDate(1, LocalDate.now()))
                .thenReturn(Optional.of(record));

        when(passwords.verify("246810")).thenReturn(true);
        GameUnlockDto result = service.unlockWithPassword(1, "246810");

        assertTrue(result.success());
        assertTrue(result.access().unlocked());
        assertEquals(30, record.getSeconds());
        assertTrue(record.getParentUnlocked());
        verifyNoInteractions(events);
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

    @Test
    void should_不重复累加_当_计时事件重试() {
        var event = new com.englishapp.domain.StudyTimeEvent();
        event.setUserId(1); event.setLessonId(7); event.setSeconds(8);
        when(lessons.existsById(7)).thenReturn(true);
        when(events.findById("same-event")).thenReturn(Optional.of(event));
        when(repository.findByUserIdAndStudyDate(1, LocalDate.now())).thenReturn(Optional.empty());
        service.recordPendingStudyTime(1, 7, 8, "same-event");
        verify(events, never()).save(any());
        verifyNoInteractions(sessions);
    }

    @Test
    void should_保留实际时长且结算尾段_当_复习已通过() {
        var session = new com.englishapp.domain.LessonStudySession();
        session.setPassed(true); session.setPendingSeconds(0);
        when(lessons.existsById(7)).thenReturn(true);
        when(sessions.findByUserIdAndLessonIdAndStudyDate(1, 7, LocalDate.now())).thenReturn(Optional.of(session));
        when(repository.findByUserIdAndStudyDate(1, LocalDate.now())).thenReturn(Optional.of(studyTime(300)));
        var access = service.recordPendingStudyTime(1, 7, 8, "tail-event");
        assertTrue(access.unlocked());
        verify(events).save(argThat(e -> e.getSeconds() == 8));
        verify(repository).save(any());
    }

    @Test
    void should_拒绝计时_当_秒数无效或事件被改写() {
        assertThrows(IllegalArgumentException.class, () -> service.recordPendingStudyTime(1, 7, 0));
        when(lessons.existsById(7)).thenReturn(true);
        var event = new com.englishapp.domain.StudyTimeEvent();
        event.setUserId(1); event.setLessonId(7); event.setSeconds(8);
        when(events.findById("same-event")).thenReturn(Optional.of(event));
        assertThrows(IllegalArgumentException.class, () -> service.recordPendingStudyTime(1, 7, 9, "same-event"));
        verify(events, never()).save(any());
    }

    private DailyStudyTime studyTime(int seconds) {
        DailyStudyTime record = new DailyStudyTime();
        record.setUserId(1);
        record.setStudyDate(LocalDate.now());
        record.setSeconds(seconds);
        return record;
    }
}
