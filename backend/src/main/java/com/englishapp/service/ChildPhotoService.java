package com.englishapp.service;

import com.englishapp.domain.ChildPhoto;
import com.englishapp.dto.ChildPhotoResponseDTO;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * 儿童成长照片业务接口。
 *
 * @author TRAE Agent
 * @since 2026-09-30
 */
public interface ChildPhotoService {
    List<ChildPhotoResponseDTO> list();

    List<String> listCategories();

    ChildPhotoResponseDTO add(LocalDate takenAt, String caption, String category, MultipartFile image);

    ChildPhotoResponseDTO update(Integer id, LocalDate takenAt, String caption, String category);

    ChildPhoto find(Integer id);

    byte[] read(Integer id);

    byte[] readThumbnail(Integer id);

    List<ChildPhotoZipItem> prepareZip(List<Integer> ids);

    void writeZip(List<ChildPhotoZipItem> items, OutputStream output);

    void delete(Integer id);
}
