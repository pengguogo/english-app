package com.englishapp.repository

import com.englishapp.data.model.ScoreResponse
import com.englishapp.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 语音仓储。
 * 封装 TTS 与发音评测两个语音 API,处理 multipart 上传。
 * @author englishapp
 * @since 1.0.0
 */
@Singleton
class VoiceRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * 文字转语音。
     * @param text 要合成的英文文本
     * @return 音频二进制数据(MP3/WAV),失败时返回 null
     */
    suspend fun textToSpeech(text: String): ByteArray? {
        return try {
            val body = mapOf("text" to text)
            val audio = apiService.textToSpeech(body)
            audio.bytes()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 发音评测。
     * @param audioData WAV 格式音频二进制(16kHz/16bit/mono)
     * @param text 参考英文文本
     * @return 评测结果(分数 + 反馈)
     * @throws Exception 网络或业务错误时抛出
     */
    suspend fun scorePronunciation(audioData: ByteArray, text: String): ScoreResponse {
        // 构建 multipart:audio 文件部分
        val audioRequestBody = audioData.toRequestBody("audio/wav".toMediaTypeOrNull())
        val audioPart = MultipartBody.Part.createFormData(
            "audio",
            "recording.wav",
            audioRequestBody
        )
        // text 字段部分
        val textRequestBody = text.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = apiService.scorePronunciation(audioPart, textRequestBody)
        if (response.isSuccess() && response.data != null) {
            return response.data
        }
        throw Exception(response.message)
    }
}
