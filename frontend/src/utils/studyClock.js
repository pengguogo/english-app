const IDLE_MS = 60000

/** 用单调时间累计可见且有近期操作的时长，保留不足一秒的余数。 */
export function createStudyClock(now) {
  let last = now
  let activity = now
  let active = false
  let milliseconds = 0
  function tick(time) {
    if (active) milliseconds += Math.max(0, Math.min(time, activity + IDLE_MS) - last)
    last = time
  }
  return {
    tick,
    setActive(value, time) { tick(time); active = value; if (value) activity = time },
    interact(time) { tick(time); activity = time },
    take(time) {
      tick(time)
      const seconds = Math.floor(milliseconds / 1000)
      milliseconds -= seconds * 1000
      return seconds
    }
  }
}

/** 失败的事件保留原 ID，下次发送重试；不会并发写入或重复累加。 */
export function createStudyQueue(send, uuid) {
  const queue = []
  let pending = null
  function drain() {
    if (pending) return pending
    if (!queue.length) return Promise.resolve()
    let failed = false
    pending = (async () => {
      while (queue.length) {
        try { await send(queue[0]); queue.shift() }
        catch { failed = true; break }
      }
    })().finally(() => {
      pending = null
      if (!failed && queue.length) return drain()
    })
    return pending
  }
  return {
    add(lessonId, seconds) {
      while (seconds > 0) {
        const chunk = Math.min(seconds, 30)
        queue.push({ lessonId, seconds: chunk, eventId: uuid() })
        seconds -= chunk
      }
      return drain()
    },
    drain
  }
}
