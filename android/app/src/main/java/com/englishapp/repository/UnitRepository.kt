package com.englishapp.repository

import com.englishapp.data.model.Unit
import com.englishapp.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 单元仓储。
 * 提供按主题查询单元列表(含进度信息)。
 * @author englishapp
 * @since 1.0.0
 */
@Singleton
class UnitRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * 获取某主题下所有单元(带进度)。
     * @param themeId 主题 ID
     * @return 单元列表
     * @throws Exception 网络或业务错误时抛出
     */
    suspend fun getUnitsByTheme(themeId: Int): List<Unit> {
        val response = apiService.getUnitsByTheme(themeId)
        if (response.isSuccess() && response.data != null) {
            return response.data
        }
        throw Exception(response.message)
    }
}
