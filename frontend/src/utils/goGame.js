export const SIZE = 9
export const KOMI = 7.5
const opposite = color => color === 'black' ? 'white' : 'black'
const key = board => board.map(value => value === 'black' ? 'b' : value === 'white' ? 'w' : '.').join('')

function neighbors(index) {
  const row = Math.floor(index / SIZE), col = index % SIZE
  return [row > 0 ? index - SIZE : -1, row < SIZE - 1 ? index + SIZE : -1,
    col > 0 ? index - 1 : -1, col < SIZE - 1 ? index + 1 : -1].filter(i => i >= 0)
}

export function group(board, index) {
  const color = board[index], found = new Set([index]), queue = [index], liberties = new Set()
  for (const point of queue) for (const next of neighbors(point)) {
    if (!board[next]) liberties.add(next)
    else if (board[next] === color && !found.has(next)) { found.add(next); queue.push(next) }
  }
  return { stones: [...found], liberties: liberties.size }
}

export function newGame() {
  const board = Array(SIZE * SIZE).fill(null)
  return { board, current: 'black', passes: 0, phase: 'play', dead: [], confirmations: 0, history: [], positions: [key(board)] }
}

export function play(game, index) {
  if (game.phase !== 'play' || index < 0 || index >= SIZE * SIZE || game.board[index]) return { game, error: '这里不能落子。' }
  const board = [...game.board], color = game.current
  board[index] = color
  for (const next of neighbors(index)) if (board[next] === opposite(color)) {
    const chain = group(board, next)
    if (!chain.liberties) chain.stones.forEach(point => { board[point] = null })
  }
  if (!group(board, index).liberties) return { game, error: '这里没有气，不能自杀落子。' }
  if (game.positions.includes(key(board))) return { game, error: '不能重复此前的棋盘局面，请先在别处落子。' }
  return { game: { ...game, board, current: opposite(color), passes: 0, history: [...game.history, game], positions: [...game.positions, key(board)] }, error: '' }
}

export function pass(game) {
  if (game.phase !== 'play') return game
  const passes = game.passes + 1
  return { ...game, current: opposite(game.current), passes, phase: passes === 2 ? 'mark' : 'play', dead: [], confirmations: 0, history: [...game.history, game] }
}

export function toggleDead(game, index) {
  if (game.phase !== 'mark' || !game.board[index]) return game
  const stones = group(game.board, index).stones, dead = new Set(game.dead), remove = dead.has(index)
  stones.forEach(point => remove ? dead.delete(point) : dead.add(point))
  return { ...game, dead: [...dead], confirmations: 0 }
}

export function score(game) {
  const board = [...game.board]
  game.dead.forEach(index => { board[index] = null })
  const result = { black: 0, white: KOMI, blackStones: 0, whiteStones: 0, blackTerritory: 0, whiteTerritory: 0, neutral: 0 }
  const visited = new Set()
  board.forEach((color, index) => {
    if (color) { result[color]++; result[`${color}Stones`]++; return }
    if (visited.has(index)) return
    const queue = [index], borders = new Set(); visited.add(index)
    for (const point of queue) for (const next of neighbors(point)) {
      if (board[next]) borders.add(board[next])
      else if (!visited.has(next)) { visited.add(next); queue.push(next) }
    }
    if (borders.size === 1) {
      const owner = [...borders][0]; result[owner] += queue.length; result[`${owner}Territory`] += queue.length
    } else result.neutral += queue.length
  })
  return result
}
