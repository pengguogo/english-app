import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { ref } from 'vue'

{
  const page = '新旧界面共享评分'
  const source = readFileSync(new URL('../src/composables/useLessonScoring.js', import.meta.url), 'utf8')
  const handler = source.slice(source.indexOf('async function handleRecorded('), source.indexOf('function updateBestScore('))
  function setup(scorePronunciation) {
    const state = { lesson: ref({ id: 1 }), currentItem: ref({ word: 'apple' }), currentText: ref('apple'),
      currentIndex: ref(0), currentScore: ref(90), currentStars: ref(3), scoreMessage: ref(''), isScoring: ref(false) }
    const scores = [90, 0]
    const callback = runInNewContext(`${handler}\nhandleRecorded`, { ...state, getVersion: () => 1, requestVersion: 0,
      scorePronunciation, markCurrentItemEngaged() {}, showMascotFeedback() {},
      scoreToStars: score => score >= 80 ? 3 : 0,
      updateBestScore: (index, score) => { scores[index] = Math.max(scores[index], score) }, console: { error() {} } })
    return { state, scores, callback }
  }
  test(`${page}：服务失败不覆盖历史成绩，有效零分仍可展示`, async () => {
    const failed = setup(async () => ({ score: null, feedback: '本次不计成绩' }))
    await failed.callback({})
    assert.equal(failed.state.currentScore.value, null)
    assert.equal(failed.state.currentStars.value, 0)
    assert.equal(failed.scores[0], 90)
    assert.match(failed.state.scoreMessage.value, /不计成绩/)
    const zero = setup(async () => ({ score: 0 }))
    await zero.callback({})
    assert.equal(zero.state.currentScore.value, 0)
  })
  test(`${page}：切题或切课后忽略旧评分，断网保留历史成绩`, async () => {
    let resolve
    const flow = setup(() => new Promise(done => { resolve = done }))
    const pending = flow.callback({})
    flow.state.currentIndex.value = 1
    resolve({ score: 100 })
    await pending
    assert.deepEqual(flow.scores, [90, 0])
    assert.equal(flow.state.currentScore.value, null)
    const network = setup(async () => { throw Error('断网') })
    await network.callback({})
    assert.equal(network.scores[0], 90)
    assert.equal(network.state.isScoring.value, false)
  })
}
