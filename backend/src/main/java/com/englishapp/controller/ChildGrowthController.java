package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.*;
import com.englishapp.service.ChildGrowthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/child-growth")
public class ChildGrowthController {
    private final ChildGrowthService service;

    public ChildGrowthController(ChildGrowthService service) { this.service = service; }

    @GetMapping("/profile")
    public Result<ChildProfileResponseDTO> getProfile() { return Result.success(service.getProfile()); }

    @PutMapping("/profile")
    public Result<ChildProfileResponseDTO> updateProfile(@Valid @RequestBody ChildProfileRequestDTO request) {
        return Result.success(service.updateProfile(request));
    }

    @GetMapping("/measurements")
    public Result<List<GrowthMeasurementResponseDTO>> list() { return Result.success(service.listMeasurements()); }

    @PostMapping("/measurements")
    public Result<GrowthMeasurementResponseDTO> create(@Valid @RequestBody GrowthMeasurementRequestDTO request) {
        return Result.success(service.createMeasurement(request));
    }

    @PutMapping("/measurements/{id}")
    public Result<GrowthMeasurementResponseDTO> update(@PathVariable Integer id,
            @Valid @RequestBody GrowthMeasurementRequestDTO request) {
        return Result.success(service.updateMeasurement(id, request));
    }

    @DeleteMapping("/measurements/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        service.deleteMeasurement(id);
        return Result.success(null);
    }
}
