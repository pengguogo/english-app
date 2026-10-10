package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.*;
import com.englishapp.service.CharacterProgressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/characters")
public class CharacterProgressController {
    private final CharacterProgressService service;
    public CharacterProgressController(CharacterProgressService service) { this.service = service; }

    @GetMapping("/progress")
    public Result<List<CharacterProgressResponseDTO>> progress() { return Result.success(service.progress()); }

    @GetMapping("/review")
    public Result<List<CharacterItemResponseDTO>> review() { return Result.success(service.review()); }

    @PostMapping("/attempts")
    public Result<Void> record(@Valid @RequestBody CharacterAttemptRequestDTO request) {
        service.record(request);
        return Result.success(null);
    }
}
