package com.englishapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.englishapp.data.model.Theme

@Dao
interface ThemeDao {
    @Query("SELECT * FROM themes ORDER BY sortOrder ASC")
    suspend fun getAll(): List<Theme>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(themes: List<Theme>)
}
