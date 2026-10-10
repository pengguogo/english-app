import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { ref, computed, reactive, watch, effectScope } from 'vue'
import { normalizeContent } from '../src/utils/lessonContent.js'
function source(name) {
  return readFileSync(new URL(`../src/composables/${name}.js`, import.meta.url), 'utf8')
    .replace(/^import .*\n/gm, '').replaceAll('export function', 'function')
}
function setup(load = async id => ({ id: Number(id), unitId: 4, type: 'WORD', name: '水果', content: { words: ['apple', 'banana'] } })) {
  const route = reactive({ params: { lessonId: '7' }, query: {} }), events = []
  const router = { replace: async target => events.push(['route', target]) }
  let unmount
  const context = { ref, computed, watch, useRoute: () => route, useRouter: () => router,
    useSafeBack: () => ({ safeBack: target => events.push(['back', target]) }),
    getLessonById: load, recordWrongAnswer: async data => events.push(['wrong', data]),
    stopActiveTts() {}, useLessonBookmark: () => () => {}, useStudyTimer() {},
    useLearningAttempts: () => ({ recordAttempt: data => events.push(['answer', data]) }),
    scorePronunciation: async () => ({ score: 80 }),
    completeLesson: async id => events.push(['complete', id]),
    getLessonsByUnit: async () => [], getUnitsByTheme: async () => [], findNextReadingLesson: async () => null,
    normalizeContent, onMounted() {}, onBeforeUnmount: fn => { unmount = fn },
    setTimeout: () => 1, clearTimeout() {}, console, alert() {} }
  for (const name of ['WordLesson', 'CharacterLesson', 'SentenceLesson', 'ReadingLesson', 'QuizLesson', 'CalculateLesson', 'PhonicsLesson', 'DialogueLesson']) context[name] = name
  const factory = runInNewContext(`${source('useLessonScoring')}\n${source('useLessonCompletion')}\n${source('useLessonLearning')}\nuseLessonLearning`, context)
  const scope = effectScope(), state = scope.run(() => factory())
  return { state, events, route, unmount: () => { unmount(); scope.stop() } }
}

test('共享课程流程兼容旧单词格式，切题进入复习，保存成功后才庆祝完成', async () => {
  const flow = setup()
  await flow.state.loadLesson()
  assert.equal(flow.state.currentItem.value.translation, '苹果')
  assert.equal(flow.state.lessonTemplate.value, 'WordLesson')
  flow.state.skipCurrentItem()
  assert.equal(flow.state.currentIndex.value, 1)
  assert.equal(flow.events[0][0], 'wrong')
  flow.state.nextItem()
  assert.equal(flow.state.showReview.value, true)
  await flow.state.handleReviewPassed()
  assert.equal(flow.state.isComplete.value, true)
  assert.equal(flow.state.isProgressSaved.value, true)
  flow.unmount()
})

test('快速切课后旧响应不覆盖当前课时，绘本仍跳转原有阅读流程', async () => {
  let resolve
  const flow = setup(id => id === '7' ? new Promise(done => { resolve = done })
    : Promise.resolve({ id: 8, unitId: 9, type: 'READING', content: { picturebook: true } }))
  const pending = flow.state.loadLesson()
  flow.route.params.lessonId = '8'
  for (let i = 0; i < 4; i++) await Promise.resolve()
  resolve({ id: 7, type: 'WORD', content: { words: ['cat'] } })
  await pending
  assert.equal(flow.state.lesson.value, null)
  assert.equal(flow.events[0][1].path, '/picturebooks/9')
  flow.unmount()
})
