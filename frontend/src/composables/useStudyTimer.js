import { onBeforeUnmount, onMounted, watch } from 'vue'
import { recordStudyTime } from '../api/games'

const HEARTBEAT_SECONDS = 15

/** 仅在学习页可见且课时加载成功时累计有效学习时长。 */
export function useStudyTimer(enabled) {
  let timer = null

  async function sendHeartbeat() {
    if (!enabled.value || document.visibilityState !== 'visible') return
    try {
      await recordStudyTime(HEARTBEAT_SECONDS)
    } catch (error) {
      console.error('记录学习时长失败:', error)
    }
  }

  function syncTimer() {
    clearInterval(timer)
    timer = null
    if (enabled.value && document.visibilityState === 'visible') {
      timer = setInterval(sendHeartbeat, HEARTBEAT_SECONDS * 1000)
    }
  }

  onMounted(() => {
    document.addEventListener('visibilitychange', syncTimer)
    syncTimer()
  })
  watch(enabled, syncTimer)
  onBeforeUnmount(() => {
    clearInterval(timer)
    document.removeEventListener('visibilitychange', syncTimer)
  })
}
