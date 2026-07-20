package com.englishapp.data.model

/**
 * 完成课时响应(含下一课解锁信息)。
 * 对应后端 CompleteResponse。
 * @author englishapp
 * @since 1.0.0
 */
data class CompleteResponse(
    val nextLessonId: Int?,
    val unlocked: Boolean
)
