package com.englishapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * 主题模型。
 * 对应后端 ThemeDto,同时作为 Room 实体用于本地缓存。
 * @author englishapp
 * @since 1.0.0
 */
@Entity(tableName = "themes")
data class Theme(
    @PrimaryKey
    val id: Int,
    val name: String,
    val iconUrl: String?,
    val sortOrder: Int,
    @SerializedName("isLocked")
    val isLocked: Boolean
)
