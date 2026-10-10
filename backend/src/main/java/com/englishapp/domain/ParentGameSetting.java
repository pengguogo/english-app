package com.englishapp.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "parent_game_setting")
public class ParentGameSetting {
    @Id
    private Integer id;
    private String passwordHash;
    private Integer failedAttempts;
    private java.time.LocalDateTime lockedUntil;
    public Integer getId() { return id; }
    public void setId(Integer value) { id = value; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String value) { passwordHash = value; }
    public Integer getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(Integer value) { failedAttempts = value; }
    public java.time.LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(java.time.LocalDateTime value) { lockedUntil = value; }
}
