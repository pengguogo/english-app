import test from 'node:test'
import assert from 'node:assert/strict'
import { createPattern, shuffle, ticTacToeResult } from '../src/utils/kidsGames.js'

test('洗牌保留所有配对，且不修改原数组', () => {
  const source = ['猫', '猫', '狗', '狗']
  const result = shuffle(source, () => 0)
  assert.deepEqual([...result].sort(), [...source].sort())
  assert.deepEqual(source, ['猫', '猫', '狗', '狗'])
  assert.notDeepEqual(result, source)
})

test('井字棋识别全部八条获胜线和两种棋子', () => {
  for (const line of [[0, 1, 2], [3, 4, 5], [6, 7, 8], [0, 3, 6], [1, 4, 7], [2, 5, 8], [0, 4, 8], [2, 4, 6]]) {
    for (const player of ['⭕', '❌']) {
      const board = Array(9).fill(null)
      line.forEach(index => { board[index] = player })
      assert.equal(ticTacToeResult(board), player)
    }
  }
})

test('井字棋区分未结束、平局和最后一格获胜', () => {
  assert.equal(ticTacToeResult(Array(9).fill(null)), null)
  assert.equal(ticTacToeResult(['⭕', '❌', '⭕', '⭕', '❌', '❌', '❌', '⭕', '⭕']), 'draw')
  assert.equal(ticTacToeResult(['⭕', '❌', '⭕', '❌', '⭕', '❌', '❌', '⭕', '⭕']), '⭕')
})

test('三种图案规律提供唯一且正确的下一项', () => {
  for (let round = 0; round < 30; round++) {
    const { sequence, options, answer } = createPattern(round)
    assert.equal(sequence.length, 6)
    assert.equal(new Set(options).size, 3)
    assert.equal(options.filter(value => value === answer).length, 1)
    const cycleLength = round % 3 === 0 ? 2 : 3
    assert.equal(answer, sequence[6 % cycleLength])
    sequence.forEach((value, index) => assert.equal(value, sequence[index % cycleLength]))
  }
})
