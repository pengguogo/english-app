import test from 'node:test'
import assert from 'node:assert/strict'
import { getQuestionTypeText } from '../src/utils/questionType.js'

test('所有后端课型都有中文名', () => {
  const types = ['WORD', 'SENTENCE', 'READING', 'QUIZ', 'CALCULATE', 'GAME', 'SPEAK', 'PHONICS', 'DIALOGUE']
  for (const type of types) {
    const text = getQuestionTypeText(type)
    assert.notEqual(text, '题目', type)
    assert.doesNotMatch(text, /[A-Z]/, type)
  }
  assert.equal(getQuestionTypeText('SENTENCE'), '句子')
})

test('未知或空类型显示"题目"', () => {
  assert.equal(getQuestionTypeText('UNKNOWN_X'), '题目')
  assert.equal(getQuestionTypeText(''), '题目')
  assert.equal(getQuestionTypeText(undefined), '题目')
})
