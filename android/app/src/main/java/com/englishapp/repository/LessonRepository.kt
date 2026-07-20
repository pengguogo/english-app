package com.englishapp.repository

import com.englishapp.data.model.Lesson
import com.englishapp.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 课时仓储。
 * 提供按单元查询课时列表与课时详情查询。
 * @author englishapp
 * @since 1.0.0
 */
@Singleton
class LessonRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * 获取某单元下所有课时。
     * @param unitId 单元 ID
     * @return 课时列表
     * @throws Exception 网络或业务错误时抛出
     */
    suspend fun getLessonsByUnit(unitId: Int): List<Lesson> {
        val response = apiService.getLessonsByUnit(unitId)
        if (response.isSuccess() && response.data != null) {
            return response.data
        }
        throw Exception(response.message)
    }

    /**
     * 根据 ID 获取课时详情(含 content JSON)。
     * @param lessonId 课时 ID
     * @return 课时详情
     * @throws Exception 网络或业务错误时抛出
     */
    suspend fun getLessonById(lessonId: Int): Lesson {
        val response = apiService.getLessonById(lessonId)
        if (response.isSuccess() && response.data != null) {
            return response.data
        }
        throw Exception(response.message)
    }
}
