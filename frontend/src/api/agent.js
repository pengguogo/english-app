/**
 * @file 学习伙伴 API(阶段 2 探索伙伴)
 * @description 对应后端:
 *  - POST /api/v1/agent/asr     语音识别,multipart audio(16k 单声道 WAV),需登录 + 家庭已同意;返回 turnToken
 *  - POST /api/v1/agent/chat    对话(推荐 / 讲解 + 点选题 / 安全话术),游客可用(只走检索)
 *  - POST /api/v1/agent/answer  提交点选题答案,quizToken 由 chat 下发
 *  - GET  /api/v1/agent/topics  热门主题入口,游客可用
 *  所有请求都可传 signal,切页/关闭面板时由 useCompanion 取消。
 */
import http from './http'

/**
 * 语音识别。
 * @param {Blob} audioBlob 录音 WAV
 * @param {{signal?: AbortSignal}} [options]
 * @return {Promise<{text: string, provider: string, latencyMs: number, turnToken?: string}>}
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
 * 和学习伙伴对话。
 * @param {{text: string, turnToken?: string, context?: {lessonId: number, itemIndex: number}}} body
 * @param {{signal?: AbortSignal}} [options]
 * @return {Promise<{type: 'RECOMMEND'|'CLARIFY'|'EXPLAIN'|'NO_MATCH'|'SAFE_REPLY', speech: string,
 *   explain?: object, quiz?: object, candidates: Array, topics: Array, learnMore?: {title: string, route: string}}>}
 */
export const chatWithAgent = (body, { signal } = {}) => http.post('/agent/chat', body, { signal })

/**
 * 提交点选题答案。
 * @param {{quizToken: string, optionId: string}} body
 * @param {{signal?: AbortSignal}} [options]
 * @return {Promise<{correct: boolean, speech: string, hint: string|null}>}
 */
export const answerQuiz = (body, { signal } = {}) => http.post('/agent/answer', body, { signal })

/** 热门主题入口。 */
export const fetchTopics = ({ signal } = {}) => http.get('/agent/topics', { signal })
