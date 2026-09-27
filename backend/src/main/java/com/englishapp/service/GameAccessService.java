package com.englishapp.service;

import com.englishapp.dto.GameAccessDto;
import com.englishapp.dto.GameUnlockDto;

public interface GameAccessService {
    GameAccessDto getTodayAccess(Integer userId);
    GameAccessDto recordStudyTime(Integer userId, int seconds);
    GameUnlockDto unlockWithPassword(Integer userId, String password);
}
