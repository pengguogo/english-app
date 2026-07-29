import assert from 'node:assert/strict'
import test from 'node:test'
import { findNextReadingLesson } from '../src/utils/continuousPlayback.js'

const units = [{ id: 10 }, { id: 20 }]
const lessonsByUnit = {
  10: [
    { id: 101, type: 'READING' },
    { id: 102, type: 'READING' },
    { id: 103, type: 'QUIZ' }
  ],
  20: [
    { id: 201, type: 'READING' },
    { id: 202, type: 'QUIZ' }
  ]
}
const loadLessons = async unitId => lessonsByUnit[unitId]

test('同一单元存在后续阅读课时应继续播放', async () => {
  const result = await findNextReadingLesson(101, 10, units, loadLessons)

  assert.deepEqual(result, {
    lesson: { id: 102, type: 'READING' },
    unitId: 10
  })
})

test('当前单元结束后应跳过问答并进入下一单元', async () => {
  const result = await findNextReadingLesson(102, 10, units, loadLessons)

  assert.deepEqual(result, {
    lesson: { id: 201, type: 'READING' },
    unitId: 20
  })
})

test('主题内没有后续阅读课时应返回空', async () => {
  const result = await findNextReadingLesson(201, 20, units, loadLessons)

  assert.equal(result, null)
})

test('当前课时不在单元列表中应停止续播', async () => {
  const result = await findNextReadingLesson(999, 10, units, loadLessons)

  assert.equal(result, null)
})
