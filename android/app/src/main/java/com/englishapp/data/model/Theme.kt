package com.englishapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "themes")
data class Theme(
    @PrimaryKey
    val id: Int,
    val name: String,
    val iconUrl: String?,
    val sortOrder: Int,
    val isLocked: Boolean
)
