package com.englishapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey
    val id: Int,
    val unitId: Int,
    val name: String,
    val type: String,
    val content: String,
    val sortOrder: Int,
    val starReward: Int
)
