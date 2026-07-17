package com.englishapp.data.remote

import com.englishapp.data.model.Lesson
import com.englishapp.data.model.Theme
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/v1/themes")
    suspend fun getThemes(): List<Theme>

    @GET("api/v1/lessons/unit/{unitId}")
    suspend fun getLessonsByUnit(@Path("unitId") unitId: Int): List<Lesson>
}
