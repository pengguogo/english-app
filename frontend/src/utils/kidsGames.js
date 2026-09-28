export function shuffle(items, random = Math.random) {
  const result = [...items]
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(random() * (i + 1))
    ;[result[i], result[j]] = [result[j], result[i]]
  }
  return result
}

export function ticTacToeResult(board) {
  const lines = [[0, 1, 2], [3, 4, 5], [6, 7, 8], [0, 3, 6], [1, 4, 7], [2, 5, 8], [0, 4, 8], [2, 4, 6]]
  for (const [a, b, c] of lines) {
    if (board[a] && board[a] === board[b] && board[a] === board[c]) return board[a]
  }
  return board.every(Boolean) ? 'draw' : null
}

export function createPattern(round, random = Math.random) {
  const symbols = shuffle(['🌸', '🍎', '⭐', '🐟', '🍀', '🦋'], random).slice(0, 3)
  const cycles = [[0, 1], [0, 0, 1], [0, 1, 2]]
  const cycle = cycles[round % cycles.length]
  return {
    sequence: Array.from({ length: 6 }, (_, i) => symbols[cycle[i % cycle.length]]),
    answer: symbols[cycle[6 % cycle.length]],
    options: shuffle(symbols, random)
  }
}
