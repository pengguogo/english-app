package com.englishapp.data.model

/**
 * 完成课时请求体。
 * 对应后端 CompleteRequest,stars 范围 0-3,score 范围 0-100。
 * @author englishapp
 * @since 1.0.0
 */
data class CompleteRequest(
    val stars: Int,
    val score: Int
)
