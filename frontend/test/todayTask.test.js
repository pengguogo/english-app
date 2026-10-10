import test from 'node:test'
import assert from 'node:assert/strict'
import { chooseTodayTask } from '../src/utils/todayTask.js'

test('本机有效续学优先于错题建议，损坏或无草稿时采用服务端建议', () => {
  const suggested = { title: '重练', path: '/wrong-answers/1/practice' }
  const values = new Map([['lastLesson', JSON.stringify({ id: 7, name: '水果', query: { themeId: '1' } })], ['lesson:7', '{}']])
  const storage = { getItem: key => values.get(key) }
  assert.equal(chooseTodayTask(storage, suggested).path, '/lesson/7')
  values.delete('lesson:7')
  assert.equal(chooseTodayTask(storage, suggested), suggested)
  values.set('lastLesson', '{broken')
  assert.equal(chooseTodayTask(storage, suggested), suggested)
  assert.equal(chooseTodayTask({ getItem() { throw Error() } }, suggested), suggested)
})
