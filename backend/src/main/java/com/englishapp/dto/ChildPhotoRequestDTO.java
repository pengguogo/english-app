package com.englishapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ChildPhotoRequestDTO(@NotNull @PastOrPresent LocalDate takenAt,
        @Size(max = 200) String caption) { }
