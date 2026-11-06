package com.englishapp.service;

import com.englishapp.domain.DailyStudyTime;
import com.englishapp.domain.StudyTimeEvent;
import com.englishapp.repository.StudyTimeEventRepository;
import com.englishapp.dto.GameAccessDto;
import com.englishapp.dto.GameUnlockDto;
import com.englishapp.repository.DailyStudyTimeRepository;
import com.englishapp.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GameAccessServiceImpl implements GameAccessService {
    static final int REQUIRED_SECONDS = 300;
    private final ParentPasswordService passwords;
    private final StudyTimeEventRepository events;
    private final DailyStudyTimeRepository repository;
    private final LessonRepository lessons;

    public GameAccessServiceImpl(DailyStudyTimeRepository repository,
                                 LessonRepository lessons,
                                 ParentPasswordService passwords, StudyTimeEventRepository events) {
        this.repository = repository;
        this.lessons = lessons;
        this.passwords = passwords;
        this.events = events;
    }

    @Override
    @Transactional
    public GameAccessDto recordPendingStudyTime(Integer userId, Integer lessonId, int seconds) {
        return recordPendingStudyTime(userId, lessonId, seconds, null);
    }

    @Override
    @Transactional
    public GameAccessDto recordPendingStudyTime(Integer userId, Integer lessonId, int seconds, String eventId) {
        if (seconds < 1 || seconds > 30) throw new IllegalArgumentException("每次学习时长须为1至30秒");
        if (lessonId == null || !lessons.existsById(lessonId)) throw new IllegalArgumentException("课时不存在");
        LocalDate today = LocalDate.now();
        String id = eventId == null ? java.util.UUID.randomUUID().toString() : eventId;
        StudyTimeEvent existing = events.findById(id).orElse(null);
        if (existing != null) {
            if (!existing.getUserId().equals(userId) || !existing.getLessonId().equals(lessonId)
                    || existing.getSeconds() != seconds) throw new IllegalArgumentException("计时事件与原请求不一致");
            return getTodayAccess(userId);
        }
        StudyTimeEvent event = new StudyTimeEvent();
        event.setId(id);
        event.setUserId(userId);
        event.setLessonId(lessonId);
        event.setStudyDate(today);
        event.setSeconds(seconds);
        events.save(event);
        return recordStudyTime(userId, seconds);
    }

    @Override
    public GameAccessDto getTodayAccess(Integer userId) {
        DailyStudyTime record = repository.findByUserIdAndStudyDate(userId, LocalDate.now()).orElse(null);
        return toAccess(record == null ? 0 : record.getSeconds(), record != null && Boolean.TRUE.equals(record.getParentUnlocked()));
    }

    @Override
    @Transactional
    public GameAccessDto recordStudyTime(Integer userId, int seconds) {
        LocalDate today = LocalDate.now();
        DailyStudyTime record = repository.findByUserIdAndStudyDate(userId, today)
                .orElseGet(() -> newRecord(userId, today));
        record.setSeconds(Math.min(REQUIRED_SECONDS, record.getSeconds() + seconds));
        record.setUpdatedAt(LocalDateTime.now());
        repository.save(record);
        return toAccess(record.getSeconds(), Boolean.TRUE.equals(record.getParentUnlocked()));
    }

    @Override
    @Transactional
    public GameUnlockDto unlockWithPassword(Integer userId, String password) {
        // 密码错误或锁定时 verify 直接抛 AuthException,不会走到下面的写库逻辑
        passwords.verify(password);
        DailyStudyTime record = repository.findByUserIdAndStudyDate(userId, LocalDate.now())
                .orElseGet(() -> newRecord(userId, LocalDate.now()));
        record.setParentUnlocked(true);
        record.setUpdatedAt(LocalDateTime.now());
        repository.save(record);
        GameAccessDto access = toAccess(record.getSeconds(), true);
        return new GameUnlockDto(true, "今日游戏已解锁", access);
    }

    private DailyStudyTime newRecord(Integer userId, LocalDate date) {
        DailyStudyTime record = new DailyStudyTime();
        record.setUserId(userId);
        record.setStudyDate(date);
        record.setSeconds(0);
        return record;
    }

    private GameAccessDto toAccess(int seconds, boolean parentUnlocked) {
        int studied = Math.min(seconds, REQUIRED_SECONDS);
        return new GameAccessDto(studied, REQUIRED_SECONDS,
                Math.max(0, REQUIRED_SECONDS - studied), parentUnlocked || studied >= REQUIRED_SECONDS);
    }
}
