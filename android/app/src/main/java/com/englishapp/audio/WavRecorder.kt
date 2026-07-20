package com.englishapp.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WAV 录音器。
 * 使用 AudioRecord 录制 PCM 数据,然后封装为 WAV 格式(16kHz/16bit/mono)。
 * 符合百度语音 API 对音频格式的要求。
 * @author englishapp
 * @since 1.0.0
 */
@Singleton
class WavRecorder @Inject constructor() {

    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var pcmStream: ByteArrayOutputStream? = null
    private var recordThread: Thread? = null

    /**
     * 开始录音。
     * 配置 AudioRecord 并在子线程中持续读取 PCM 数据。
     * @throws IllegalStateException 当麦克风不可用时抛出
     */
    fun start() {
        if (isRecording) return

        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        val bufferSize = maxOf(minBufferSize * 2, 3200)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        pcmStream = ByteArrayOutputStream()
        isRecording = true

        audioRecord?.startRecording()

        // 启动子线程读取 PCM 数据,避免阻塞调用线程
        recordThread = Thread {
            val buffer = ShortArray(bufferSize)
            while (isRecording) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: -1
                if (read > 0) {
                    // Short 转 Little-Endian Byte
                    val byteBuffer = ByteBuffer.allocate(read * 2).order(ByteOrder.LITTLE_ENDIAN)
                    byteBuffer.asShortBuffer().put(buffer, 0, read)
                    pcmStream?.write(byteBuffer.array())
                }
            }
        }.also { it.start() }
    }

    /**
     * 停止录音并返回 WAV 格式音频。
     * @return WAV 二进制数据(16kHz/16bit/mono)
     */
    fun stop(): ByteArray {
        isRecording = false
        recordThread?.join(2000)
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        val pcmData = pcmStream?.toByteArray() ?: ByteArray(0)
        pcmStream = null
        return buildWav(pcmData)
    }

    /** 判断是否正在录音 */
    fun isRecording(): Boolean = isRecording

    /**
     * 将 PCM 数据封装为 WAV 格式。
     * 添加 44 字节 WAV 头(RIFF + fmt + data)。
     * @param pcmData 原始 PCM 16-bit Little-Endian 数据
     * @return 完整 WAV 文件二进制
     */
    private fun buildWav(pcmData: ByteArray): ByteArray {
        val totalDataLen = pcmData.size
        val totalLen = 36 + totalDataLen
        val byteRate = SAMPLE_RATE * 1 * 16 / 8 // 单声道 16-bit

        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        header.put("RIFF".toByteArray())        // RIFF 标识
        header.putInt(totalLen)                  // 文件总长度
        header.put("WAVE".toByteArray())        // WAVE 标识
        header.put("fmt ".toByteArray())        // fmt 标识
        header.putInt(16)                        // fmt 块长度
        header.putShort(1)                       // 音频格式(1 = PCM)
        header.putShort(1)                       // 声道数(1 = mono)
        header.putInt(SAMPLE_RATE)               // 采样率
        header.putInt(byteRate)                  // 字节率
        header.putShort(2)                       // 块对齐
        header.putShort(16)                      // 位深
        header.put("data".toByteArray())        // data 标识
        header.putInt(totalDataLen)              // PCM 数据长度

        val result = ByteArray(44 + totalDataLen)
        header.position(0)
        header.get(result, 0, 44)
        System.arraycopy(pcmData, 0, result, 44, totalDataLen)
        return result
    }
}
