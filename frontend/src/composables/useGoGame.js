import { computed, ref } from 'vue'
import tenuki from 'tenuki'

const SIZE = 9

function createEngine() {
  return new tenuki.Game({
    boardSize: SIZE,
    scoring: 'area',
    koRule: 'superko',
    komi: 0,
  })
}

export function useGoGame() {
  const engine = ref(createEngine())
  const revision = ref(0)
  const message = ref('黑方先行')

  const intersections = computed(() => {
    revision.value
    return engine.value.intersections()
  })
  const board = computed(() => intersections.value.map((point) => (
    point.isBlack() ? 'black' : point.isWhite() ? 'white' : null
  )))
  const current = computed(() => engine.value.currentPlayer())
  const ended = computed(() => engine.value.isOver())
  const score = computed(() => {
    revision.value
    if (engine.value.isOver()) return engine.value.score()
    return {
      black: intersections.value.filter((point) => point.isBlack()).length,
      white: intersections.value.filter((point) => point.isWhite()).length,
    }
  })

  function play(index) {
    const y = Math.floor(index / SIZE)
    const x = index % SIZE
    if (engine.value.isOver()) {
      engine.value.toggleDeadAt(y, x, { render: false })
      message.value = '终局计分：点击棋子可切换死活状态'
      revision.value += 1
      return
    }
    if (!engine.value.playAt(y, x, { render: false })) {
      message.value = '这里不能落子（禁入点、打劫或已有棋子）'
      return
    }
    message.value = `${engine.value.currentPlayer() === 'black' ? '黑' : '白'}方落子`
    revision.value += 1
  }

  function pass() {
    if (engine.value.isOver()) return
    engine.value.pass({ render: false })
    message.value = engine.value.isOver()
      ? '双方连续停一手，点击棋子标记死活后查看数目结果'
      : `${engine.value.currentPlayer() === 'black' ? '黑' : '白'}方落子`
    revision.value += 1
  }

  function undo() {
    if (engine.value.moveNumber() === 0) return
    engine.value.undo({ render: false })
    message.value = `已悔棋，${engine.value.currentPlayer() === 'black' ? '黑' : '白'}方落子`
    revision.value += 1
  }

  function restart() {
    engine.value = createEngine()
    message.value = '黑方先行'
    revision.value += 1
  }

  return { board, current, ended, score, message, play, pass, undo, restart }
}
