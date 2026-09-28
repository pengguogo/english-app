import test from 'node:test'
import assert from 'node:assert/strict'
import { createPicturePieces, hintPicturePieces, swapPicturePieces } from '../src/utils/picturePuzzle.js'

test('每种难度保留所有图片块且初始不通关', () => {
  for (const size of [2, 3, 4]) {
    for (const random of [() => 0, () => 0.99999, Math.random]) {
      const pieces = createPicturePieces(size, random)
      assert.deepEqual([...pieces].sort((a, b) => a - b), Array.from({ length: size * size }, (_, i) => i))
      assert.equal(pieces.every((value, index) => value === index), false)
    }
  }
})

test('交换不改变源数组，交换两次恢复', () => {
  const pieces = [2, 0, 3, 1]
  const swapped = swapPicturePieces(pieces, 0, 3)
  assert.deepEqual(pieces, [2, 0, 3, 1])
  assert.deepEqual(swapped, [1, 0, 3, 2])
  assert.deepEqual(swapPicturePieces(swapped, 0, 3), pieces)
})

test('提示不破坏已归位图块，并能在有限次内通关', () => {
  for (const size of [2, 3, 4]) {
    let pieces = createPicturePieces(size)
    for (let step = 0; step < size * size; step++) {
      const next = hintPicturePieces(pieces)
      pieces.forEach((value, index) => { if (value === index) assert.equal(next[index], index) })
      pieces = next
    }
    assert.equal(pieces.every((value, index) => value === index), true)
    assert.deepEqual(hintPicturePieces(pieces), pieces)
  }
})
