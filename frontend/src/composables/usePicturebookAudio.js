import { onBeforeUnmount, ref } from 'vue'
import { useTts } from './useTts'

// 优先复用项目语音；服务不可用时尝试设备朗读，失败也不阻断翻页。
export function usePicturebookAudio() {
  const { playAndWait, stop: stopTts } = useTts()
  const speaking = ref(false)
  const audioError = ref('')
  let version = 0
  let utterance = null
  let timeout = null
  function stop() {
    version++
    stopTts()
    if (utterance) window.speechSynthesis?.cancel()
    utterance = null
    clearTimeout(timeout)
    speaking.value = false
  }
  async function speak(text, language = 'en') {
    stop()
    const session = version
    audioError.value = ''
    speaking.value = true
    try {
      await playAndWait(text, language, { voiceProfile: language === 'en' ? 'story-narrator' : 'challenge-curious', cacheable: true })
      if (session === version) speaking.value = false
    } catch {
      if (session !== version) return
      if (!window.speechSynthesis || !window.SpeechSynthesisUtterance) {
        speaking.value = false
        audioError.value = '广播暂时无法播放，可以重试或和家长一起读。'
        return
      }
      utterance = new SpeechSynthesisUtterance(text)
      utterance.lang = language === 'en' ? 'en-GB' : 'zh-CN'
      utterance.rate = 0.85
      const finish = () => { if (session === version) { speaking.value = false; utterance = null; clearTimeout(timeout) } }
      utterance.onend = finish
      utterance.onerror = () => { if (session === version) audioError.value = '设备朗读失败，可以重试或继续看图。'; finish() }
      window.speechSynthesis.speak(utterance)
      timeout = setTimeout(() => { if (session === version) { stop(); audioError.value = '广播超时，请重试。' } }, 45000)
    }
  }
  onBeforeUnmount(stop)
  return { speak, stop, speaking, audioError }
}
