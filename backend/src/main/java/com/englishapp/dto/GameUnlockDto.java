package com.englishapp.dto;

public record GameUnlockDto(boolean success, String message, GameAccessDto access) {
}
