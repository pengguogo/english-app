package com.englishapp.service;

import com.englishapp.domain.DailyStudyTime;
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
    private static final String PARENT_PASSWORD = "000000";
    private final DailyStudyTimeRepository repository;
    private final LessonStudySessionRepository sessions;
    private final LessonRepository lessons;

    public GameAccessServiceImpl(DailyStudyTimeRepository repository,
                                 LessonStudySessionRepository sessions, LessonRepository lessons) {
        this.repository = repository;
        this.sessions = sessions;
        this.lessons = lessons;
    }

    @Override
    @Transactional
    public GameAccessDto recordPendingStudyTime(Integer userId, Integer lessonId, int seconds) {
        if (lessonId == null || !lessons.existsById(lessonId)) throw new IllegalArgumentException("课时不存在");
        Integer uid = resolveUser(userId);
        LocalDate today = LocalDate.now();
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
        session.setPendingSeconds(Math.min(REQUIRED_SECONDS, session.getPendingSeconds() + seconds));
        sessions.save(session);
        return getTodayAccess(uid);
    }

    @Override
    public GameAccessDto getTodayAccess(Integer userId) {
        int seconds = repository.findByUserIdAndStudyDate(resolveUser(userId), LocalDate.now())
                .map(DailyStudyTime::getSeconds)
                .orElse(0);
        return toAccess(seconds);
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
        return toAccess(record.getSeconds());
    }

    @Override
    @Transactional
    public GameUnlockDto unlockWithPassword(Integer userId, String password) {
        if (!PARENT_PASSWORD.equals(password)) {
            return new GameUnlockDto(false, "密码不正确", getTodayAccess(userId));
        }
        GameAccessDto access = recordStudyTime(userId, REQUIRED_SECONDS);
        return new GameUnlockDto(true, "今日游戏已解锁", access);
    }

    private DailyStudyTime newRecord(Integer userId, LocalDate date) {
        DailyStudyTime record = new DailyStudyTime();
        record.setUserId(userId);
        record.setStudyDate(date);
        record.setSeconds(0);
        return record;
    }

    private GameAccessDto toAccess(int seconds) {
        int studied = Math.min(seconds, REQUIRED_SECONDS);
        return new GameAccessDto(studied, REQUIRED_SECONDS,
                Math.max(0, REQUIRED_SECONDS - studied), studied >= REQUIRED_SECONDS);
    }

    private Integer resolveUser(Integer userId) {
        return userId == null ? DEFAULT_USER_ID : userId;
    }
}
