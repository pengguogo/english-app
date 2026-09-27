<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import GameAccessGate from '../components/games/GameAccessGate.vue'
import { useGameAccess } from '../composables/useGameAccess'

const TRACK_LENGTH = 20
const track = Array.from({ length: 36 }, () => null)
const path = [0,1,2,3,4,5,11,17,23,29,35,34,33,32,31,30,24,18,12,6]
const router = useRouter()
const accessState = useGameAccess()
const players = ref([{ name: '红队', color: 'red', position: -1 }, { name: '蓝队', color: 'blue', position: -1 }])
const turn = ref(0)
const dice = ref(null)
const winner = ref(null)
const rolling = ref(false)
const message = computed(() => winner.value ? `${winner.value}抵达终点！` : `${players.value[turn.value].name}的回合`)

function piecesAt(cellIndex) {
  return players.value.filter((player) => player.position >= 0 && player.position < TRACK_LENGTH && path[player.position] === cellIndex)
}

function rollDice() {
  if (rolling.value || winner.value) return
  rolling.value = true
  dice.value = Math.floor(Math.random() * 6) + 1
  const player = players.value[turn.value]
  if (player.position === -1) {
    if (dice.value === 6) player.position = 0
  } else {
    player.position = Math.min(TRACK_LENGTH, player.position + dice.value)
    if (player.position === TRACK_LENGTH) winner.value = player.name
  }
  window.setTimeout(() => {
    if (!winner.value) turn.value = (turn.value + 1) % players.value.length
    rolling.value = false
  }, 350)
}

function restart() {
  players.value.forEach((player) => { player.position = -1 })
  turn.value = 0
  dice.value = null
  winner.value = null
}
</script>

<template>
  <main class="game-page">
    <BackBar title="飞行棋" @back="router.push('/games')" />
    <GameAccessGate
      :access="accessState.access.value" :is-loading="accessState.isLoading.value"
      :error-msg="accessState.errorMsg.value" :progress-percent="accessState.progressPercent.value"
      @retry="accessState.refreshAccess" @learn="router.push('/')"
    >
      <section class="game-panel">
        <div class="toolbar"><strong>{{ message }}</strong><AppButton variant="ghost" @click="restart">重新开始</AppButton></div>
        <div class="race-board" aria-label="飞行棋跑道">
          <div v-for="(_, cell) in track" :key="cell" class="track-cell" :class="{ active: path.includes(cell) }">
            <span v-if="cell === path[0]" class="label">起</span>
            <span v-if="cell === path[TRACK_LENGTH - 1]" class="label">终</span>
            <span v-for="piece in piecesAt(cell)" :key="piece.color" class="plane" :class="piece.color">✈</span>
          </div>
          <div class="center"><span>🏁</span><small>飞行赛道</small></div>
        </div>
        <div class="controls">
          <div class="hangar"><span v-for="player in players.filter(p => p.position === -1)" :key="player.color" class="plane" :class="player.color">✈</span><small>掷到 6 才能起飞</small></div>
          <div class="dice" aria-live="polite">{{ dice || '—' }}</div>
          <AppButton size="lg" :disabled="rolling || !!winner" @click="rollDice">掷骰子</AppButton>
        </div>
        <p class="rule">两人轮流掷骰子，掷到 6 起飞，率先飞完一圈到达终点的一方获胜。</p>
      </section>
    </GameAccessGate>
  </main>
</template>

<style scoped>
.game-page { min-height: 100dvh; padding: var(--space-4); background: var(--gradient-warm); }
.game-panel { padding: var(--space-4); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--space-3); color: var(--text-primary); }
.race-board { position: relative; display: grid; grid-template-columns: repeat(6, 1fr); width: min(100%, 560px); aspect-ratio: 1; margin: 0 auto; gap: var(--space-1); padding: var(--space-2); background: var(--game-sky); border-radius: var(--radius-lg); }
.track-cell { display: flex; align-items: center; justify-content: center; gap: 2px; min-width: 0; border-radius: var(--radius-sm); }
.track-cell.active { background: var(--bg-card); border: 2px solid var(--game-track); }
.label { color: var(--text-secondary); font-weight: var(--font-bold); }
.center { position: absolute; inset: 34%; display: flex; flex-direction: column; align-items: center; justify-content: center; background: var(--color-accent); border-radius: var(--radius-pill); color: var(--text-primary); }
.center span { font-size: 36px; }
.plane { font-size: clamp(16px, 4vw, 30px); line-height: 1; }
.plane.red { color: var(--game-red); }
.plane.blue { color: var(--color-primary); }
.controls { display: flex; align-items: center; justify-content: center; flex-wrap: wrap; gap: var(--space-4); margin-top: var(--space-5); }
.hangar { display: flex; align-items: center; gap: var(--space-2); color: var(--text-tertiary); }
.dice { display: grid; place-items: center; width: 64px; height: 64px; background: var(--bg-card); color: var(--text-primary); border: 3px solid var(--color-primary); border-radius: var(--radius-md); font-size: var(--text-xl); font-weight: var(--font-bold); }
.rule { margin-top: var(--space-4); text-align: center; color: var(--text-secondary); font-size: var(--text-sm); }
</style>
