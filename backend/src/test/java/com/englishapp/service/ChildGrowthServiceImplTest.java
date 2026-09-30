package com.englishapp.service;

import com.englishapp.domain.AppUser;
import com.englishapp.domain.GrowthMeasurement;
import com.englishapp.dto.ChildProfileRequestDTO;
import com.englishapp.dto.GrowthMeasurementRequestDTO;
import com.englishapp.repository.AppUserRepository;
import com.englishapp.repository.GrowthMeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChildGrowthServiceImplTest {
    @Mock AppUserRepository users;
    @Mock GrowthMeasurementRepository measurements;
    ChildGrowthServiceImpl service;

    @BeforeEach
    void setup() { service = new ChildGrowthServiceImpl(users, measurements); }

    @Test
    void should_返回孩子资料_当_用户存在() {
        AppUser user = new AppUser();
        user.setNickname("小朋友");
        when(users.findById(1)).thenReturn(Optional.of(user));
        assertEquals("小朋友", service.getProfile().nickname());
    }

    @Test
    void should_保存生日而不存年龄_当_更新资料() {
        AppUser user = new AppUser();
        when(users.findById(1)).thenReturn(Optional.of(user));
        when(users.save(any())).thenAnswer(call -> call.getArgument(0));
        var response = service.updateProfile(new ChildProfileRequestDTO(" 小明 ", LocalDate.of(2020, 1, 1), "MALE"));
        assertEquals("小明", response.nickname());
        assertEquals(LocalDate.of(2020, 1, 1), response.birthDate());
    }

    @Test
    void should_拒绝未来生日_当_更新资料() {
        assertThrows(IllegalArgumentException.class, () -> service.updateProfile(
                new ChildProfileRequestDTO("孩子", LocalDate.now().plusDays(1), null)));
        verifyNoInteractions(users);
    }

    @Test
    void should_按日期返回记录_当_查询列表() {
        GrowthMeasurement item = new GrowthMeasurement();
        item.setMeasuredAt(LocalDate.of(2026, 1, 1));
        when(measurements.findByUserIdOrderByMeasuredAtAscIdAsc(1)).thenReturn(List.of(item));
        assertEquals(1, service.listMeasurements().size());
    }

    @Test
    void should_允许只记录身高_当_新增测量() {
        when(measurements.save(any())).thenAnswer(call -> call.getArgument(0));
        var result = service.createMeasurement(new GrowthMeasurementRequestDTO(
                LocalDate.now(), new BigDecimal("120.5"), null, null));
        assertEquals(new BigDecimal("120.5"), result.heightCm());
        assertNull(result.weightKg());
    }

    @Test
    void should_拒绝空测量_当_新增测量() {
        assertThrows(IllegalArgumentException.class, () -> service.createMeasurement(
                new GrowthMeasurementRequestDTO(LocalDate.now(), null, null, null)));
        verifyNoInteractions(measurements);
    }

    @Test
    void should_更新已有记录_当_修改测量() {
        GrowthMeasurement item = new GrowthMeasurement();
        when(measurements.findByIdAndUserId(3, 1)).thenReturn(Optional.of(item));
        when(measurements.save(any())).thenAnswer(call -> call.getArgument(0));
        var result = service.updateMeasurement(3, new GrowthMeasurementRequestDTO(
                LocalDate.now(), null, new BigDecimal("22.40"), "学校体检"));
        assertEquals("学校体检", result.note());
    }

    @Test
    void should_拒绝其他记录_当_删除测量() {
        when(measurements.findByIdAndUserId(9, 1)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.deleteMeasurement(9));
        verify(measurements, never()).delete(any());
    }
}
