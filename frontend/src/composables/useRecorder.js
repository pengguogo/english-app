/**
 * @file useRecorder.js - 录音组合式函数
 * @description 封装浏览器 MediaRecorder API,将录音采集后转换编码为 WAV 格式
 *              (16kHz、16-bit、单声道),以适配后端百度语音评测 API 对音频
 *              格式的硬性要求。对外暴露响应式录音状态与启停方法。
 * @author english-app
 * @since 2026-07-20
 */
import { ref } from 'vue'

// ===== WAV 编码常量(百度语音评测要求:16kHz / 16bit / mono)=====
const WAV_SAMPLE_RATE = 16000
const WAV_NUM_CHANNELS = 1
const WAV_BIT_DEPTH = 16
const WAV_HEADER_SIZE = 44

/**
 * 录音组合式函数。
 * 通过 MediaRecorder 采集麦克风音频(webm),再经 AudioContext 解码、
 * 手动编码为符合后端要求的 WAV Blob。
 * @return {{isRecording: import('vue').Ref<boolean>,
 *           start: () => Promise<void>,
 *           stop: () => Promise<Blob|null>}}
 */
export function useRecorder() {
  // 录音状态(响应式),供组件绑定按钮样式与文案
  const isRecording = ref(false)
  let mediaRecorder = null
  // 音频数据块暂存区,stop 时合并编码
  let audioChunks = []
  // 专用于解码的 AudioContext(采样率锁定为 16kHz)
  let audioContext = null

  /**
   * 启动录音:申请麦克风权限并开始采集。
   * 注意:此处仅注册 ondataavailable,onstop 的编码逻辑由 stop() 接管,
   *       避免参考实现中 start/stop 重复绑定 onstop 造成的死代码。
   * @return {Promise<void>}
   * @throws {Error} 当用户拒绝麦克风权限或设备不可用时抛出
   */
  async function start() {
    if (isRecording.value) return
    // 指定采样率与声道数,从源头贴近目标格式;浏览器不保证完全遵从,
    // 后续 decodeAudioData 会基于 AudioContext 的采样率做重采样兜底
    const stream = await navigator.mediaDevices.getUserMedia({
      audio: {
        sampleRate: WAV_SAMPLE_RATE,
        channelCount: WAV_NUM_CHANNELS,
        echoCancellation: true
      }
    })
    // AudioContext 采样率一经创建不可变,锁定 16kHz 以保证解码后采样率一致
    audioContext = new AudioContext({ sampleRate: WAV_SAMPLE_RATE })
    mediaRecorder = new MediaRecorder(stream)
    audioChunks = []

    // 仅绑定数据采集回调,录音停止逻辑统一在 stop() 中处理
    mediaRecorder.ondataavailable = handleDataAvailable

    mediaRecorder.start()
    isRecording.value = true
  }

  /**
   * MediaRecorder 数据可用回调:收集非空数据块。
   * @param {BlobEvent} e 浏览器抛出的数据事件
   */
  function handleDataAvailable(e) {
    if (e.data.size > 0) audioChunks.push(e.data)
  }

  /**
   * 停止录音并返回 WAV Blob。
   * 通过覆盖 onstop 实现异步编码,编码失败时返回 null 由调用方提示用户。
   * @return {Promise<Blob|null>} 编码成功返回 WAV Blob,失败或未录音返回 null
   */
  function stop() {
    return new Promise((resolve) => {
      // 未在录音状态直接返回 null,保证调用方幂等
      if (!mediaRecorder || !isRecording.value) {
        resolve(null)
        return
      }
      // 在 stop 触发后执行编码,确保所有数据块已就绪
      mediaRecorder.onstop = async () => {
        try {
          const wavBlob = await encodeChunksToWav()
          resolve(wavBlob)
        } catch (e) {
          console.error('录音编码失败:', e)
          resolve(null)
        } finally {
          cleanup()
        }
      }
      mediaRecorder.stop()
    })
  }

  /**
   * 将采集到的 webm 数据块解码并编码为 WAV。
   * @return {Promise<Blob>} WAV 格式 Blob
   */
  async function encodeChunksToWav() {
    const blob = new Blob(audioChunks, { type: 'audio/webm' })
    const arrayBuffer = await blob.arrayBuffer()
    // decodeAudioData 会按 audioContext 的采样率(16kHz)重采样
    const audioBuffer = await audioContext.decodeAudioData(arrayBuffer)
    return encodeWav(audioBuffer)
  }

  /**
   * 释放底层资源:停止麦克风轨道并关闭 AudioContext。
   * 无论编码成功与否都必须执行,避免麦克风指示灯常亮与上下文泄漏。
   */
  function cleanup() {
    if (mediaRecorder && mediaRecorder.stream) {
      mediaRecorder.stream.getTracks().forEach((t) => t.stop())
    }
    if (audioContext) {
      audioContext.close()
      audioContext = null
    }
    isRecording.value = false
  }

  /**
   * 将 AudioBuffer 编码为 WAV(16-bit PCM)Blob。
   * 采样率取自 audioBuffer 实际值(经 AudioContext 重采样后应为 16kHz),
   * 而非硬编码,以提升对浏览器差异的健壮性。
   * @param {AudioBuffer} audioBuffer 解码后的音频缓冲
   * @return {Blob} WAV 格式 Blob
   */
  function encodeWav(audioBuffer) {
    const samples = audioBuffer.getChannelData(0)
    const sampleRate = audioBuffer.sampleRate
    const dataLength = samples.length * 2
    const buffer = new ArrayBuffer(WAV_HEADER_SIZE + dataLength)
    const view = new DataView(buffer)
    writeWavHeader(view, dataLength, sampleRate)
    writePcmSamples(view, samples)
    return new Blob([buffer], { type: 'audio/wav' })
  }

  /**
   * 写入 44 字节 WAV 文件头(RIFF / WAVE / fmt / data)。
   * @param {DataView} view 待写入的数据视图
   * @param {number} dataLength PCM 数据字节数
   * @param {number} sampleRate 实际采样率
   */
  function writeWavHeader(view, dataLength, sampleRate) {
    writeString(view, 0, 'RIFF')
    view.setUint32(4, 36 + dataLength, true)
    writeString(view, 8, 'WAVE')
    writeString(view, 12, 'fmt ')
    view.setUint32(16, 16, true) // fmt 块长度固定 16
    view.setUint16(20, 1, true) // 编码格式:1 = PCM
    view.setUint16(22, WAV_NUM_CHANNELS, true)
    view.setUint32(24, sampleRate, true)
    view.setUint32(28, sampleRate * WAV_NUM_CHANNELS * 2, true) // 字节率
    view.setUint16(32, WAV_NUM_CHANNELS * 2, true) // 块对齐
    view.setUint16(34, WAV_BIT_DEPTH, true)
    writeString(view, 36, 'data')
    view.setUint32(40, dataLength, true)
  }

  /**
   * 将浮点样本(-1~1)写入为 16-bit 有符号整数 PCM 数据。
   * @param {DataView} view 待写入的数据视图
   * @param {Float32Array} samples 单声道浮点样本
   */
  function writePcmSamples(view, samples) {
    let offset = WAV_HEADER_SIZE
    for (let i = 0; i < samples.length; i++) {
      // 先限幅到 [-1,1] 防止溢出,再分别映射负/正区间到 int16
      const s = Math.max(-1, Math.min(1, samples[i]))
      view.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7fff, true)
      offset += 2
    }
  }

  /**
   * 向 DataView 写入 ASCII 字符串(用于 WAV 头中的 chunk 标识)。
   * @param {DataView} view 待写入的数据视图
   * @param {number} offset 起始偏移
   * @param {string} str 待写入的字符串
   */
  function writeString(view, offset, str) {
    for (let i = 0; i < str.length; i++) {
      view.setUint8(offset + i, str.charCodeAt(i))
    }
  }

  return { isRecording, start, stop }
}
