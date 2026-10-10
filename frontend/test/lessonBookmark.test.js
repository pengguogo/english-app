import test from 'node:test'
import assert from 'node:assert/strict'
import { readLessonBookmark, saveLessonBookmark, clearLessonBookmark } from '../src/utils/lessonBookmark.js'

function storage() {
  const values = new Map()
  return { getItem: key => values.get(key) ?? null, setItem: (key, value) => values.set(key, value), removeItem: key => values.delete(key) }
}
const lesson = { id: 1, name: '水果', content: { items: [{ word: 'apple' }, { word: 'pear' }] } }

test('恢复学习位置、成绩和已参与状态，完成后清除续学入口', () => {
  const store = storage()
  saveLessonBookmark(store, lesson, 1, [80, 70], [true, false], { themeId: '1' })
  assert.deepEqual(readLessonBookmark(store, lesson), { index: 1, scores: [80, 70], engaged: [true, false] })
  assert.equal(JSON.parse(store.getItem('lastLesson')).query.themeId, '1')
  clearLessonBookmark(store, lesson.id)
  assert.equal(readLessonBookmark(store, lesson), null)
  assert.equal(store.getItem('lastLesson'), null)
})

test('内容更新丢弃旧草稿，不同课程的完成不会删除当前续学入口', () => {
  const store = storage()
  saveLessonBookmark(store, lesson, 0, [], [], {})
  assert.equal(readLessonBookmark(store, { ...lesson, content: { items: [{ word: 'banana' }] } }), null)
  clearLessonBookmark(store, 2)
  assert.equal(JSON.parse(store.getItem('lastLesson')).id, 1)
})

test('损坏存储、禁用存储和无效成绩不会阻断学习', () => {
  const store = storage()
  saveLessonBookmark(store, lesson, 99, [-1, 101], [true], {})
  assert.deepEqual(readLessonBookmark(store, lesson), { index: 1, scores: [0, 0], engaged: [true, false] })
  store.setItem('lesson:1', '{broken')
  assert.equal(readLessonBookmark(store, lesson), null)
  const disabled = { getItem() { throw Error() }, setItem() { throw Error() }, removeItem() { throw Error() } }
  assert.equal(readLessonBookmark(disabled, lesson), null)
  assert.doesNotThrow(() => saveLessonBookmark(disabled, lesson, 0, [], [], {}))
})


test('未获得有效评测的学习项恢复后仍为空，不变成零分', () => {
  const store = storage()
  const spoken = { ...lesson, type: 'WORD' }
  saveLessonBookmark(store, spoken, 1, [80, null], [true, true], {})
  assert.deepEqual(readLessonBookmark(store, spoken).scores, [80, null])
})
