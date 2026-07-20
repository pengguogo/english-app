package com.englishapp.audio

import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 音频播放器。
 * 使用 MediaPlayer 播放后端 TTS 返回的音频二进制。
 * 支持播放完成回调与停止播放。
 * @author englishapp
 * @since 1.0.0
 */
@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var mediaPlayer: MediaPlayer? = null
    private var tempFile: File? = null
    private var onCompleteCallback: (() -> Unit)? = null

    /**
     * 播放音频。
     * 将二进制写入临时文件,通过 MediaPlayer 播放。
     * @param audioData 音频二进制(MP3/WAV)
     * @param onPlaying 播放开始回调
     * @param onComplete 播放完成回调
     */
    fun play(audioData: ByteArray, onPlaying: () -> Unit = {}, onComplete: () -> Unit = {}) {
        stop()
        onCompleteCallback = onComplete

        // 写入临时文件
        tempFile = File.createTempFile("tts_audio", ".mp3", context.cacheDir).apply {
            writeBytes(audioData)
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(tempFile!!.absolutePath)
            prepare()
            setOnCompletionListener {
                onCompleteCallback?.invoke()
                cleanup()
            }
            setOnErrorListener { _, _, _ ->
                cleanup()
                true
            }
            start()
        }
        onPlaying()
    }

    /** 停止播放并释放资源 */
    fun stop() {
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
        onCompleteCallback = null
        cleanup()
    }

    /** 判断是否正在播放 */
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    /** 清理临时文件 */
    private fun cleanup() {
        tempFile?.delete()
        tempFile = null
    }
}
