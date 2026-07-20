/**
 * @file 音频状态 Pinia store
 * @description
 *  集中管理音频播放与录音的全局状态,避免多个组件各自维护导致状态不同步。
 *  例如:TTS 播放时禁止重复触发、录音时禁止播放等互斥逻辑可基于此状态实现。
 * @author english-app
 * @since 2026-07-20
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAudioStore = defineStore('audio', () => {
  // 是否正在播放 TTS 音频
  const isPlaying = ref(false)
  // 是否正在录音(发音评测输入)
  const isRecording = ref(false)

  /**
   * 设置播放状态。
   *
   * @param {boolean} val 是否播放中
   */
  function setPlaying(val) {
    isPlaying.value = val
  }

  /**
   * 设置录音状态。
   *
   * @param {boolean} val 是否录音中
   */
  function setRecording(val) {
    isRecording.value = val
  }

  return { isPlaying, isRecording, setPlaying, setRecording }
})
