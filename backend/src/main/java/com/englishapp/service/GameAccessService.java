package com.englishapp.service;

import com.englishapp.dto.GameAccessDto;

public interface GameAccessService {
    GameAccessDto getTodayAccess(Integer userId);
    GameAccessDto recordStudyTime(Integer userId, int seconds);
}
