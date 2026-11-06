package com.englishapp.service;

import com.englishapp.dto.*;
import java.util.List;

public interface ChildGrowthService {
    ChildProfileResponseDTO getProfile(Integer childId);
    ChildProfileResponseDTO updateProfile(Integer childId, ChildProfileRequestDTO request);
    List<GrowthMeasurementResponseDTO> listMeasurements(Integer childId);
    GrowthMeasurementResponseDTO createMeasurement(Integer childId, GrowthMeasurementRequestDTO request);
    GrowthMeasurementResponseDTO updateMeasurement(Integer childId, Integer id, GrowthMeasurementRequestDTO request);
    void deleteMeasurement(Integer childId, Integer id);
}
