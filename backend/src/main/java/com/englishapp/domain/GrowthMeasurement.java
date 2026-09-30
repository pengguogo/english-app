package com.englishapp.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "growth_measurement")
public class GrowthMeasurement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private LocalDate measuredAt;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String note;
    private LocalDateTime createdAt;

    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public LocalDate getMeasuredAt() { return measuredAt; }
    public void setMeasuredAt(LocalDate measuredAt) { this.measuredAt = measuredAt; }
    public BigDecimal getHeightCm() { return heightCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
