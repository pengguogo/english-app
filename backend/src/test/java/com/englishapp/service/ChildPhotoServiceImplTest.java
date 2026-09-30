package com.englishapp.service;

import com.englishapp.domain.ChildPhoto;
import com.englishapp.repository.ChildPhotoRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChildPhotoServiceImplTest {
    @TempDir Path tempDir;

    /** 构造指向临时目录的被测服务,仓储为 mock */
    private ChildPhotoServiceImpl newService(ChildPhotoRepository repository) {
        return new ChildPhotoServiceImpl(repository, new ChildPhotoImageStore(tempDir.toString()));
    }

    /** mock 仓储:保存回填 id,按 id 查询时从磁盘扫描真实文件名(排除缩略图目录) */
    private void mockSaveAndFind(ChildPhotoRepository repository, int id) {
        when(repository.save(any())).thenAnswer(call -> {
            ChildPhoto photo = call.getArgument(0);
            photo.setId(id);
            return photo;
        });
        when(repository.findByIdAndUserId(id, 1)).thenAnswer(call -> {
            ChildPhoto photo = new ChildPhoto();
            photo.setId(id);
            photo.setTakenAt(LocalDate.now());
            String name = Files.list(tempDir)
                    .filter(p -> p.getFileName().toString().endsWith(".jpg"))
                    .filter(p -> Files.isRegularFile(p))
                    .findFirst().orElseThrow().getFileName().toString();
            photo.setFileName(name);
            return Optional.of(photo);
        });
    }

    @Test
    void should_落盘并可读取_当_上传有效照片() throws Exception {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        mockSaveAndFind(repository, 7);
        ChildPhotoServiceImpl service = newService(repository);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "jpg", output);
        var file = new MockMultipartFile("image", "test.jpg", "image/jpeg", output.toByteArray());

        var saved = service.add(LocalDate.now(), "第一次骑车", "成长", file);
        assertEquals("/api/v1/child-growth/photos/7/image", saved.imageUrl());
        assertEquals("/api/v1/child-growth/photos/7/thumbnail", saved.thumbnailUrl());
        assertEquals("成长", saved.category());
        assertTrue(service.read(7).length > 0);
        service.delete(7);
        assertEquals(0, Files.list(tempDir).count());
    }

    @Test
    void should_拒绝无效内容_当_伪装成照片() {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        ChildPhotoServiceImpl service = newService(repository);
        var file = new MockMultipartFile("image", "fake.jpg", "image/jpeg", "not an image".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.add(LocalDate.now(), "", null, file));
        verifyNoInteractions(repository);
    }

    @Test
    void should_返回照片列表_当_查询() {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        when(repository.findByUserIdOrderByTakenAtDescIdDesc(1)).thenReturn(List.of());
        assertTrue(newService(repository).list().isEmpty());
    }

    @Test
    void should_修改日期说明和分类_当_照片已存在() {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        ChildPhoto photo = new ChildPhoto();
        photo.setId(2);
        when(repository.findByIdAndUserId(2, 1)).thenReturn(Optional.of(photo));
        when(repository.save(any())).thenAnswer(call -> call.getArgument(0));
        var result = newService(repository)
                .update(2, LocalDate.of(2025, 1, 1), "  一岁生日  ", "  家庭  ");
        assertEquals(LocalDate.of(2025, 1, 1), result.takenAt());
        assertEquals("一岁生日", result.caption());
        assertEquals("家庭", result.category());
    }

    @Test
    void should_拒绝超长分类_当_更新照片() {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        assertThrows(IllegalArgumentException.class, () -> newService(repository)
                .update(2, LocalDate.now(), null, "很".repeat(51)));
        verifyNoInteractions(repository);
    }

    @Test
    void should_返回去重分类列表_当_查询分类() {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        when(repository.findDistinctCategories(1)).thenReturn(List.of("家庭", "生日"));
        assertEquals(List.of("家庭", "生日"), newService(repository).listCategories());
    }

    @Test
    void should_生成并缓存缩略图_当_原图长边超过480() throws Exception {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        mockSaveAndFind(repository, 7);
        ChildPhotoServiceImpl service = newService(repository);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB), "jpg", output);
        var file = new MockMultipartFile("image", "big.jpg", "image/jpeg", output.toByteArray());
        service.add(LocalDate.now(), null, null, file);

        byte[] thumb = service.readThumbnail(7);
        BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(thumb));
        assertEquals(480, decoded.getWidth());
        assertEquals(320, decoded.getHeight());
        // 第二次读取应命中磁盘缓存:thumbs 目录下恰好一个缓存文件
        service.readThumbnail(7);
        try (var cached = Files.list(tempDir.resolve("thumbs"))) {
            assertEquals(1, cached.count());
        }
    }

    @Test
    void should_降级返回原图_当_缩略图生成失败() throws Exception {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        ChildPhoto photo = new ChildPhoto();
        photo.setId(3);
        photo.setFileName("missing.jpg");
        when(repository.findByIdAndUserId(3, 1)).thenReturn(Optional.of(photo));
        // 文件不存在时,readThumbnail 降级为 read,最终抛 404 对应的 NoSuchElementException
        assertThrows(java.util.NoSuchElementException.class, () -> newService(repository).readThumbnail(3));
    }

    @Test
    void should_打包照片为zip_当_指定id列表() throws Exception {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        ChildPhotoServiceImpl service = newService(repository);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), "jpg", output);
        byte[] content = output.toByteArray();
        Files.write(tempDir.resolve("photo-1.jpg"), content);
        ChildPhoto photo = new ChildPhoto();
        photo.setId(9);
        photo.setUserId(1);
        photo.setFileName("photo-1.jpg");
        photo.setTakenAt(LocalDate.of(2025, 6, 1));
        photo.setCaption("公园/游玩");
        when(repository.findByIdAndUserId(9, 1)).thenReturn(Optional.of(photo));

        var items = service.prepareZip(List.of(9));
        assertEquals(1, items.size());
        ByteArrayOutputStream zipOutput = new ByteArrayOutputStream();
        service.writeZip(items, zipOutput);

        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(zipOutput.toByteArray()))) {
            ZipEntry entry = zip.getNextEntry();
            assertNotNull(entry, "zip 应包含一个条目");
            assertEquals("2025-06-01_9_公园_游玩.jpg", entry.getName());
            assertArrayEquals(content, zip.readAllBytes());
            assertNull(zip.getNextEntry(), "zip 只应包含一个条目");
        }
    }

    @Test
    void should_拒绝不存在的照片_当_打包指定id() {
        ChildPhotoRepository repository = mock(ChildPhotoRepository.class);
        when(repository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());
        assertThrows(java.util.NoSuchElementException.class,
                () -> newService(repository).prepareZip(List.of(99)));
    }
}
