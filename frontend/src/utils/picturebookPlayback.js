// 一次只播放一组语音；停止后，尚未返回的网络请求也不能继续下一句。
export function createPicturebookPlayback({ playAndWait, stopTts, onState, device = globalThis }) {
  let version = 0
  let finishDevice = null
  const unavailable = '声音暂时没准备好。点喇叭再试一次，也可以继续看图。'

  function stop() {
    version++
    stopTts()
    if (finishDevice) {
      finishDevice(false)
      device.speechSynthesis?.cancel()
    }
    onState({ speaking: false })
  }

  function playDevice(text, language, session) {
    if (!device.speechSynthesis || !device.SpeechSynthesisUtterance) {
      onState({ error: unavailable })
      return Promise.resolve(false)
    }
    return new Promise(resolve => {
      const utterance = new device.SpeechSynthesisUtterance(text)
      utterance.lang = language === 'en' ? 'en-GB' : 'zh-CN'
      utterance.rate = 0.85
      let timeout
      let settled = false
      const finish = (completed, message = '') => {
        if (settled) return
        settled = true
        clearTimeout(timeout)
        utterance.onend = null
        utterance.onerror = null
        finishDevice = null
        if (message && session === version) onState({ error: message })
        resolve(completed && session === version)
      }
      finishDevice = finish
      utterance.onend = () => finish(true)
      utterance.onerror = () => finish(false, unavailable)
      timeout = setTimeout(() => {
        finish(false, '声音休息太久啦，点喇叭再试一次。')
        device.speechSynthesis.cancel()
      }, 45000)
      try {
        device.speechSynthesis.speak(utterance)
      } catch {
        finish(false, unavailable)
      }
    })
  }

  async function speakSequence(parts) {
    stop()
    const session = version
    onState({ speaking: true, error: '' })
    try {
      for (const { text, language = 'en' } of parts) {
        if (session !== version) return false
        if (!text?.trim()) continue
        try {
          await playAndWait(text, language, {
            voiceProfile: language === 'en' ? 'story-narrator' : 'challenge-curious', cacheable: true,
          })
        } catch {
          if (session !== version || !await playDevice(text, language, session)) return false
        }
      }
      return session === version
    } finally {
      if (session === version) onState({ speaking: false })
    }
  }

  return { stop, speakSequence, speak: (text, language = 'en') => speakSequence([{ text, language }]) }
}
