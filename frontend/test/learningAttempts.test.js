import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { ref, watch } from 'vue'
const source = readFileSync(new URL('../src/composables/useLearningAttempts.js', import.meta.url), 'utf8')
  .replace(/^import .*\n/gm, '').replace('export function', 'function')

test('首次错误不会被辅助答对覆盖，同一题复用事件ID，切题使用新ID', async () => {
  const requests = []
  let id = 0
  const factory = runInNewContext(`${source}\nuseLearningAttempts`, { watch,
    crypto: { randomUUID: () => String(++id) }, recordLearningAttempt: async request => requests.push(request), console })
  const lesson = ref({ id: 7, type: 'QUIZ' }), index = ref(0)
  const flow = factory(lesson, index)
  flow.recordAttempt({ correct: false, assisted: false })
  flow.recordAttempt({ correct: true, assisted: true })
  index.value = 1
  flow.recordAttempt({ correct: true, assisted: false })
  for (let i = 0; i < 12; i++) await Promise.resolve()
  assert.deepEqual(requests.map(r => r.eventId), ['1', '1', '2'])
  assert.deepEqual(requests.map(r => r.firstCorrect), [false, false, true])
  assert.deepEqual(requests.map(r => r.assistedCorrect), [false, true, false])
  assert.equal(requests[2].questionIndex, 1)
})
