import { ref, onBeforeUnmount } from 'vue'

/**
 * 未解锁时点「→」：短暂高亮该先做的按钮，引导孩子先听或先练。
 * @param {number} duration 高亮持续毫秒
 */
export function useNextHint(duration = 1600) {
  const hinting = ref(false)
  let timer = 0
  function showHint() {
    clearTimeout(timer)
    hinting.value = true
    timer = setTimeout(() => { hinting.value = false }, duration)
  }
  onBeforeUnmount(() => clearTimeout(timer))
  return { hinting, showHint }
}
