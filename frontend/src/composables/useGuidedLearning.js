import { onBeforeUnmount, ref } from 'vue'
import { advanceJourney, getJourney } from '../api/agent'
import { useTts } from './useTts'

export function useGuidedLearning() {
  const screen = ref(null)
  const loading = ref(true)
  const pending = ref(false)
  const error = ref('')
  const audioError = ref('')
  const tts = useTts()
  let disposed = false

  async function listen() {
    const current = screen.value
    if (!current) return
    const options = current.options.map(option => option.text)
    const choices = options.length ? `你可以选：${options.join('，或者')}。` : ''
    const text = (current.feedback || current.speech) + choices
    audioError.value = ''
    try {
      await tts.play(text, 'zh')
    } catch (error) {
      if (error?.name !== 'AbortError') audioError.value = '这次声音没播出来，可以再点一下。'
    }
  }

  async function load() {
    loading.value = true
    error.value = ''
    try {
      screen.value = await getJourney()
    } catch {
      error.value = '暂时找不到学习进度，点一下重试。'
    } finally {
      loading.value = false
    }
  }

  async function act(action, option) {
    if (loading.value || pending.value) return
    pending.value = true
    error.value = ''
    audioError.value = ''
    tts.stop()
    try {
      screen.value = await advanceJourney(action, screen.value?.revision, option)
      if (!disposed) void listen()
    } catch {
      error.value = '豆豆刚才没接上，再点一次试试。'
    } finally {
      pending.value = false
    }
  }

  onBeforeUnmount(() => {
    disposed = true
    tts.stop()
  })
  return { screen, loading, pending, error, audioError, playing: tts.isPlaying, load, act, listen, stop: tts.stop }
}
