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
    List<ChildPhotoResponseDTO> list(Integer childId);

    List<String> listCategories(Integer childId);

    ChildPhotoResponseDTO add(Integer childId, LocalDate takenAt, String caption, String category, MultipartFile image);

    ChildPhotoResponseDTO update(Integer childId, Integer id, LocalDate takenAt, String caption, String category);

    ChildPhoto find(Integer childId, Integer id);

    byte[] read(Integer childId, Integer id);

    byte[] readThumbnail(Integer childId, Integer id);

    List<ChildPhotoZipItem> prepareZip(Integer childId, List<Integer> ids);

    void writeZip(List<ChildPhotoZipItem> items, OutputStream output);

    void delete(Integer childId, Integer id);
}
