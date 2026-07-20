/**
 * useTts.js - 文本转语音(TTS)组合式函数
 * 用途:封装调用后端 textToSpeech API 的完整流程,
 *      包括 loading 状态管理、Blob -> ObjectURL 转换、Audio 播放与旧 URL 释放。
 *      支持按语言代码(lan)合成中英文语音,并可等待播放完成。
 * 作者:english-app
 * 创建日期:2026-07-20
 */
import { ref } from 'vue'
import { textToSpeech } from '../api/voice'

/**
 * TTS 组合式函数。
 * @return {{audioUrl: import('vue').Ref<string|null>,
 *           isLoading: import('vue').Ref<boolean>,
 *           play: (text: string, lan?: string) => Promise<void>,
 *           playAndWait: (text: string, lan?: string) => Promise<void>}}
 */
export function useTts() {
  // 当前音频的 ObjectURL(用于潜在的外部 <audio> 绑定)
  const audioUrl = ref(null)
  // 加载状态标志,防止重复点击
  const isLoading = ref(false)

  /**
   * 释放之前创建的 ObjectURL,避免内存泄漏。
   */
  function releaseUrl() {
    if (audioUrl.value) {
      URL.revokeObjectURL(audioUrl.value)
      audioUrl.value = null
    }
  }

  /**
   * 播放指定文本的语音。
   * 流程:请求 TTS -> 释放旧 URL -> 创建新 ObjectURL -> Audio 播放。
   * @param {string} text 待播放的文本
   * @param {string} [lan='en'] 语言代码("en" 英文, "zh" 中文)
   * @return {Promise<void>}
   */
  async function play(text, lan = 'en') {
    // 正在加载时直接返回,避免并发请求造成音频叠加
    if (isLoading.value) return
    isLoading.value = true
    try {
      const blob = await textToSpeech(text, lan)
      releaseUrl()
      audioUrl.value = URL.createObjectURL(blob)
      const audio = new Audio(audioUrl.value)
      await audio.play()
    } catch (e) {
      console.error('TTS 播放失败:', e)
      alert('发音加载失败,请重试')
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 播放指定文本的语音并等待播放完成。
   * 用于顺序播放多个音频的场景(如先中文再英文)。
   * @param {string} text 待播放的文本
   * @param {string} [lan='en'] 语言代码("en" 英文, "zh" 中文)
   * @return {Promise<void>}
   */
  async function playAndWait(text, lan = 'en') {
    try {
      const blob = await textToSpeech(text, lan)
      releaseUrl()
      audioUrl.value = URL.createObjectURL(blob)
      const audio = new Audio(audioUrl.value)
      return new Promise((resolve, reject) => {
        audio.onended = resolve
        audio.onerror = reject
        audio.play().catch(reject)
      })
    } catch (e) {
      console.error('TTS 播放失败:', e)
    }
  }

  return { audioUrl, isLoading, play, playAndWait }
}
