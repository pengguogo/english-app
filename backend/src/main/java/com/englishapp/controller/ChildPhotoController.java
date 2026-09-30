package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.ChildPhotoResponseDTO;
import com.englishapp.dto.ChildPhotoRequestDTO;
import com.englishapp.service.ChildPhotoService;
import com.englishapp.service.ChildPhotoZipItem;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import jakarta.validation.Valid;

/**
 * 儿童成长照片接口:列表、分类、上传、缩略图、原图、打包下载、修改、删除。
 *
 * @author TRAE Agent
 * @since 2026-09-30
 */
@RestController
@RequestMapping("/api/v1/child-growth/photos")
public class ChildPhotoController {
    private final ChildPhotoService service;

    public ChildPhotoController(ChildPhotoService service) { this.service = service; }

    @GetMapping
    public Result<List<ChildPhotoResponseDTO>> list() { return Result.success(service.list()); }

    @GetMapping("/categories")
    public Result<List<String>> categories() { return Result.success(service.listCategories()); }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ChildPhotoResponseDTO> add(@RequestParam LocalDate takenAt,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false) String category,
            @RequestParam MultipartFile image) {
        return Result.success(service.add(takenAt, caption, category, image));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> image(@PathVariable Integer id) {
        var photo = service.find(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(photo.getContentType()))
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                .header("X-Content-Type-Options", "nosniff")
                .body(service.read(id));
    }

    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<byte[]> thumbnail(@PathVariable Integer id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=86400")
                .header("X-Content-Type-Options", "nosniff")
                .body(service.readThumbnail(id));
    }

    @GetMapping("/zip")
    public ResponseEntity<StreamingResponseBody> zip(@RequestParam(required = false) List<Integer> ids) {
        // 预检在响应流开始前完成,非法 id 走全局 404,而不是输出损坏的 zip
        List<ChildPhotoZipItem> items = service.prepareZip(ids);
        StreamingResponseBody body = output -> service.writeZip(items, output);
        String fileName = "child-photos-" + LocalDate.now() + ".zip";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(body);
    }

    @PutMapping("/{id}")
    public Result<ChildPhotoResponseDTO> update(@PathVariable Integer id,
            @Valid @RequestBody ChildPhotoRequestDTO request) {
        return Result.success(service.update(id, request.takenAt(), request.caption(), request.category()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return Result.success(null);
    }
}
