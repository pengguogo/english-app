package com.englishapp.dto;

import java.time.LocalDate;

public record ChildProfileResponseDTO(String nickname, LocalDate birthDate, String sex) { }
