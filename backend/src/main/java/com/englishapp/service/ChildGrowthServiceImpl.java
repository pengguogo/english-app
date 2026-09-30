package com.englishapp.service;

import com.englishapp.domain.AppUser;
import com.englishapp.domain.GrowthMeasurement;
import com.englishapp.dto.*;
import com.englishapp.repository.AppUserRepository;
import com.englishapp.repository.GrowthMeasurementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChildGrowthServiceImpl implements ChildGrowthService {
    private static final int USER_ID = 1;
    private final AppUserRepository userRepository;
    private final GrowthMeasurementRepository measurementRepository;

    public ChildGrowthServiceImpl(AppUserRepository userRepository,
            GrowthMeasurementRepository measurementRepository) {
        this.userRepository = userRepository;
        this.measurementRepository = measurementRepository;
    }

    @Override
    public ChildProfileResponseDTO getProfile() {
        return toProfile(userRepository.findById(USER_ID).orElseThrow());
    }

    @Override @Transactional
    public ChildProfileResponseDTO updateProfile(ChildProfileRequestDTO request) {
        if (request.birthDate() != null && request.birthDate().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("生日不能晚于今天");
        if (request.nickname() == null || request.nickname().isBlank())
            throw new IllegalArgumentException("请填写昵称");
        if (request.sex() != null && !List.of("MALE", "FEMALE").contains(request.sex()))
            throw new IllegalArgumentException("性别选项无效");
        AppUser user = userRepository.findById(USER_ID).orElseThrow();
        user.setNickname(request.nickname().trim());
        user.setBirthDate(request.birthDate());
        user.setSex(request.sex());
        return toProfile(userRepository.save(user));
    }

    @Override
    public List<GrowthMeasurementResponseDTO> listMeasurements() {
        return measurementRepository.findByUserIdOrderByMeasuredAtAscIdAsc(USER_ID)
                .stream().map(this::toMeasurement).toList();
    }

    @Override @Transactional
    public GrowthMeasurementResponseDTO createMeasurement(GrowthMeasurementRequestDTO request) {
        validate(request);
        GrowthMeasurement item = new GrowthMeasurement();
        item.setUserId(USER_ID);
        item.setCreatedAt(java.time.LocalDateTime.now());
        apply(item, request);
        return toMeasurement(measurementRepository.save(item));
    }

    @Override @Transactional
    public GrowthMeasurementResponseDTO updateMeasurement(Integer id, GrowthMeasurementRequestDTO request) {
        validate(request);
        GrowthMeasurement item = measurementRepository.findByIdAndUserId(id, USER_ID)
                .orElseThrow(() -> new NoSuchElementException("测量记录不存在"));
        apply(item, request);
        return toMeasurement(measurementRepository.save(item));
    }

    @Override @Transactional
    public void deleteMeasurement(Integer id) {
        GrowthMeasurement item = measurementRepository.findByIdAndUserId(id, USER_ID)
                .orElseThrow(() -> new NoSuchElementException("测量记录不存在"));
        measurementRepository.delete(item);
    }

    private void validate(GrowthMeasurementRequestDTO request) {
        if (request.measuredAt() == null || request.measuredAt().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("测量日期不能晚于今天");
        if (request.heightCm() == null && request.weightKg() == null)
            throw new IllegalArgumentException("身高和体重至少填写一项");
        if (request.heightCm() != null && (request.heightCm().scale() > 1 ||
                request.heightCm().compareTo(new java.math.BigDecimal("20")) < 0 ||
                request.heightCm().compareTo(new java.math.BigDecimal("250")) > 0))
            throw new IllegalArgumentException("身高应为 20～250 cm，最多一位小数");
        if (request.weightKg() != null && (request.weightKg().scale() > 2 ||
                request.weightKg().compareTo(new java.math.BigDecimal("0.5")) < 0 ||
                request.weightKg().compareTo(new java.math.BigDecimal("300")) > 0))
            throw new IllegalArgumentException("体重应为 0.5～300 kg，最多两位小数");
    }

    private void apply(GrowthMeasurement item, GrowthMeasurementRequestDTO request) {
        item.setMeasuredAt(request.measuredAt());
        item.setHeightCm(request.heightCm());
        item.setWeightKg(request.weightKg());
        item.setNote(request.note() == null ? null : request.note().trim());
    }

    private ChildProfileResponseDTO toProfile(AppUser user) {
        return new ChildProfileResponseDTO(user.getNickname(), user.getBirthDate(), user.getSex());
    }

    private GrowthMeasurementResponseDTO toMeasurement(GrowthMeasurement item) {
        return new GrowthMeasurementResponseDTO(item.getId(), item.getMeasuredAt(),
                item.getHeightCm(), item.getWeightKg(), item.getNote());
    }
}
