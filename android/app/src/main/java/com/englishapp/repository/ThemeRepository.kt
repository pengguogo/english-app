package com.englishapp.repository

import com.englishapp.data.local.ThemeDao
import com.englishapp.data.model.Theme
import com.englishapp.data.remote.ApiService
import com.englishapp.data.remote.ApiResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 主题仓储。
 * 优先从远程获取,失败时回退到本地 Room 缓存。
 * @author englishapp
 * @since 1.0.0
 */
@Singleton
class ThemeRepository @Inject constructor(
    private val apiService: ApiService,
    private val themeDao: ThemeDao
) {
    /**
     * 获取所有主题。
     * 远程成功后写入本地缓存;失败时返回本地缓存。
     * @return 主题列表
     */
    suspend fun getThemes(): List<Theme> {
        return try {
            val response: ApiResponse<List<Theme>> = apiService.getThemes()
            if (response.isSuccess() && response.data != null) {
                themeDao.insertAll(response.data)
                response.data
            } else {
                themeDao.getAll()
            }
        } catch (e: Exception) {
            themeDao.getAll()
        }
    }
}
