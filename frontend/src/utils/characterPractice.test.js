import test from 'node:test'
import assert from 'node:assert/strict'
import { characterChoices, recognitionOutcome } from './characterPractice.js'

test('选项包含唯一目标并去重，答案不固定在首位', () => {
  const target = { word: '山' }
  const pool = [target, { word: '水' }, { word: '水' }, { word: '日' }, { word: '月' }]
  const choices = characterChoices(target, pool, () => 0)
  assert.equal(choices.length, 3)
  assert.equal(new Set(choices.map(i => i.word)).size, 3)
  assert.notEqual(choices[0].word, '山')
  assert.equal(choices.filter(i => i.word === '山').length, 1)
})
test('首次错误不会被后来答对覆盖，提示结果独立记录', () => {
  assert.equal(recognitionOutcome(true, true), 'WRONG')
  assert.equal(recognitionOutcome(true, false), 'WRONG')
  assert.equal(recognitionOutcome(false, true), 'ASSISTED')
  assert.equal(recognitionOutcome(false, false), 'INDEPENDENT')
})
