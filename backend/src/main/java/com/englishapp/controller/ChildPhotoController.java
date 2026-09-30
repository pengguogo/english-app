package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.ChildPhotoResponseDTO;
import com.englishapp.dto.ChildPhotoRequestDTO;
import com.englishapp.service.ChildPhotoService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/child-growth/photos")
public class ChildPhotoController {
    private final ChildPhotoService service;

    public ChildPhotoController(ChildPhotoService service) { this.service = service; }

    @GetMapping
    public Result<List<ChildPhotoResponseDTO>> list() { return Result.success(service.list()); }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ChildPhotoResponseDTO> add(@RequestParam LocalDate takenAt,
            @RequestParam(required = false) String caption, @RequestParam MultipartFile image) {
        return Result.success(service.add(takenAt, caption, image));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> image(@PathVariable Integer id) {
        var photo = service.find(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(photo.getContentType()))
                .header("Cache-Control", "private, max-age=3600")
                .header("X-Content-Type-Options", "nosniff")
                .body(service.read(id));
    }

    @PutMapping("/{id}")
    public Result<ChildPhotoResponseDTO> update(@PathVariable Integer id,
            @Valid @RequestBody ChildPhotoRequestDTO request) {
        return Result.success(service.update(id, request.takenAt(), request.caption()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return Result.success(null);
    }
}
