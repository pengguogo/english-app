package com.englishapp.data.remote

/**
 * 后端统一响应包装类。
 * 对应后端 Result {code, message, data} 结构。
 * @author englishapp
 * @since 1.0.0
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    /** 判断业务是否成功(code == 200) */
    fun isSuccess(): Boolean = code == 200
}
