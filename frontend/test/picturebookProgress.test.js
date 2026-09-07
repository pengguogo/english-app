import test from 'node:test'
import assert from 'node:assert/strict'
import { readBookmark, writeBookmark, nextUnfinished, quizScore } from '../src/utils/picturebookProgress.js'

const lessons = [{ id: 72, content: { items: [{}, {}, {}] } }, { id: 73, content: { items: [{}] } }]
const storage = (value) => ({ getItem: () => value })

test('恢复同一绘本的页码和已答题成绩，内容缩短后页码仍有效', () => {
  assert.deepEqual(readBookmark(storage(JSON.stringify({ lessonId: 72, index: 20, scores: [100, 70, 500] })), 1, lessons), { lessonId: 72, index: 2, scores: [100, 70, 0] })
})
test('书签损坏、课程删除、浏览器禁用存储时可以重新出发', () => {
  assert.equal(readBookmark(storage('{bad'), 1, lessons), null)
  assert.equal(readBookmark(storage('{"lessonId":99,"index":0}'), 1, lessons), null)
  assert.equal(readBookmark({ getItem() { throw new Error() } }, 1, lessons), null)
  assert.equal(writeBookmark({ setItem() { throw new Error() } }, 1, 72, 0), false)
})
test('书签按绘本隔离，重做成绩不会替代服务端完成状态', () => {
  let saved
  assert.equal(writeBookmark({ setItem(key, value) { saved = { key, value } } }, 8, 72, 1, [100]), true)
  assert.equal(saved.key, 'picturebook:8')
  assert.equal(JSON.parse(saved.value).index, 1)
  assert.equal(nextUnfinished(lessons, [72]), 1)
  assert.equal(nextUnfinished(lessons, [72, 73]), -1)
})
test('问答按总题数计算，未作答不能获得满分', () => {
  assert.equal(quizScore([100, 70], 3), 57)
  assert.equal(quizScore([], 0), 0)
})
