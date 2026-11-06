package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.*;
import com.englishapp.security.CurrentChild;
import com.englishapp.service.CharacterProgressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/characters")
public class CharacterProgressController {
    private final CharacterProgressService service;
    private final CurrentChild currentChild;
    public CharacterProgressController(CharacterProgressService service, CurrentChild currentChild) {
        this.service = service;
        this.currentChild = currentChild;
    }

    @GetMapping("/progress")
    public Result<List<CharacterProgressResponseDTO>> progress() { return Result.success(service.progress(currentChild.requireChildId())); }

    @GetMapping("/review")
    public Result<List<CharacterItemResponseDTO>> review() { return Result.success(service.review(currentChild.requireChildId())); }

    @PostMapping("/attempts")
    public Result<Void> record(@Valid @RequestBody CharacterAttemptRequestDTO request) {
        service.record(currentChild.requireChildId(), request);
        return Result.success(null);
    }
}
