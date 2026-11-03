import test from 'node:test'
import assert from 'node:assert/strict'
import { buildThemeDestination } from '../src/utils/lessonNav.js'

test('有 themeId 时回到主题页并保留展示参数', () => {
  const dest = buildThemeDestination({ unitId: '1', themeId: '1', themeName: '水果乐园', subjectId: 1, subjectName: '英语' })
  assert.deepEqual(dest, { path: '/theme/1', query: { themeName: '水果乐园', subjectId: '1', subjectName: '英语' } })
})

test('不会跳到单元页，也不带 unitId', () => {
  const dest = buildThemeDestination({ unitId: '3', themeId: '2' })
  assert.equal(dest.path, '/theme/2')
  assert.equal(dest.query.unitId, undefined)
})

test('没有主题信息时回首页', () => {
  assert.deepEqual(buildThemeDestination({ unitId: '3' }), { path: '/' })
  assert.deepEqual(buildThemeDestination(), { path: '/' })
})
