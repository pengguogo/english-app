import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { ref, computed } from 'vue'
import { characterChoices, recognitionOutcome } from '../src/utils/characterPractice.js'

// 隔离网络和生命周期，使用真实 Vue ref/computed 验证完整练习状态机。
const source = readFileSync(new URL('../src/composables/useCharacterPractice.js', import.meta.url), 'utf8')
  .replace(/^import .*\n/gm, '').replace('export function', 'function')
function setup({ reviewOnly = false, record = async () => {} } = {}) {
  const timers = new Map()
  const events = []
  const requests = []
  let id = 0
  let reset
  let unmount
  const props = { lessonId: 68, currentIndex: 0, currentItem: { word: '山' },
    items: [{ word: '山' }, { word: '水' }, { word: '日' }], reviewOnly }
  const context = { ref, computed, characterChoices, recognitionOutcome,
    watch: (_, callback) => { reset = callback; callback() },
    onBeforeUnmount: callback => { unmount = callback },
    crypto: { randomUUID: () => `event-${++id}` }, stopActiveTts() {},
    setTimeout: (callback, delay) => { const key = ++id; timers.set(key, { callback, delay }); return key },
    clearTimeout: key => timers.delete(key),
    recordCharacterAttempt: request => { requests.push(request); return record(request) } }
  const factory = runInNewContext(`${source}\nuseCharacterPractice`, context)
  const state = factory(props, (...args) => events.push(args))
  const tick = () => { const pending = [...timers.values()]; timers.clear(); pending.forEach(t => t.callback()) }
  return { state, props, events, requests, timers, tick, reset: () => reset(), unmount: () => unmount() }
}
const settle = async () => { await Promise.resolve(); await Promise.resolve() }

test('答对先反馈，900ms 后自动下一问，重复点击不跳题', () => {
  const flow = setup()
  flow.state.advance()
  flow.state.heard.value = true
  flow.state.choose('山')
  flow.state.choose('山')
  assert.equal(flow.state.feedback.value, '找对了！')
  assert.equal(flow.state.phase.value, 1)
  assert.equal(flow.timers.size, 1)
  assert.equal([...flow.timers.values()][0].delay, 900)
  flow.tick()
  assert.equal(flow.state.phase.value, 2)
  assert.equal(flow.state.answered.value, false)
})

test('答错留在原题，切换学习项和卸载会取消待执行跳转', () => {
  const flow = setup()
  flow.state.advance()
  flow.state.heard.value = true
  flow.state.choose('水')
  assert.equal(flow.timers.size, 0)
  assert.equal(flow.state.phase.value, 1)
  flow.state.choose('山')
  flow.props.currentIndex = 1
  flow.props.currentItem = { word: '水' }
  flow.reset()
  flow.tick()
  assert.equal(flow.state.phase.value, 0)
  flow.state.advance()
  flow.state.heard.value = true
  flow.state.choose('水')
  flow.unmount()
  flow.tick()
  assert.equal(flow.events.length, 0)
  assert.equal(flow.state.phase.value, 1)
})

test('最后一问等待保存成功再展示反馈并自动下一个字', async () => {
  let complete
  const flow = setup({ reviewOnly: true, record: () => new Promise(resolve => { complete = resolve }) })
  flow.state.heard.value = true
  flow.state.choose('山')
  assert.equal(flow.state.saving.value, true)
  assert.equal(flow.timers.size, 0)
  flow.tick()
  assert.equal(flow.events.length, 0)
  complete()
  await settle()
  assert.equal(flow.state.saved.value, true)
  assert.equal(flow.events[0][0], 'answered')
  assert.equal(flow.events.some(e => e[0] === 'next'), false)
  flow.tick()
  assert.equal(flow.events.filter(e => e[0] === 'next').length, 1)
})

test('保存失败不跳转，重试保留同一事件和首次错误结果', async () => {
  let attempts = 0
  const flow = setup({ reviewOnly: true, record: async () => { if (++attempts === 1) throw new Error('offline') } })
  flow.state.heard.value = true
  flow.state.choose('水')
  flow.state.choose('山')
  await settle()
  assert.ok(flow.state.error.value)
  assert.equal(flow.timers.size, 0)
  assert.equal(flow.events.length, 0)
  flow.state.advance()
  await settle()
  assert.equal(flow.requests[0].eventId, flow.requests[1].eventId)
  assert.equal(flow.requests[1].outcome, 'WRONG')
  flow.tick()
  assert.equal(flow.events.filter(e => e[0] === 'next').length, 1)
})

test('保存过程中离开页面不会向新页面发出结果或自动跳转', async () => {
  let complete
  const flow = setup({ reviewOnly: true, record: () => new Promise(resolve => { complete = resolve }) })
  flow.state.heard.value = true
  flow.state.choose('山')
  flow.unmount()
  complete()
  await settle()
  flow.tick()
  assert.equal(flow.events.length, 0)
  assert.equal(flow.timers.size, 0)
})

test('虚词听词找字，使用词语位置提示并保留自动跳题', () => {
  const flow = setup()
  flow.props.currentItem = { word: '的', exampleWord: '我的', imageChoice: false }
  flow.props.items = [{ word: '的' }, { word: '地' }, { word: '了' }]
  flow.reset()
  flow.state.advance()
  assert.equal(flow.state.audioText.value, '请听词语“我的”，找出第2个字。')
  flow.state.heard.value = true
  flow.state.choose('的')
  flow.tick()
  assert.equal(flow.state.phase.value, 2)
  assert.equal(flow.state.isImage.value, false)
  assert.equal(flow.state.title.value, '听词找字')
  assert.equal(flow.state.needsAudio.value, true)
})

test('没有有效图片干扰项时使用听词找字，重复图片不进入图片题', () => {
  const flow = setup()
  flow.props.currentItem = { word: '星', image: 'star', exampleWord: '星星' }
  flow.props.items = [{ word: '星', image: 'star' }, { word: '闪', image: 'star' }]
  flow.reset()
  flow.state.phase.value = 2
  assert.equal(flow.state.isImage.value, false)
  const choices = characterChoices({ word: '星', image: 'star' }, [
    { word: '闪', image: 'star' }, { word: '地', image: 'ground', imageChoice: false },
    { word: '云', image: 'cloud' }, { word: '白', image: 'cloud' }], () => 0, true)
  assert.deepEqual(choices.map(item => item.image).sort(), ['cloud', 'star'])
})
