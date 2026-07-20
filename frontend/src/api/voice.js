/**
 * @file 语音相关 API(TTS 合成与发音评测)
 * @description 对应后端:
 *  - POST /api/v1/voice/tts    文本转语音,返回 binary blob
 *  - POST /api/v1/voice/score  发音评测,multipart 上传音频
 * @author english-app
 * @since 2026-07-20
 */
import http from './http'

/**
 * 文本转语音。
 * 使用 responseType: 'blob' 让 axios 以二进制流接收,
 * 拦截器识别为 Blob 后直接透传,调用方可直接用于 <audio> 播放或 URL.createObjectURL。
 *
 * @param {string} text 待合成语音的文本
 * @return {Promise<Blob>} 语音二进制数据
 */
export const textToSpeech = (text) =>
  http.post('/voice/tts', { text }, { responseType: 'blob' })

/**
 * 发音评测。
 * 通过 FormData 以 multipart/form-data 上传录音文件与对照文本,
 * 后端返回 { code, message, data: { score, feedback } },拦截器剥离后返回 data。
 *
 * @param {Blob} audioBlob 录音二进制数据
 * @param {string} text 对照文本
 * @return {Promise<{score: number, feedback: string}>} 评测结果
 */
export const scorePronunciation = (audioBlob, text) => {
  const formData = new FormData()
  // 字段名 audio 与后端 MultipartFile 参数名对应,文件名固定 record.wav
  formData.append('audio', audioBlob, 'record.wav')
  formData.append('text', text)
  return http.post('/voice/score', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
