package com.englishapp.repository;

import com.englishapp.domain.ParentGameSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentGameSettingRepository extends JpaRepository<ParentGameSetting, Integer> {
}
