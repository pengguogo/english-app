import test from 'node:test'
import assert from 'node:assert/strict'
import { formatPhonetic } from '../src/utils/phonetic.js'

test('数据已带斜杠时只保留一层', () => {
  assert.equal(formatPhonetic('/ef/'), '/ef/')
  assert.equal(formatPhonetic('//fruːt//'), '/fruːt/')
})

test('数据不带斜杠时补一层', () => {
  assert.equal(formatPhonetic('f'), '/f/')
  assert.equal(formatPhonetic(' f '), '/f/')
})

test('空值返回空串', () => {
  assert.equal(formatPhonetic(''), '')
  assert.equal(formatPhonetic(null), '')
  assert.equal(formatPhonetic('//'), '')
})
