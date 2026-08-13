/**
 * 音频输出预热。
 * 手机、蓝牙耳机或音箱从休眠状态恢复时可能吞掉最初几百毫秒，先输出一段几乎
 * 不可闻的低频信号，让音频通道在正式语音开始前进入稳定状态。
 */
export const AUDIO_WARMUP_MS = 350
export const AUDIO_IDLE_THRESHOLD_MS = 1500

let audioContext = null
let lastOutputActiveAt = 0

export function shouldWarmAudioOutput(now = Date.now()) {
  return now - lastOutputActiveAt > AUDIO_IDLE_THRESHOLD_MS
}

export function markAudioOutputActive(now = Date.now()) {
  lastOutputActiveAt = now
}

export async function warmAudioOutput() {
  if (!shouldWarmAudioOutput()) return

  const AudioContextClass = globalThis.AudioContext || globalThis.webkitAudioContext
  if (!AudioContextClass) return

  try {
    audioContext ||= new AudioContextClass()
    if (audioContext.state === 'suspended') {
      await audioContext.resume()
    }

    const oscillator = audioContext.createOscillator()
    const gain = audioContext.createGain()
    oscillator.frequency.value = 30
    gain.gain.value = 0.0001
    oscillator.connect(gain)
    gain.connect(audioContext.destination)
    oscillator.start()
    oscillator.stop(audioContext.currentTime + AUDIO_WARMUP_MS / 1000)
    markAudioOutputActive()

    await new Promise((resolve) => setTimeout(resolve, AUDIO_WARMUP_MS))
    oscillator.disconnect()
    gain.disconnect()
  } catch (e) {
    // 预热失败不能阻断正常语音播放，部分浏览器会限制 AudioContext 自动启动。
    console.warn('音频输出预热失败，将直接播放:', e)
  }
}
