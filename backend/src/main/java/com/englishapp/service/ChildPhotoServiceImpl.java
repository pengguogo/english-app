package com.englishapp.service;

import com.englishapp.domain.ChildPhoto;
import com.englishapp.dto.ChildPhotoResponseDTO;
import com.englishapp.repository.ChildPhotoRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ChildPhotoServiceImpl implements ChildPhotoService {
    private static final int USER_ID = 1;
    private static final int MAX_BYTES = 8 * 1024 * 1024;
    private final ChildPhotoRepository repository;
    private final Path directory;

    public ChildPhotoServiceImpl(ChildPhotoRepository repository,
            @Value("${app.child-photo.dir:child-photos}") String directory) {
        this.repository = repository;
        this.directory = Path.of(directory).toAbsolutePath().normalize();
    }

    @Override
    public List<ChildPhotoResponseDTO> list() {
        return repository.findByUserIdOrderByTakenAtDescIdDesc(USER_ID).stream().map(this::toDto).toList();
    }

    @Override
    public ChildPhotoResponseDTO add(LocalDate takenAt, String caption, MultipartFile image) {
        validateDetails(takenAt, caption);
        if (image == null || image.isEmpty() || image.getSize() > MAX_BYTES)
            throw new IllegalArgumentException("请选择不超过 8 MB 的照片");
        String type = image.getContentType();
        String suffix = switch (type == null ? "" : type) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            default -> throw new IllegalArgumentException("仅支持 JPG 或 PNG 照片");
        };
        byte[] bytes;
        try {
            BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(image.getBytes()));
            if (decoded == null)
                throw new IllegalArgumentException("照片文件无效");
            if ((long) decoded.getWidth() * decoded.getHeight() > 20_000_000L)
                throw new IllegalArgumentException("照片像素过大");
            BufferedImage clean = new BufferedImage(decoded.getWidth(), decoded.getHeight(),
                    suffix.equals(".jpg") ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
            var graphics = clean.createGraphics();
            try { graphics.drawImage(decoded, 0, 0, null); }
            finally { graphics.dispose(); }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (!ImageIO.write(clean, suffix.substring(1), output))
                throw new IllegalArgumentException("照片格式无效");
            bytes = output.toByteArray();
            Files.createDirectories(directory);
        } catch (IOException ex) {
            throw new IllegalStateException("照片保存失败", ex);
        }
        String fileName = UUID.randomUUID() + suffix;
        Path path = directory.resolve(fileName);
        try {
            Files.write(path, bytes, StandardOpenOption.CREATE_NEW);
            ChildPhoto photo = new ChildPhoto();
            photo.setUserId(USER_ID);
            photo.setTakenAt(takenAt);
            photo.setCaption(caption == null ? null : caption.trim());
            photo.setFileName(fileName);
            photo.setContentType(type);
            photo.setCreatedAt(LocalDateTime.now());
            try { return toDto(repository.save(photo)); }
            catch (RuntimeException ex) { Files.deleteIfExists(path); throw ex; }
        } catch (IOException ex) {
            throw new IllegalStateException("照片保存失败", ex);
        }
    }

    @Override
    public ChildPhotoResponseDTO update(Integer id, LocalDate takenAt, String caption) {
        validateDetails(takenAt, caption);
        ChildPhoto photo = find(id);
        photo.setTakenAt(takenAt);
        photo.setCaption(caption == null ? null : caption.trim());
        return toDto(repository.save(photo));
    }

    @Override
    public ChildPhoto find(Integer id) {
        return repository.findByIdAndUserId(id, USER_ID)
                .orElseThrow(() -> new NoSuchElementException("照片不存在"));
    }

    @Override
    public byte[] read(Integer id) {
        try { return Files.readAllBytes(directory.resolve(find(id).getFileName())); }
        catch (IOException ex) { throw new NoSuchElementException("照片文件不存在"); }
    }

    @Override
    public void delete(Integer id) {
        ChildPhoto photo = find(id);
        repository.delete(photo);
        try { Files.deleteIfExists(directory.resolve(photo.getFileName())); }
        catch (IOException ex) { throw new IllegalStateException("照片文件删除失败", ex); }
    }

    private ChildPhotoResponseDTO toDto(ChildPhoto photo) {
        return new ChildPhotoResponseDTO(photo.getId(), photo.getTakenAt(), photo.getCaption(),
                "/api/v1/child-growth/photos/" + photo.getId() + "/image");
    }

    private void validateDetails(LocalDate takenAt, String caption) {
        if (takenAt == null || takenAt.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("照片日期不能晚于今天");
        if (caption != null && caption.length() > 200)
            throw new IllegalArgumentException("说明不能超过 200 字");
    }
}
