<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import GameAccessGate from '../components/games/GameAccessGate.vue'
import { useGameAccess } from '../composables/useGameAccess'

const SIZE = 15
const router = useRouter()
const accessState = useGameAccess()
const board = ref(Array(SIZE * SIZE).fill(null))
const current = ref('black')
const winner = ref(null)
const moves = ref(0)
const statusText = computed(() => winner.value
  ? `${winner.value === 'black' ? '黑方' : '白方'}获胜！`
  : moves.value === SIZE * SIZE ? '和棋！' : `${current.value === 'black' ? '黑方' : '白方'}落子`)

function placeStone(index) {
  if (board.value[index] || winner.value) return
  board.value[index] = current.value
  moves.value++
  if (hasFive(index, current.value)) winner.value = current.value
  else current.value = current.value === 'black' ? 'white' : 'black'
}

function hasFive(index, color) {
  const row = Math.floor(index / SIZE)
  const col = index % SIZE
  return [[1, 0], [0, 1], [1, 1], [1, -1]].some(([dr, dc]) =>
    1 + count(row, col, dr, dc, color) + count(row, col, -dr, -dc, color) >= 5)
}

function count(row, col, dr, dc, color) {
  let total = 0
  for (let r = row + dr, c = col + dc; r >= 0 && r < SIZE && c >= 0 && c < SIZE; r += dr, c += dc) {
    if (board.value[r * SIZE + c] !== color) break
    total++
  }
  return total
}

function restart() {
  board.value = Array(SIZE * SIZE).fill(null)
  current.value = 'black'
  winner.value = null
  moves.value = 0
}
</script>

<template>
  <main class="game-page">
    <BackBar title="五子棋" @back="router.push('/games')" />
    <GameAccessGate
      :access="accessState.access.value" :is-loading="accessState.isLoading.value"
      :error-msg="accessState.errorMsg.value" :progress-percent="accessState.progressPercent.value"
      @retry="accessState.refreshAccess" @learn="router.push('/')"
    >
      <section class="game-panel">
        <div class="toolbar"><strong>{{ statusText }}</strong><AppButton variant="ghost" @click="restart">重新开始</AppButton></div>
        <div class="board" role="grid" aria-label="十五路五子棋棋盘">
          <button v-for="(_, index) in board" :key="index" class="cell" type="button" role="gridcell" :aria-label="`第 ${Math.floor(index / SIZE) + 1} 行第 ${index % SIZE + 1} 列`" @click="placeStone(index)">
            <span v-if="board[index]" class="stone" :class="board[index]"></span>
          </button>
        </div>
        <p class="rule">两人轮流落子，横、竖或斜线率先连成五颗棋子的一方获胜。</p>
      </section>
    </GameAccessGate>
  </main>
</template>

<style scoped>
.game-page { min-height: 100dvh; padding: var(--space-4); background: var(--gradient-warm); }
.game-panel { padding: var(--space-4); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--space-3); color: var(--text-primary); }
.board { display: grid; grid-template-columns: repeat(15, 1fr); width: min(100%, 620px); aspect-ratio: 1; margin: 0 auto; padding: var(--space-3); background: var(--game-board); border: 3px solid var(--game-board-edge); border-radius: var(--radius-sm); }
.cell { position: relative; min-width: 0; background-image: linear-gradient(var(--game-grid), var(--game-grid)), linear-gradient(90deg, var(--game-grid), var(--game-grid)); background-size: 100% 1px, 1px 100%; background-position: center; background-repeat: no-repeat; }
.stone { position: absolute; inset: 10%; border-radius: var(--radius-pill); box-shadow: var(--shadow-soft); }
.stone.black { background: var(--game-stone-black); }
.stone.white { background: var(--game-stone-white); border: 1px solid var(--border-light); }
.rule { margin-top: var(--space-4); text-align: center; color: var(--text-secondary); font-size: var(--text-sm); }
</style>
