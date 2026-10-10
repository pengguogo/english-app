import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { ref, watch, effectScope } from 'vue'
import { createStudyClock, createStudyQueue } from '../src/utils/studyClock.js'
const source = readFileSync(new URL('../src/composables/useStudyTimer.js', import.meta.url), 'utf8')
  .replace(/^import .*\n/gm, '').replace('export function', 'function')

test('切课归属正确，隐藏及卸载结算尾段，不把隐藏时间算到新课', async () => {
  let time = 0, mount, unmount, id = 0
  const documentEvents = new Map(), windowEvents = new Map(), requests = []
  const document = { visibilityState: 'visible', addEventListener: (event, fn) => documentEvents.set(event, fn), removeEventListener: event => documentEvents.delete(event) }
  const factory = runInNewContext(`${source}\nuseStudyTimer`, { watch,
    onMounted: fn => { mount = fn }, onBeforeUnmount: fn => { unmount = fn },
    performance: { now: () => time }, crypto: { randomUUID: () => String(++id) },
    recordStudyTime: async (lessonId, seconds) => requests.push({ lessonId, seconds }),
    createStudyClock, createStudyQueue, document, window: {
      addEventListener: (event, fn) => windowEvents.set(event, fn), removeEventListener: event => windowEvents.delete(event)
    }, setInterval: () => 1, clearInterval() {} })
  const scope = effectScope(), enabled = ref(true), lessonId = ref(7)
  const timer = scope.run(() => factory(enabled, lessonId))
  mount()
  time = 8000; lessonId.value = 8
  await timer.flush()
  time = 13000; document.visibilityState = 'hidden'; documentEvents.get('visibilitychange')()
  await timer.flush()
  time = 40000; document.visibilityState = 'visible'; documentEvents.get('visibilitychange')()
  time = 45000; unmount()
  await timer.flush()
  assert.deepEqual(requests, [{ lessonId: 7, seconds: 8 }, { lessonId: 8, seconds: 5 }, { lessonId: 8, seconds: 5 }])
  assert.equal(documentEvents.size, 0); assert.equal(windowEvents.size, 0)
  scope.stop()
})
