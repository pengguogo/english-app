import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { computed, ref } from 'vue'

function eligibility(path, state) {
  const source = readFileSync(new URL(`../src/${path}`, import.meta.url), 'utf8')
  const hook = source.match(/useStudyTimer\([\s\S]*?\n[^\n]*lessonId\)\)|useStudyTimer\([^\n]*\n[^\n]*lesson\.value\?\.id\)\)/)?.[0]
  assert.ok(hook, '学习入口必须接入计时器')
  let result
  runInNewContext(hook, { computed, ...state, useStudyTimer: (enabled, lessonId) => { result = { enabled, lessonId } } })
  return result
}

test('绘本学习和复习计时，出发站、加载及到站页不计时', () => {
  const state = { active: ref(0), lesson: ref({ id: 7 }), loading: ref(false), arrival: ref(false), reviewing: ref(false) }
  const timer = eligibility('views/PicturebookView.vue', state)
  assert.equal(timer.enabled.value, true)
  state.reviewing.value = true
  assert.equal(timer.enabled.value, true)
  for (const key of ['loading', 'arrival']) {
    state[key].value = true
    assert.equal(timer.enabled.value, false)
    state[key].value = false
  }
  state.active.value = -1
  assert.equal(timer.enabled.value, false)
})

test('错题重练不受答错影响，加载和掌握完成后停止计时', () => {
  const state = { loading: ref(true), item: ref({}), mastered: ref(false), entry: ref({ lessonId: 8 }) }
  const timer = eligibility('views/WrongAnswerPracticeView.vue', state)
  assert.equal(timer.enabled.value, false)
  state.loading.value = false
  assert.equal(timer.enabled.value, true)
  assert.equal(timer.lessonId.value, 8)
  state.mastered.value = true
  assert.equal(timer.enabled.value, false)
})

test('汉字复习仅在练习中计时，切题使用当前课时归属', () => {
  const state = { active: ref(false), loading: ref(false), current: ref({ lessonId: 9 }), finished: ref(false) }
  const timer = eligibility('views/CharacterReviewView.vue', state)
  assert.equal(timer.enabled.value, false)
  state.active.value = true
  assert.equal(timer.enabled.value, true)
  state.current.value = { lessonId: 10 }
  assert.equal(timer.lessonId.value, 10)
  state.finished.value = true
  assert.equal(timer.enabled.value, false)
})

test('普通课程与课后复习计时，加载及完成后停止', () => {
  const source = readFileSync(new URL('../src/composables/useLessonLearning.js', import.meta.url), 'utf8')
  const expression = source.match(/const isActivelyStudying = (computed\([^\n]+\))/)[1]
  const state = { isLoading: ref(false), lesson: ref({ id: 7 }), isComplete: ref(false), showReview: ref(false) }
  const enabled = runInNewContext(expression, { computed, ...state })
  assert.equal(enabled.value, true)
  state.showReview.value = true
  assert.equal(enabled.value, true)
  state.isLoading.value = true
  assert.equal(enabled.value, false)
  state.isLoading.value = false
  state.isComplete.value = true
  assert.equal(enabled.value, false)
})
