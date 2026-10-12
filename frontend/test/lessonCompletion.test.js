import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { ref } from 'vue'

function setup(completeLesson, continuous = false) {
  const state = { route: { params: { lessonId: '7' }, query: continuous ? { continuous: '1' } : {} },
    router: { replace: async () => {} }, lesson: ref({ id: 7, unitId: 4 }),
    totalStars: ref(3), totalBestScore: ref(100), showMascotFeedback() {} }
  for (const key of ['isSubmitting', 'isProgressSaved', 'isComplete', 'showReview']) state[key] = ref(key === 'showReview')
  state.saveError = ref('')
  const source = readFileSync(new URL('../src/composables/useLessonCompletion.js', import.meta.url), 'utf8')
    .replace(/^import .*\n/gm, '').replace('export function', 'function')
  const factory = runInNewContext(`${source}\nuseLessonCompletion`, { completeLesson,
    getLessonsByUnit: async () => [], getUnitsByTheme: async () => [],
    findNextReadingLesson: async () => null, console: { error() {} } })
  return { state, ...factory(state) }
}

test('复习通过后保存未完成时，再次触发不会并发保存', async () => {
  let resolve, requests = 0
  const flow = setup(() => { requests++; return new Promise(done => { resolve = done }) })
  const pending = flow.handleReviewPassed()
  await flow.handleReviewPassed()
  assert.equal(requests, 1)
  assert.equal(flow.state.isSubmitting.value, true)
  resolve()
  await pending
  assert.equal(flow.state.isSubmitting.value, false)
  assert.equal(flow.state.isComplete.value, true)
})

test('连续阅读保存失败显示页内错误，重试成功退出复习', async () => {
  let requests = 0
  const flow = setup(async () => { if (++requests === 1) throw new Error('timeout') }, true)
  await flow.handleReviewPassed()
  assert.equal(flow.state.isSubmitting.value, false)
  assert.match(flow.state.saveError.value, /重试保存/)
  assert.equal(flow.state.isComplete.value, false)
  await flow.handleReviewPassed()
  assert.equal(flow.state.saveError.value, '')
  assert.equal(flow.state.isComplete.value, true)
})
