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
import { markAudioOutputActive, warmAudioOutput } from '../utils/audioOutputWarmup'

let activeStop = null

/** 停止页面中当前活跃的 TTS 播放。 */
export function stopActiveTts() {
  activeStop?.()
}

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
  let ownsAudioUrl = false
  // 当前等待播放完成任务的结束回调,用于主动停止时立即解除等待
  let finishCurrentPlayback = null
  // 播放任务版本号,用于取消仍在请求 TTS 的旧任务
  let playbackVersion = 0

  /**
   * 释放之前创建的 ObjectURL,避免内存泄漏。
   */
  function releaseUrl() {
    if (audioUrl.value && ownsAudioUrl) {
      URL.revokeObjectURL(audioUrl.value)
    }
    audioUrl.value = null
    ownsAudioUrl = false
  }

  async function createAudio(text, lan, options) {
    releaseUrl()
    if (options.audioUrl) {
      audioUrl.value = options.audioUrl
      return new Audio(options.audioUrl)
    }
    const blob = await textToSpeech(text, lan, options)
    if (!blob || blob.size === 0) {
      throw new Error('语音合成返回空数据,可能服务暂时不可用')
    }
    audioUrl.value = URL.createObjectURL(blob)
    ownsAudioUrl = true
    return new Audio(audioUrl.value)
  }

  /**
   * 停止当前正在播放的音频。
   * 调用 Audio.pause() 并置空引用,不影响 isPlaying 外部状态(由调用方管理)。
   */
  function stop() {
    playbackVersion++
    const finish = finishCurrentPlayback
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
    if (finish) {
      finish()
    } else {
      isPlaying.value = false
    }
    if (activeStop === stop) activeStop = null
  }

  function claimPlayback() {
    activeStop?.()
    activeStop = stop
  }

  /**
   * 播放指定文本的语音(不等待播放完成)。
   * 流程:停止上一个 -> 请求 TTS -> 校验 blob -> 创建 Audio -> 开始播放。
   * 播放状态 isPlaying 在 onended/onerror 时才重置,正确反映播放全过程。
   * @param {string} text 待播放的文本
   * @param {string} [lan='en'] 语言代码("en" 英文, "zh" 中文)
   * @return {Promise<void>}
   * @throws {Error} 当 blob 为空或播放失败时抛出
   */
  async function play(text, lan = 'en', options = {}) {
    claimPlayback()
    const version = ++playbackVersion
    isPlaying.value = true
    isLoading.value = true
    try {
      // 在拉取音频的同时预热输出设备，避免手机/蓝牙设备吞掉开头几个字。
      const warmup = warmAudioOutput()
      const audio = await createAudio(text, lan, options)
      if (version !== playbackVersion) return
      await warmup
      // 请求较慢时首次预热可能已经失效，播放前按闲置时间再检查一次。
      await warmAudioOutput()
      if (version !== playbackVersion) return
      currentAudio = audio
      // 播放结束时清理状态(不在 finally 中重置,因为 play 不等待播放完成)
      currentAudio.onended = () => {
        markAudioOutputActive()
        isPlaying.value = false
        currentAudio = null
        if (activeStop === stop) activeStop = null
      }
      currentAudio.onerror = () => {
        isPlaying.value = false
        currentAudio = null
        if (activeStop === stop) activeStop = null
      }
      await currentAudio.play()
    } catch (e) {
      if (version !== playbackVersion) return
      console.error('TTS 播放失败:', e)
      isPlaying.value = false
      currentAudio = null
      throw e
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 播放指定文本的语音并等待播放完成。
   * 用于顺序播放多个音频的场景(如先中文再英文)。
   * 修复:添加 blob 校验、settled 防重复回调、基于 duration 的超时兜底,
   *      避免 onended 不触发时 isPlaying 永久卡死。
   * @param {string} text 待播放的文本
   * @param {string} [lan='en'] 语言代码("en" 英文, "zh" 中文)
   * @return {Promise<void>}
   * @throws {Error} 当 blob 为空或播放失败时抛出
   */
  async function playAndWait(text, lan = 'en', options = {}) {
    claimPlayback()
    const version = ++playbackVersion
    isPlaying.value = true
    try {
      // 预热与网络请求并行，不额外叠加大部分接口等待时间。
      const warmup = warmAudioOutput()
      const audio = await createAudio(text, lan, options)
      if (version !== playbackVersion) return
      await warmup
      await warmAudioOutput()
      if (version !== playbackVersion) return
      currentAudio = audio
      // 等待播放完成,带超时兜底防止 onended 不触发导致永久卡死
      await new Promise((resolve, reject) => {
        let settled = false
        let timeoutId = null

        /** 安全结束:确保只执行一次,清理状态并 resolve */
        const finish = () => {
          if (settled) return
          settled = true
          if (timeoutId) clearTimeout(timeoutId)
          finishCurrentPlayback = null
          markAudioOutputActive()
          // 兜底:超时先于 onended 触发时,音频可能仍在播放。
          // 必须主动 pause + 移除事件,否则旧音频会继续发声,
          // 与下一轮新音频叠加,表现为"中间重复播放"。
          if (currentAudio) {
            try {
              currentAudio.onended = null
              currentAudio.onerror = null
              currentAudio.onloadedmetadata = null
              currentAudio.pause()
              currentAudio.currentTime = 0
            } catch (e) {
              console.warn('结束音频时出错:', e)
            }
          }
          isPlaying.value = false
          currentAudio = null
          if (activeStop === stop) activeStop = null
          resolve()
        }

        /** 安全失败:确保只执行一次,清理状态并 reject */
        const fail = (e) => {
          if (settled) return
          settled = true
          if (timeoutId) clearTimeout(timeoutId)
          finishCurrentPlayback = null
          // 与 finish 一致:出错时也主动 pause 旧音频,避免残留发声。
          if (currentAudio) {
            try {
              currentAudio.onended = null
              currentAudio.onerror = null
              currentAudio.onloadedmetadata = null
              currentAudio.pause()
              currentAudio.currentTime = 0
            } catch (err) {
              console.warn('结束音频时出错:', err)
            }
          }
          isPlaying.value = false
          currentAudio = null
          if (activeStop === stop) activeStop = null
          reject(e)
        }

        currentAudio.onended = finish
        currentAudio.onerror = fail
        finishCurrentPlayback = finish

        // 默认 30 秒兜底超时,防止 onended/onerror 均不触发时永久卡死
        timeoutId = setTimeout(finish, 30000)

        // 音频元数据加载后,基于实际时长设置更精确的超时(duration + 2秒)
        currentAudio.onloadedmetadata = () => {
          if (settled) return
          const duration = currentAudio.duration
          // duration 有效则用 duration+2s,否则保持 30s 兜底
          if (duration && isFinite(duration) && duration > 0) {
            clearTimeout(timeoutId)
            timeoutId = setTimeout(finish, (duration + 2) * 1000)
          }
        }

        currentAudio.play().catch(fail)
      })
    } catch (e) {
      if (version !== playbackVersion) return
      console.error('TTS 播放失败:', e)
      isPlaying.value = false
      currentAudio = null
      // 重新抛出,让调用方感知错误并提示用户
      throw e
    }
  }

  return { audioUrl, isLoading, isPlaying, play, playAndWait, stop }
}
