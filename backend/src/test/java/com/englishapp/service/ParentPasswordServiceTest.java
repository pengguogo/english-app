package com.englishapp.service;

import com.englishapp.domain.ParentGameSetting;
import com.englishapp.repository.ParentGameSettingRepository;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParentPasswordServiceTest {
    @Test
    void should_保存摘要并替换密码_当_家长设置或修改() {
        var repository = mock(ParentGameSettingRepository.class);
        var setting = new ParentGameSetting();
        when(repository.findById(1)).thenReturn(Optional.of(setting));
        var service = new ParentPasswordService(repository);
        assertFalse(service.isConfigured());
        service.setPassword("246810");
        assertNotEquals("246810", setting.getPasswordHash());
        assertTrue(service.verify("246810"));
        service.setPassword("135790");
        assertFalse(service.verify("246810"));
        assertTrue(service.verify("135790"));
        when(repository.existsById(1)).thenReturn(true);
        assertTrue(service.isConfigured());
        assertThrows(IllegalArgumentException.class, () -> service.setPassword("abc"));
    }

    @Test
    void should_临时锁定且允许过期后重试_当_连续五次错误() {
        var repository = mock(ParentGameSettingRepository.class);
        var setting = new ParentGameSetting();
        when(repository.findById(1)).thenReturn(Optional.of(setting));
        var service = new ParentPasswordService(repository);
        service.setPassword("246810");
        for (int i = 0; i < 5; i++) assertFalse(service.verify("111111"));
        assertNotNull(setting.getLockedUntil());
        assertFalse(service.verify("246810"));
        setting.setLockedUntil(LocalDateTime.now().minusSeconds(1));
        assertTrue(service.verify("246810"));
        assertEquals(0, setting.getFailedAttempts());
        assertNull(setting.getLockedUntil());
    }

    @Test
    void should_拒绝默认密码_当_尚未设置() {
        var repository = mock(ParentGameSettingRepository.class);
        when(repository.findById(1)).thenReturn(Optional.empty());
        assertFalse(new ParentPasswordService(repository).verify("000000"));
        verify(repository, never()).save(any());
        String first = ParentPasswordHash.encode("246810");
        assertNotEquals(first, ParentPasswordHash.encode("246810"));
    }
}
