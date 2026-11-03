import test from 'node:test'
import assert from 'node:assert/strict'
import { getRecorderStartErrorMessage } from '../src/utils/recorderError.js'

test('非安全上下文提示使用 HTTPS', () => {
  assert.equal(
    getRecorderStartErrorMessage(new TypeError(), { secureContext: false, getUserMedia: false }),
    '跟读功能需要通过 HTTPS 打开'
  )
})

test('拒绝权限时提示修改浏览器设置', () => {
  assert.equal(
    getRecorderStartErrorMessage({ name: 'NotAllowedError' }, { secureContext: true, getUserMedia: true }),
    '请在浏览器设置中允许麦克风权限'
  )
})

test('没有设备与设备占用时给出对应提示', () => {
  assert.equal(
    getRecorderStartErrorMessage({ name: 'NotFoundError' }, { secureContext: true, getUserMedia: true }),
    '没有检测到可用麦克风'
  )
  assert.equal(
    getRecorderStartErrorMessage({ name: 'NotReadableError' }, { secureContext: true, getUserMedia: true }),
    '麦克风正被其他应用占用，请关闭后重试'
  )
})
