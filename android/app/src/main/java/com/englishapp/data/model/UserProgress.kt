package com.englishapp.data.model

/**
 * 课时进度模型。
 * 对应后端 ProgressDto,status 取值:LOCKED / IN_PROGRESS / COMPLETED。
 * @author englishapp
 * @since 1.0.0
 */
data class UserProgress(
    val lessonId: Int,
    val status: String,
    val stars: Int,
    val score: Int
)
