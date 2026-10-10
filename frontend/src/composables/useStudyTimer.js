import { onBeforeUnmount, onMounted, watch } from 'vue'
import { recordStudyTime } from '../api/games'
import { createStudyClock, createStudyQueue } from '../utils/studyClock'

/** 页面可见且60秒内有操作时计时；隐藏、切课和卸载结算尾段。 */
export function useStudyTimer(enabled, lessonId) {
  const now = () => performance.now()
  let clock = createStudyClock(now())
  let currentLesson = null
  let timer
  const queue = createStudyQueue(({ lessonId, seconds, eventId }) =>
    recordStudyTime(lessonId, seconds, eventId), () => crypto.randomUUID())

  function flush() {
    const seconds = clock.take(now())
    return currentLesson ? queue.add(currentLesson, seconds) : queue.drain()
  }
  function sync() {
    flush()
    if (currentLesson !== lessonId.value) {
      clock = createStudyClock(now())
      currentLesson = lessonId.value
    }
    clock.setActive(enabled.value && !!currentLesson && document.visibilityState === 'visible', now())
  }
  function interact() { clock.interact(now()) }
  function hide() { flush(); clock.setActive(false, now()) }

  onMounted(() => {
    sync()
    timer = setInterval(flush, 15000)
    document.addEventListener('visibilitychange', sync)
    for (const event of ['pointerdown', 'keydown', 'scroll']) document.addEventListener(event, interact, { passive: true })
    window.addEventListener('pagehide', hide)
  })
  watch([enabled, lessonId], sync, { flush: 'sync' })
  onBeforeUnmount(() => {
    hide()
    clearInterval(timer)
    document.removeEventListener('visibilitychange', sync)
    for (const event of ['pointerdown', 'keydown', 'scroll']) document.removeEventListener(event, interact)
    window.removeEventListener('pagehide', hide)
  })
  return { flush }
}
