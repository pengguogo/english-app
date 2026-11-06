/**
 * @file 学习伙伴 API(阶段 1 课程向导)
 * @description 对应后端:
 *  - POST /api/v1/agent/asr     语音识别,multipart audio(16k 单声道 WAV),需登录 + 家庭已同意
 *  - POST /api/v1/agent/guide   文本检索课程,游客可用
 *  - GET  /api/v1/agent/topics  热门主题入口,游客可用
 *  所有请求都可传 signal,切页/关闭面板时由 useCompanion 取消。
 */
import http from './http'

/**
 * 语音识别。
 * @param {Blob} audioBlob 录音 WAV
 * @param {{signal?: AbortSignal}} [options]
 * @return {Promise<{text: string, provider: string, latencyMs: number}>}
 */
export const recognizeSpeech = (audioBlob, { signal } = {}) => {
  const formData = new FormData()
  formData.append('audio', audioBlob, 'speech.wav')
  return http.post('/agent/asr', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    signal
  })
}

/**
 * 按孩子说的话检索课程。
 * @param {{text: string, lessonId?: number}} body
 * @param {{signal?: AbortSignal}} [options]
 * @return {Promise<{type: 'RECOMMEND'|'CLARIFY'|'NO_MATCH', speech: string, candidates: Array, topics: Array}>}
 */
export const askGuide = (body, { signal } = {}) => http.post('/agent/guide', body, { signal })

/** 热门主题入口。 */
export const fetchTopics = ({ signal } = {}) => http.get('/agent/topics', { signal })
