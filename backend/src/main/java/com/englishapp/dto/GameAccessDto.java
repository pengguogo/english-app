package com.englishapp.dto;

public record GameAccessDto(
        int studiedSeconds,
        int requiredSeconds,
        int remainingSeconds,
        boolean unlocked) {
}
