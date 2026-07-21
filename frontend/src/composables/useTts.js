/**
 * useTts.js - 文本转语音(TTS)组合式函数
 * 用途:封装调用后端 textToSpeech API 的完整流程,
 *      包括 loading 状态管理、Blob -> ObjectURL 转换、Audio 播放与旧 URL 释放。
 *      支持按语言代码(lan)合成中英文语音,并可等待播放完成。
 *      修复:引入 isPlaying 状态与 stop() 方法,避免连续点击导致音频叠加。
 * 作者:english-app
 * 创建日期:2026-07-20
 * 修改:2026-07-21 增加播放状态管理与停止机制
 */
import { ref } from 'vue'
import { textToSpeech } from '../api/voice'

/**
 * TTS 组合式函数。
 * @return {{audioUrl: import('vue').Ref<string|null>,
 *           isLoading: import('vue').Ref<boolean>,
 *           isPlaying: import('vue').Ref<boolean>,
 *           play: (text: string, lan?: string) => Promise<void>,
 *           playAndWait: (text: string, lan?: string) => Promise<void>,
 *           stop: () => void}}
 */
export function useTts() {
  // 当前音频的 ObjectURL(用于潜在的外部 <audio> 绑定)
  const audioUrl = ref(null)
  // 加载状态标志,仅表示"请求 TTS 接口过程中"
  const isLoading = ref(false)
  // 播放状态标志,涵盖"加载 + 播放"全过程,用于禁用按钮
  const isPlaying = ref(false)
  // 当前正在播放的 Audio 对象引用,用于停止播放
  let currentAudio = null

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
   * 停止当前正在播放的音频。
   * 调用 Audio.pause() 并置空引用,不影响 isPlaying 外部状态(由调用方管理)。
   */
  function stop() {
    if (currentAudio) {
      // 暂停播放并清理事件,避免 onended 再次触发状态变更
      try {
        currentAudio.pause()
        currentAudio.currentTime = 0
      } catch (e) {
        // 忽略 pause 异常(如音频尚未加载完成)
        console.warn('停止音频时出错:', e)
      }
      currentAudio.onended = null
      currentAudio.onerror = null
      currentAudio = null
    }
  }

  /**
   * 播放指定文本的语音。
   * 流程:停止上一个 -> 请求 TTS -> 释放旧 URL -> 创建新 ObjectURL -> Audio 播放。
   * @param {string} text 待播放的文本
   * @param {string} [lan='en'] 语言代码("en" 英文, "zh" 中文)
   * @return {Promise<void>}
   */
  async function play(text, lan = 'en') {
    // 如果正在播放,先停止上一个音频,避免叠加
    if (isPlaying.value) {
      stop()
    }
    isPlaying.value = true
    isLoading.value = true
    try {
      const blob = await textToSpeech(text, lan)
      releaseUrl()
      audioUrl.value = URL.createObjectURL(blob)
      // 创建新的 Audio 对象并保存引用,便于后续 stop()
      currentAudio = new Audio(audioUrl.value)
      await currentAudio.play()
    } catch (e) {
      console.error('TTS 播放失败:', e)
      alert('发音加载失败,请重试')
    } finally {
      isLoading.value = false
      isPlaying.value = false
      currentAudio = null
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
    // 如果正在播放,先停止上一个音频,避免叠加
    if (isPlaying.value) {
      stop()
    }
    isPlaying.value = true
    try {
      const blob = await textToSpeech(text, lan)
      releaseUrl()
      audioUrl.value = URL.createObjectURL(blob)
      // 创建新的 Audio 对象并保存引用
      currentAudio = new Audio(audioUrl.value)
      return new Promise((resolve, reject) => {
        // 播放完成后清理状态
        currentAudio.onended = () => {
          isPlaying.value = false
          currentAudio = null
          resolve()
        }
        currentAudio.onerror = (e) => {
          isPlaying.value = false
          currentAudio = null
          reject(e)
        }
        currentAudio.play().catch((e) => {
          isPlaying.value = false
          currentAudio = null
          reject(e)
        })
      })
    } catch (e) {
      console.error('TTS 播放失败:', e)
      isPlaying.value = false
      currentAudio = null
    }
  }

  return { audioUrl, isLoading, isPlaying, play, playAndWait, stop }
}
