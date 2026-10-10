package com.englishapp.service;

import com.englishapp.domain.DailyStudyTime;
import com.englishapp.domain.StudyTimeEvent;
import com.englishapp.repository.StudyTimeEventRepository;
import com.englishapp.domain.LessonStudySession;
import com.englishapp.dto.GameAccessDto;
import com.englishapp.dto.GameUnlockDto;
import com.englishapp.repository.DailyStudyTimeRepository;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.LessonStudySessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GameAccessServiceImpl implements GameAccessService {
    static final int REQUIRED_SECONDS = 300;
    private static final int DEFAULT_USER_ID = 1;
    private final ParentPasswordService passwords;
    private final StudyTimeEventRepository events;
    private final DailyStudyTimeRepository repository;
    private final LessonStudySessionRepository sessions;
    private final LessonRepository lessons;

    public GameAccessServiceImpl(DailyStudyTimeRepository repository,
                                 LessonStudySessionRepository sessions, LessonRepository lessons,
                                 ParentPasswordService passwords, StudyTimeEventRepository events) {
        this.repository = repository;
        this.sessions = sessions;
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
        Integer uid = resolveUser(userId);
        LocalDate today = LocalDate.now();
        String id = eventId == null ? java.util.UUID.randomUUID().toString() : eventId;
        StudyTimeEvent existing = events.findById(id).orElse(null);
        if (existing != null) {
            if (!existing.getUserId().equals(uid) || !existing.getLessonId().equals(lessonId)
                    || existing.getSeconds() != seconds) throw new IllegalArgumentException("计时事件与原请求不一致");
            return getTodayAccess(uid);
        }
        StudyTimeEvent event = new StudyTimeEvent();
        event.setId(id);
        event.setUserId(uid);
        event.setLessonId(lessonId);
        event.setStudyDate(today);
        event.setSeconds(seconds);
        events.save(event);
        LessonStudySession session = sessions.findByUserIdAndLessonIdAndStudyDate(uid, lessonId, today)
                .orElseGet(() -> {
                    LessonStudySession created = new LessonStudySession();
                    created.setUserId(uid);
                    created.setLessonId(lessonId);
                    created.setStudyDate(today);
                    created.setPendingSeconds(0);
                    created.setPassed(false);
                    return created;
                });
        if (Boolean.TRUE.equals(session.getPassed())) return recordStudyTime(uid, seconds);
        session.setPendingSeconds(Math.min(REQUIRED_SECONDS, session.getPendingSeconds() + seconds));
        sessions.save(session);
        return getTodayAccess(uid);
    }

    @Override
    public GameAccessDto getTodayAccess(Integer userId) {
        DailyStudyTime record = repository.findByUserIdAndStudyDate(resolveUser(userId), LocalDate.now()).orElse(null);
        return toAccess(record == null ? 0 : record.getSeconds(), record != null && Boolean.TRUE.equals(record.getParentUnlocked()));
    }

    @Override
    @Transactional
    public GameAccessDto recordStudyTime(Integer userId, int seconds) {
        Integer uid = resolveUser(userId);
        LocalDate today = LocalDate.now();
        DailyStudyTime record = repository.findByUserIdAndStudyDate(uid, today)
                .orElseGet(() -> newRecord(uid, today));
        record.setSeconds(Math.min(REQUIRED_SECONDS, record.getSeconds() + seconds));
        record.setUpdatedAt(LocalDateTime.now());
        repository.save(record);
        return toAccess(record.getSeconds(), Boolean.TRUE.equals(record.getParentUnlocked()));
    }

    @Override
    @Transactional
    public GameUnlockDto unlockWithPassword(Integer userId, String password) {
        if (!passwords.verify(password)) {
            return new GameUnlockDto(false, "密码未设置、错误或暂时锁定，请家长前往家长中心检查", getTodayAccess(userId));
        }
        Integer uid = resolveUser(userId);
        DailyStudyTime record = repository.findByUserIdAndStudyDate(uid, LocalDate.now())
                .orElseGet(() -> newRecord(uid, LocalDate.now()));
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

    private Integer resolveUser(Integer userId) {
        return userId == null ? DEFAULT_USER_ID : userId;
    }
}
