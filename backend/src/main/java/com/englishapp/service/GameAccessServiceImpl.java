package com.englishapp.service;

import com.englishapp.domain.DailyStudyTime;
import com.englishapp.dto.GameAccessDto;
import com.englishapp.repository.DailyStudyTimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GameAccessServiceImpl implements GameAccessService {
    static final int REQUIRED_SECONDS = 300;
    private static final int DEFAULT_USER_ID = 1;
    private final DailyStudyTimeRepository repository;

    public GameAccessServiceImpl(DailyStudyTimeRepository repository) {
        this.repository = repository;
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
