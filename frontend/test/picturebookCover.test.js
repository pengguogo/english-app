import test from 'node:test'
import assert from 'node:assert/strict'
import { picturebookCover } from '../src/utils/picturebookCover.js'

test('书架把迁移中的图片 key 转成可访问地址', () => {
  assert.equal(picturebookCover({ items: [
    { image: 'picturebooks/train-station' },
    { image: 'story/ocean-station-01' },
  ] }), '/images/story/ocean-station-01.jpg')
})

test('书架保留详情接口已经解析的图片地址', () => {
  assert.equal(picturebookCover({ items: [{ image: '/images/story/forest-mail-01.jpg' }] }), '/images/story/forest-mail-01.jpg')
  assert.equal(picturebookCover({ items: [] }), '/images/picturebooks/train-station.jpg')
})
