import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { ref } from 'vue'
const source = readFileSync(new URL('../src/composables/useWrongAnswerPractice.js', import.meta.url), 'utf8')
  .replace(/^import .*\n/gm, '').replace('export function', 'function')
function setup(record, resolve) {
  const factory = runInNewContext(`${source}\nuseWrongAnswerPractice`, {
    ref, recordWrongAnswer: record, resolveWrongAnswer: resolve
  })
  return factory(ref({ id: 4, lessonId: 1, questionIndex: 2 }), ref({ question: '1+1', answer: 2 }))
}

test('答错不标记掌握，答对等待错题写入后再更新', async () => {
  const events = []
  let release
  const flow = setup(() => new Promise(done => { release = () => { events.push('wrong'); done() } }), async () => events.push('resolve'))
  flow.answered({ correct: false, firstWrong: true, userAnswer: 3, correctAnswer: 2 })
  const saved = flow.answered({ correct: true })
  assert.equal(flow.mastered.value, false)
  assert.deepEqual(events, [])
  await Promise.resolve()
  release()
  await saved
  assert.deepEqual(events, ['wrong', 'resolve'])
  assert.equal(flow.mastered.value, true)
})

test('掌握保存失败可以重试，保存中重复提交不会产生重复请求', async () => {
  let calls = 0
  const flow = setup(async () => {}, async () => { if (++calls === 1) throw Error('断网') })
  await flow.answered({ correct: true })
  assert.equal(flow.mastered.value, false)
  assert.match(flow.error.value, /保存失败/)
  const retry = flow.save()
  await flow.save()
  await retry
  assert.equal(calls, 2)
  assert.equal(flow.mastered.value, true)
})

test('错误记录写入失败时重试，失败期间不标记掌握', async () => {
  let writes = 0
  let resolves = 0
  const flow = setup(async () => { writes++; throw Error('断网') }, async () => resolves++)
  flow.answered({ correct: false, firstWrong: true })
  await flow.answered({ correct: true })
  assert.equal(flow.mastered.value, false)
  assert.equal(resolves, 0)
  await flow.save()
  assert.ok(writes >= 3)
  assert.equal(resolves, 0)
})
