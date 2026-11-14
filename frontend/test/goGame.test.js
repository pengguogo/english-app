import test from 'node:test'
import assert from 'node:assert/strict'
import { newGame, play, pass, toggleDead, score } from '../src/utils/goGame.js'

function position(stones, current = 'black') {
  const game = newGame()
  for (const [index, color] of stones) game.board[index] = color
  game.current = current
  return game
}

test('提走失去最后一口气的整块棋子', () => {
  const game = position([[0, 'white'], [1, 'white'], [2, 'black'], [9, 'black']])
  const next = play(game, 10).game
  assert.equal(next.board[0], null)
  assert.equal(next.board[1], null)
})

test('拒绝自杀落子，但允许通过提子获得气', () => {
  assert.match(play(position([[1, 'white'], [9, 'white']]), 0).error, /没有气/)
  assert.equal(play(position([[0, 'white'], [1, 'black']]), 9).game.board[0], null)
})

test('禁止落子重复此前棋盘局面', () => {
  const game = position([[10, 'white'], [2, 'white'], [12, 'white'], [20, 'white'], [1, 'black'], [9, 'black'], [19, 'black']])
  game.positions = [game.board.map(x => x === 'black' ? 'b' : x === 'white' ? 'w' : '.').join('')]
  const after = play(game, 11).game
  assert.equal(after.board[10], null)
  assert.match(play(after, 10).error, /重复/)
})

test('连续停手进入死子确认，按整块切换并清除确认', () => {
  let game = pass(pass(position([[0, 'white'], [1, 'white']])))
  assert.equal(game.phase, 'mark')
  game = toggleDead({ ...game, confirmations: 1 }, 0)
  assert.deepEqual(game.dead.sort((a, b) => a - b), [0, 1])
  assert.equal(game.confirmations, 0)
  assert.deepEqual(toggleDead(game, 1).dead, [])
})

test('移除死子后计算活子、空地和贴目', () => {
  const game = position([[0, 'white'], [1, 'black'], [9, 'black']])
  game.dead = [0]
  const result = score(game)
  assert.equal(result.blackStones, 2)
  assert.equal(result.blackTerritory, 79)
  assert.equal(result.black, 81)
  assert.equal(result.white, 7.5)
  assert.equal(score(newGame()).neutral, 81)
})
