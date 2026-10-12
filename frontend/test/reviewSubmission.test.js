import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { ref } from 'vue'

function setupReview(submitReview) {
  const props = { lessonId: 7, saving: false, saveError: '' }
  const events = []
  const source = readFileSync(new URL('../src/components/lesson-templates/LessonReview.vue', import.meta.url), 'utf8')
    .split('<script setup>')[1].split('</script>')[0].replace(/^import .*\n/gm, '')
  const state = runInNewContext(`${source}\n({ load, submit, answers, loading, error, reviewPassed })`, {
    ref, onMounted() {}, defineProps: () => props, defineEmits: () => event => events.push(event),
    getReviewQuestions: async () => [{ index: 0 }], submitReview
  })
  return { state, props, events }
}

test('请求未完成或进度保存中时，重复点击不会重复提交', async () => {
  let resolve, requests = 0
  const flow = setupReview(() => { requests++; return new Promise(done => { resolve = done }) })
  await flow.state.load()
  flow.state.answers.value[0] = 0
  const pending = flow.state.submit()
  await flow.state.submit()
  assert.equal(requests, 1)
  resolve({ passed: true })
  await pending
  flow.props.saving = true
  await flow.state.submit()
  assert.equal(flow.events.length, 1)
  assert.equal(flow.state.loading.value, false)
})

test('保存失败后重试保留复习通过结果，只重新触发进度保存', async () => {
  let requests = 0
  const flow = setupReview(async () => { requests++; return { passed: true } })
  await flow.state.load()
  flow.state.answers.value[0] = 0
  await flow.state.submit()
  flow.props.saveError = '保存失败'
  await flow.state.submit()
  assert.equal(requests, 1)
  assert.equal(flow.events.length, 2)
  assert.equal(flow.state.reviewPassed.value, true)
})

test('提交超时或失败后解除等待，允许保留答案重新提交', async () => {
  let requests = 0
  const flow = setupReview(async () => {
    if (++requests === 1) throw new Error('timeout')
    return { passed: true }
  })
  await flow.state.load()
  flow.state.answers.value[0] = 0
  await flow.state.submit()
  assert.equal(flow.state.loading.value, false)
  assert.equal(flow.state.reviewPassed.value, false)
  assert.equal(flow.state.error.value, '提交失败，请重试')
  await flow.state.submit()
  assert.equal(requests, 2)
  assert.equal(flow.events.length, 1)
})
