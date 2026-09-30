package com.englishapp.service;

import com.englishapp.dto.*;
import java.util.List;

public interface ChildGrowthService {
    ChildProfileResponseDTO getProfile();
    ChildProfileResponseDTO updateProfile(ChildProfileRequestDTO request);
    List<GrowthMeasurementResponseDTO> listMeasurements();
    GrowthMeasurementResponseDTO createMeasurement(GrowthMeasurementRequestDTO request);
    GrowthMeasurementResponseDTO updateMeasurement(Integer id, GrowthMeasurementRequestDTO request);
    void deleteMeasurement(Integer id);
}
