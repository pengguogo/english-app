package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.*;
import com.englishapp.security.CurrentChild;
import com.englishapp.service.ChildGrowthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/child-growth")
public class ChildGrowthController {
    private final ChildGrowthService service;
    private final CurrentChild currentChild;

    public ChildGrowthController(ChildGrowthService service, CurrentChild currentChild) {
        this.service = service;
        this.currentChild = currentChild;
    }

    /** 家长区:先校验家长二次验证,再取当前孩子 ID */
    private Integer parentChildId() {
        currentChild.requireParentVerified();
        return currentChild.requireChildId();
    }

    @GetMapping("/profile")
    public Result<ChildProfileResponseDTO> getProfile() { return Result.success(service.getProfile(parentChildId())); }

    @PutMapping("/profile")
    public Result<ChildProfileResponseDTO> updateProfile(@Valid @RequestBody ChildProfileRequestDTO request) {
        return Result.success(service.updateProfile(parentChildId(), request));
    }

    @GetMapping("/measurements")
    public Result<List<GrowthMeasurementResponseDTO>> list() { return Result.success(service.listMeasurements(parentChildId())); }

    @PostMapping("/measurements")
    public Result<GrowthMeasurementResponseDTO> create(@Valid @RequestBody GrowthMeasurementRequestDTO request) {
        return Result.success(service.createMeasurement(parentChildId(), request));
    }

    @PutMapping("/measurements/{id}")
    public Result<GrowthMeasurementResponseDTO> update(@PathVariable Integer id,
            @Valid @RequestBody GrowthMeasurementRequestDTO request) {
        return Result.success(service.updateMeasurement(parentChildId(), id, request));
    }

    @DeleteMapping("/measurements/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        service.deleteMeasurement(parentChildId(), id);
        return Result.success(null);
    }
}
