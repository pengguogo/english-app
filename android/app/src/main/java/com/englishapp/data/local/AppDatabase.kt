package com.englishapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.englishapp.data.model.Lesson
import com.englishapp.data.model.Theme

@Database(entities = [Theme::class, Lesson::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun themeDao(): ThemeDao
}
