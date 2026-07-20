package com.englishapp.data.model

/**
 * 发音评测结果。
 * 对应后端 ScoreResponse,score 范围 0-100。
 * @author englishapp
 * @since 1.0.0
 */
data class ScoreResponse(
    val score: Int,
    val feedback: String
)
