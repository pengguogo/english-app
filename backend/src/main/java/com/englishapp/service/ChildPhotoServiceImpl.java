package com.englishapp.service;

import com.englishapp.domain.ChildPhoto;
import com.englishapp.dto.ChildPhotoResponseDTO;
import com.englishapp.repository.ChildPhotoRepository;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 儿童成长照片业务实现:分类、增删改查、缩略图与打包下载。
 * 磁盘图片操作委托 {@link ChildPhotoImageStore},本类只负责校验与数据库逻辑。
 *
 * @author TRAE Agent
 * @since 2026-09-30
 */
@Service
public class ChildPhotoServiceImpl implements ChildPhotoService {
    private static final int USER_ID = 1;
    private static final String BASE_URL = "/api/v1/child-growth/photos/";
    private final ChildPhotoRepository repository;
    private final ChildPhotoImageStore store;

    public ChildPhotoServiceImpl(ChildPhotoRepository repository, ChildPhotoImageStore store) {
        this.repository = repository;
        this.store = store;
    }

    @Override
    public List<ChildPhotoResponseDTO> list() {
        return repository.findByUserIdOrderByTakenAtDescIdDesc(USER_ID).stream().map(this::toDto).toList();
    }

    @Override
    public List<String> listCategories() {
        return repository.findDistinctCategories(USER_ID);
    }

    @Override
    public ChildPhotoResponseDTO add(LocalDate takenAt, String caption, String category, MultipartFile image) {
        validateDetails(takenAt, caption, category);
        String suffix = store.extensionOf(image.getContentType());
        byte[] bytes = store.reencode(image, suffix);
        String fileName = UUID.randomUUID() + suffix;
        store.write(fileName, bytes);
        ChildPhoto photo = new ChildPhoto();
        photo.setUserId(USER_ID);
        photo.setTakenAt(takenAt);
        photo.setCaption(normalize(caption));
        photo.setCategory(normalize(category));
        photo.setFileName(fileName);
        photo.setContentType(image.getContentType());
        photo.setCreatedAt(LocalDateTime.now());
        try { return toDto(repository.save(photo)); }
        catch (RuntimeException ex) { store.rollback(fileName); throw ex; }
    }

    @Override
    public ChildPhotoResponseDTO update(Integer id, LocalDate takenAt, String caption, String category) {
        validateDetails(takenAt, caption, category);
        ChildPhoto photo = find(id);
        photo.setTakenAt(takenAt);
        photo.setCaption(normalize(caption));
        photo.setCategory(normalize(category));
        return toDto(repository.save(photo));
    }

    @Override
    public ChildPhoto find(Integer id) {
        return repository.findByIdAndUserId(id, USER_ID)
                .orElseThrow(() -> new NoSuchElementException("照片不存在"));
    }

    @Override
    public byte[] read(Integer id) {
        return store.read(find(id).getFileName());
    }

    @Override
    public byte[] readThumbnail(Integer id) {
        return store.readThumbnail(find(id));
    }

    @Override
    public List<ChildPhotoZipItem> prepareZip(List<Integer> ids) {
        List<ChildPhoto> photos = ids == null || ids.isEmpty()
                ? repository.findByUserIdOrderByTakenAtDescIdDesc(USER_ID)
                : ids.stream().map(this::find).toList();
        return photos.stream().map(photo -> new ChildPhotoZipItem(entryNameOf(photo), photo.getFileName())).toList();
    }

    @Override
    public void writeZip(List<ChildPhotoZipItem> items, OutputStream output) {
        try (ZipOutputStream zip = new ZipOutputStream(output)) {
            for (ChildPhotoZipItem item : items) {
                zip.putNextEntry(new ZipEntry(item.entryName()));
                zip.write(store.read(item.fileName()));
                zip.closeEntry();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("照片打包失败", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        ChildPhoto photo = find(id);
        repository.delete(photo);
        store.delete(photo.getFileName());
    }

    private ChildPhotoResponseDTO toDto(ChildPhoto photo) {
        return new ChildPhotoResponseDTO(photo.getId(), photo.getTakenAt(), photo.getCaption(),
                photo.getCategory(), BASE_URL + photo.getId() + "/image", BASE_URL + photo.getId() + "/thumbnail");
    }

    private void validateDetails(LocalDate takenAt, String caption, String category) {
        if (takenAt == null || takenAt.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("照片日期不能晚于今天");
        if (caption != null && caption.length() > 200)
            throw new IllegalArgumentException("说明不能超过 200 字");
        if (category != null && category.trim().length() > 50)
            throw new IllegalArgumentException("分类不能超过 50 字");
    }

    /** 空白文本统一归一为 null,避免数据库里出现空字符串分类 */
    private String normalize(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }

    /** zip 条目名:日期_id_清洗后的说明.后缀;含 id 保证唯一,非法文件名字符替换为下划线 */
    private String entryNameOf(ChildPhoto photo) {
        String safe = photo.getCaption() == null ? ""
                : photo.getCaption().replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
        if (safe.length() > 30) safe = safe.substring(0, 30);
        String base = photo.getTakenAt() + "_" + photo.getId();
        String name = safe.isEmpty() ? base : base + "_" + safe;
        return name + extensionOf(photo.getFileName());
    }

    private String extensionOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot);
    }
}
