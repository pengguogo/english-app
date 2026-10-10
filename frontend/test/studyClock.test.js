import test from 'node:test'
import assert from 'node:assert/strict'
import { createStudyClock, createStudyQueue } from '../src/utils/studyClock.js'

test('隐藏页面不计时，离页结算不足15秒的尾段', () => {
  const clock = createStudyClock(0)
  clock.setActive(true, 0)
  clock.setActive(false, 8500)
  assert.equal(clock.take(30000), 8)
  clock.setActive(true, 30000)
  assert.equal(clock.take(30500), 1)
})

test('60秒无操作后暂停，恢复操作不会补计闲置时间', () => {
  const clock = createStudyClock(0)
  clock.setActive(true, 0)
  assert.equal(clock.take(90000), 60)
  clock.interact(100000)
  assert.equal(clock.take(115000), 15)
  assert.equal(clock.take(200000), 45)
})

test('网络失败保留同一事件ID重试，多条时长顺序发送且每条不超过30秒', async () => {
  let id = 0
  const requests = []
  let fail = true
  const queue = createStudyQueue(async request => {
    requests.push({ ...request })
    if (fail) { fail = false; throw Error('断网') }
  }, () => String(++id))
  await queue.add(7, 45)
  await queue.add(8, 8)
  assert.deepEqual(requests.map(r => r.eventId), ['1', '1', '2', '3'])
  assert.deepEqual(requests.map(r => r.seconds), [30, 30, 15, 8])
  assert.deepEqual(requests.map(r => r.lessonId), [7, 7, 7, 8])
})
