package com.englishapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * 单元模型(带进度信息)。
 * 对应后端 UnitDto,包含课程总数与已完成课程数。
 * @author englishapp
 * @since 1.0.0
 */
data class Unit(
    val id: Int,
    val name: String,
    val sortOrder: Int,
    @SerializedName("isLocked")
    val isLocked: Boolean,
    val totalLessons: Int,
    val completedLessons: Int
)
