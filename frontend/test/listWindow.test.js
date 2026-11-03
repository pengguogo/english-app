import test from 'node:test'
import assert from 'node:assert/strict'
import { getNextVisibleCount, getVisibleItems } from '../src/utils/listWindow.js'

test('长列表只返回当前需要展示的项目', () => {
  assert.deepEqual(getVisibleItems([1, 2, 3, 4], 2), [1, 2])
})

test('加载更多不会超过列表总数', () => {
  assert.equal(getNextVisibleCount(20, 114), 40)
  assert.equal(getNextVisibleCount(100, 114), 114)
})
