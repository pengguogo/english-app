package com.englishapp.repository

import com.englishapp.data.model.CompleteRequest
import com.englishapp.data.model.CompleteResponse
import com.englishapp.data.model.UserProgress
import com.englishapp.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 进度仓储。
 * 提供课时进度查询与完成课时提交。
 * @author englishapp
 * @since 1.0.0
 */
@Singleton
class ProgressRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * 获取某课时进度。
     * @param lessonId 课时 ID
     * @return 进度信息,无记录时返回 null
     */
    suspend fun getLessonProgress(lessonId: Int): UserProgress? {
        return try {
            val response = apiService.getLessonProgress(lessonId)
            if (response.isSuccess()) response.data else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 完成课时并解锁下一课。
     * @param lessonId 课时 ID
     * @param stars 获得星星数(0-3)
     * @param score 平均分数(0-100)
     * @return 完成响应(含下一课解锁信息)
     * @throws Exception 网络或业务错误时抛出
     */
    suspend fun completeLesson(lessonId: Int, stars: Int, score: Int): CompleteResponse {
        val request = CompleteRequest(stars, score)
        val response = apiService.completeLesson(lessonId, request)
        if (response.isSuccess() && response.data != null) {
            return response.data
        }
        throw Exception(response.message)
    }
}
