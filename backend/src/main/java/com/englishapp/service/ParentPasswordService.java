package com.englishapp.service;

import com.englishapp.domain.ParentGameSetting;
import com.englishapp.repository.ParentGameSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ParentPasswordService {
    private final ParentGameSettingRepository repository;
    public ParentPasswordService(ParentGameSettingRepository repository) { this.repository = repository; }

    public boolean isConfigured() { return repository.existsById(1); }

    @Transactional
    public void setPassword(String password) {
        if (password == null || !password.matches("[0-9]{6}")) throw new IllegalArgumentException("请输入6位数字密码");
        ParentGameSetting setting = repository.findById(1).orElseGet(ParentGameSetting::new);
        setting.setId(1);
        setting.setPasswordHash(ParentPasswordHash.encode(password));
        setting.setFailedAttempts(0);
        setting.setLockedUntil(null);
        repository.save(setting);
    }

    @Transactional
    public boolean verify(String password) {
        ParentGameSetting setting = repository.findById(1).orElse(null);
        if (setting == null) return false;
        LocalDateTime now = LocalDateTime.now();
        if (setting.getLockedUntil() != null && now.isBefore(setting.getLockedUntil())) return false;
        boolean correct = ParentPasswordHash.matches(password, setting.getPasswordHash());
        int failures = correct || setting.getLockedUntil() != null ? 0 : setting.getFailedAttempts();
        setting.setFailedAttempts(correct ? 0 : failures + 1);
        setting.setLockedUntil(setting.getFailedAttempts() >= 5 ? now.plusMinutes(5) : null);
        repository.save(setting);
        return correct;
    }
}
