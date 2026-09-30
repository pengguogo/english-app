package com.englishapp.service;

import com.englishapp.domain.ChildPhoto;
import com.englishapp.repository.ChildPhotoRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChildPhotoServiceImplTest {
    @TempDir Path tempDir;

    @Test
    void should_落盘并可读取_当_上传有效照片() throws Exception {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        when(repository.save(any())).thenAnswer(call -> {
            ChildPhoto photo = call.getArgument(0);
            photo.setId(7);
            return photo;
        });
        when(repository.findByIdAndUserId(7, 1)).thenAnswer(call -> {
            ChildPhoto photo = new ChildPhoto();
            photo.setId(7);
            photo.setFileName(java.nio.file.Files.list(tempDir).findFirst().orElseThrow().getFileName().toString());
            return Optional.of(photo);
        });
        ChildPhotoServiceImpl service = new ChildPhotoServiceImpl(repository, tempDir.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "jpg", output);
        var file = new MockMultipartFile("image", "test.jpg", "image/jpeg", output.toByteArray());

        var saved = service.add(LocalDate.now(), "第一次骑车", file);
        assertEquals("/api/v1/child-growth/photos/7/image", saved.imageUrl());
        assertTrue(service.read(7).length > 0);
        service.delete(7);
        assertEquals(0, java.nio.file.Files.list(tempDir).count());
    }

    @Test
    void should_拒绝无效内容_当_伪装成照片() {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        ChildPhotoServiceImpl service = new ChildPhotoServiceImpl(repository, tempDir.toString());
        var file = new MockMultipartFile("image", "fake.jpg", "image/jpeg", "not an image".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.add(LocalDate.now(), "", file));
        verifyNoInteractions(repository);
    }

    @Test
    void should_返回照片列表_当_查询() {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        when(repository.findByUserIdOrderByTakenAtDescIdDesc(1)).thenReturn(List.of());
        assertTrue(new ChildPhotoServiceImpl(repository, tempDir.toString()).list().isEmpty());
    }

    @Test
    void should_修改日期和说明_当_照片已存在() {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        ChildPhoto photo = new ChildPhoto();
        photo.setId(2);
        when(repository.findByIdAndUserId(2, 1)).thenReturn(Optional.of(photo));
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
        var result = new ChildPhotoServiceImpl(repository, tempDir.toString())
                .update(2, LocalDate.of(2025, 1, 1), "  一岁生日  ");
        assertEquals(LocalDate.of(2025, 1, 1), result.takenAt());
        assertEquals("一岁生日", result.caption());
    }
}
