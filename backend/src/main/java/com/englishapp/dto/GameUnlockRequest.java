package com.englishapp.dto;

import jakarta.validation.constraints.NotBlank;

public class GameUnlockRequest {
    @NotBlank
    private String password;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
