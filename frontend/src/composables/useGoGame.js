import { computed, ref } from 'vue'

const SIZE = 9
const EMPTY_BOARD = () => Array(SIZE * SIZE).fill(null)

/** 9 路围棋规则：提子、禁入点、简易劫争与连续停一手结束。 */
export function useGoGame() {
  const board = ref(EMPTY_BOARD())
  const current = ref('black')
  const captured = ref({ black: 0, white: 0 })
  const previousBoard = ref('')
  const passes = ref(0)
  const ended = ref(false)
  const message = ref('黑方先行')
  const score = computed(() => ({
    black: board.value.filter((stone) => stone === 'black').length + captured.value.black,
    white: board.value.filter((stone) => stone === 'white').length + captured.value.white
  }))

  function play(index) {
    if (ended.value || board.value[index]) return
    const next = [...board.value]
    next[index] = current.value
    const opponent = current.value === 'black' ? 'white' : 'black'
    let removed = 0
    neighbors(index).forEach((neighbor) => {
      if (next[neighbor] !== opponent) return
      const group = collectGroup(next, neighbor)
      if (!hasLiberty(next, group)) {
        group.forEach((point) => { next[point] = null })
        removed += group.length
      }
    })
    if (!hasLiberty(next, collectGroup(next, index))) {
      message.value = '这里没有气，不能落子'
      return
    }
    if (next.join(',') === previousBoard.value) {
      message.value = '不能立即提回，换个地方吧'
      return
    }
    previousBoard.value = board.value.join(',')
    board.value = next
    captured.value[current.value] += removed
    passes.value = 0
    current.value = opponent
    message.value = `${opponent === 'black' ? '黑方' : '白方'}落子`
  }

  function pass() {
    if (ended.value) return
    passes.value++
    if (passes.value >= 2) {
      ended.value = true
      message.value = score.value.black === score.value.white
        ? '双方平局' : `${score.value.black > score.value.white ? '黑方' : '白方'}暂时领先`
      return
    }
    current.value = current.value === 'black' ? 'white' : 'black'
    message.value = `${current.value === 'black' ? '黑方' : '白方'}落子`
  }

  function restart() {
    board.value = EMPTY_BOARD()
    current.value = 'black'
    captured.value = { black: 0, white: 0 }
    previousBoard.value = ''
    passes.value = 0
    ended.value = false
    message.value = '黑方先行'
  }

  return { SIZE, board, current, captured, ended, message, score, play, pass, restart }
}

function neighbors(index) {
  const row = Math.floor(index / SIZE)
  const col = index % SIZE
  return [[row - 1, col], [row + 1, col], [row, col - 1], [row, col + 1]]
    .filter(([r, c]) => r >= 0 && r < SIZE && c >= 0 && c < SIZE)
    .map(([r, c]) => r * SIZE + c)
}

function collectGroup(board, start) {
  const color = board[start]
  const group = new Set([start])
  const queue = [start]
  while (queue.length) {
    neighbors(queue.pop()).forEach((point) => {
      if (board[point] === color && !group.has(point)) {
        group.add(point)
        queue.push(point)
      }
    })
  }
  return [...group]
}

function hasLiberty(board, group) {
  return group.some((point) => neighbors(point).some((neighbor) => !board[neighbor]))
}
