package com.englishapp.service;

import com.englishapp.domain.ChildPhoto;
import com.englishapp.dto.ChildPhotoResponseDTO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ChildPhotoService {
    List<ChildPhotoResponseDTO> list();
    ChildPhotoResponseDTO add(LocalDate takenAt, String caption, MultipartFile image);
    ChildPhotoResponseDTO update(Integer id, LocalDate takenAt, String caption);
    ChildPhoto find(Integer id);
    byte[] read(Integer id);
    void delete(Integer id);
}
