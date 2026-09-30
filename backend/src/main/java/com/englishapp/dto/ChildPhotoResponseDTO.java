package com.englishapp.dto;

import java.time.LocalDate;

public record ChildPhotoResponseDTO(Integer id, LocalDate takenAt, String caption, String imageUrl) { }
