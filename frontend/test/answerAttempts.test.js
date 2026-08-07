import test from 'node:test'
import assert from 'node:assert/strict'
import { evaluateAnswerAttempt } from '../src/utils/answerAttempts.js'

test('首次答对按独立掌握计 100 分', () => {
  assert.deepEqual(evaluateAnswerAttempt(1, true), {
    complete: true,
    assisted: false,
    score: 100,
    status: 'correct'
  })
})

test('首次答错允许重试且不结束题目', () => {
  assert.deepEqual(evaluateAnswerAttempt(1, false), {
    complete: false,
    assisted: false,
    score: 0,
    status: 'retry'
  })
})

test('提示后答对按辅助掌握计 70 分', () => {
  assert.deepEqual(evaluateAnswerAttempt(2, true), {
    complete: true,
    assisted: true,
    score: 70,
    status: 'assisted-correct'
  })
})

test('第二次仍答错时结束并揭示答案', () => {
  assert.deepEqual(evaluateAnswerAttempt(2, false), {
    complete: true,
    assisted: true,
    score: 0,
    status: 'revealed'
  })
})
