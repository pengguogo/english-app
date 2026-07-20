package com.englishapp.data.remote

import com.englishapp.data.model.CompleteRequest
import com.englishapp.data.model.CompleteResponse
import com.englishapp.data.model.Lesson
import com.englishapp.data.model.ScoreResponse
import com.englishapp.data.model.Theme
import com.englishapp.data.model.Unit
import com.englishapp.data.model.UserProgress
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * 后端 API 服务接口。
 * 对接 Spring Boot 后端 /api/v1/* 全部端点。
 * @author englishapp
 * @since 1.0.0
 */
interface ApiService {

    /** 获取所有主题 */
    @GET("api/v1/themes")
    suspend fun getThemes(): ApiResponse<List<Theme>>

    /** 获取某主题下所有单元(带进度) */
    @GET("api/v1/units/theme/{themeId}")
    suspend fun getUnitsByTheme(
        @Path("themeId") themeId: Int,
        @Query("userId") userId: Int = 1
    ): ApiResponse<List<Unit>>

    /** 获取某单元下所有课时 */
    @GET("api/v1/lessons/unit/{unitId}")
    suspend fun getLessonsByUnit(@Path("unitId") unitId: Int): ApiResponse<List<Lesson>>

    /** 根据 ID 获取课时详情(含 content JSON) */
    @GET("api/v1/lessons/{id}")
    suspend fun getLessonById(@Path("id") id: Int): ApiResponse<Lesson>

    /** 获取某课时进度 */
    @GET("api/v1/progress/lesson/{lessonId}")
    suspend fun getLessonProgress(
        @Path("lessonId") lessonId: Int,
        @Query("userId") userId: Int = 1
    ): ApiResponse<UserProgress>

    /** 完成课时并解锁下一课 */
    @POST("api/v1/progress/lesson/{lessonId}/complete")
    suspend fun completeLesson(
        @Path("lessonId") lessonId: Int,
        @Body request: CompleteRequest,
        @Query("userId") userId: Int = 1
    ): ApiResponse<CompleteResponse>

    /** 文字转语音(返回二进制音频流) */
    @Streaming
    @POST("api/v1/voice/tts")
    suspend fun textToSpeech(@Body body: Map<String, String>): ResponseBody

    /** 发音评测(multipart:audio + text) */
    @Multipart
    @POST("api/v1/voice/score")
    suspend fun scorePronunciation(
        @Part audio: MultipartBody.Part,
        @Part("text") text: RequestBody
    ): ApiResponse<ScoreResponse>
}
