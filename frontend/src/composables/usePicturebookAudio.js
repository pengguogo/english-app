import { onBeforeUnmount, ref } from 'vue'
import { useTts } from './useTts'
import { createPicturebookPlayback } from '../utils/picturebookPlayback'

// 优先复用项目语音；服务不可用时尝试设备朗读，失败也不阻断翻页。
export function usePicturebookAudio() {
  const { playAndWait, stop: stopTts } = useTts()
  const speaking = ref(false)
  const audioError = ref('')
  const { speak, speakSequence, stop } = createPicturebookPlayback({
    playAndWait, stopTts,
    onState(state) {
      if ('speaking' in state) speaking.value = state.speaking
      if ('error' in state) audioError.value = state.error
    },
  })
  onBeforeUnmount(stop)
  return { speak, speakSequence, stop, speaking, audioError }
}
