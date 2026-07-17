package com.englishapp.repository

import com.englishapp.data.local.ThemeDao
import com.englishapp.data.model.Theme
import com.englishapp.data.remote.RetrofitClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepository @Inject constructor(
    private val themeDao: ThemeDao
) {
    suspend fun getThemes(): List<Theme> {
        return try {
            val themes = RetrofitClient.apiService.getThemes()
            themeDao.insertAll(themes)
            themes
        } catch (e: Exception) {
            themeDao.getAll()
        }
    }
}
