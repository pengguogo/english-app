import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs'
import { buildHomeTiles } from '../src/utils/homeTiles.js'

const subjects = [
  { id: 1, code: 'ENGLISH', name: '英语' },
  { id: 9, code: 'NEW', name: '新学科' }
]

test('学科排在前面并指向学科页', () => {
  const tiles = buildHomeTiles(subjects)
  assert.equal(tiles[0].to, '/subject/1')
  assert.equal(tiles[1].label, '新学科')
})

test('包含认字、绘本、游戏、3D、收集水果入口', () => {
  const keys = buildHomeTiles([]).map((t) => t.key)
  for (const k of ['character', 'picturebooks', 'games', 'models', 'fruit']) assert.ok(keys.includes(k), k)
})

test('每个宫格都有本地 JPG 且文件存在', () => {
  for (const tile of buildHomeTiles(subjects)) {
    assert.match(tile.image, /^\/images\/.+\.jpg$/)
    const file = new URL(`../../backend/src/main/resources/static${tile.image}`, import.meta.url)
    assert.ok(fs.existsSync(file), tile.image)
  }
})
