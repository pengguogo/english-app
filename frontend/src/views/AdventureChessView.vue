<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import GameAccessGate from '../components/games/GameAccessGate.vue'
import { useGameAccess } from '../composables/useGameAccess'

const LAST_CELL = 29
const events = {
  4: { icon: '🪜', text: '发现捷径，前进 3 格', move: 3 },
  8: { icon: '🕸️', text: '碰到蛛网，后退 2 格', move: -2 },
  13: { icon: '💎', text: '找到宝石，前进 2 格', move: 2 },
  18: { icon: '🌊', text: '渡河绕路，后退 3 格', move: -3 },
  23: { icon: '🦅', text: '乘上巨鹰，前进 4 格', move: 4 },
  26: { icon: '🪨', text: '落石挡路，后退 2 格', move: -2 }
}
const router = useRouter()
const accessState = useGameAccess()
const players = ref([{ name: '橙队', className: 'orange', position: 0 }, { name: '紫队', className: 'purple', position: 0 }])
const turn = ref(0)
const dice = ref(null)
const winner = ref(null)
const rolling = ref(false)
const notice = ref('橙队先出发！')
const cells = computed(() => Array.from({ length: 30 }, (_, visualIndex) => {
  const row = Math.floor(visualIndex / 6)
  const offset = visualIndex % 6
  return row % 2 === 0 ? row * 6 + offset : row * 6 + (5 - offset)
}))

function rollDice() {
  if (rolling.value || winner.value) return
  rolling.value = true
  dice.value = Math.floor(Math.random() * 6) + 1
  const player = players.value[turn.value]
  player.position = Math.min(LAST_CELL, player.position + dice.value)
  const event = events[player.position]
  if (event) player.position = Math.max(0, Math.min(LAST_CELL, player.position + event.move))
  notice.value = event ? `${player.name}：${event.text}` : `${player.name}前进 ${dice.value} 格`
  if (player.position === LAST_CELL) winner.value = player.name
  window.setTimeout(() => {
    if (!winner.value) turn.value = (turn.value + 1) % players.value.length
    rolling.value = false
  }, 450)
}

function restart() {
  players.value.forEach((player) => { player.position = 0 })
  turn.value = 0
  dice.value = null
  winner.value = null
  notice.value = '橙队先出发！'
}
</script>

<template>
  <main class="game-page">
    <BackBar title="丛林冒险棋" @back="router.push('/games')" />
    <GameAccessGate
      :access="accessState.access.value" :is-loading="accessState.isLoading.value"
      :error-msg="accessState.errorMsg.value" :progress-percent="accessState.progressPercent.value"
      @retry="accessState.refreshAccess" @learn="router.push('/')"
    >
      <section class="game-panel">
        <div class="toolbar"><strong>{{ winner ? `${winner}找到宝藏！` : `${players[turn].name}的回合` }}</strong><AppButton variant="ghost" @click="restart">重新开始</AppButton></div>
        <div class="adventure-board" aria-label="丛林冒险棋棋盘">
          <div v-for="cell in cells" :key="cell" class="adventure-cell" :class="{ event: events[cell] }">
            <small>{{ cell + 1 }}</small><span class="event-icon">{{ cell === LAST_CELL ? '🏰' : events[cell]?.icon }}</span>
            <div class="pieces"><span v-for="player in players.filter(p => p.position === cell)" :key="player.className" class="piece" :class="player.className">●</span></div>
          </div>
        </div>
        <div class="controls"><div class="dice" aria-live="polite">{{ dice || '—' }}</div><AppButton size="lg" :disabled="rolling || !!winner" @click="rollDice">掷骰前进</AppButton></div>
        <p class="notice" aria-live="polite">{{ notice }}</p>
        <p class="rule">两队轮流掷骰，跟随捷径、宝石和陷阱前进，率先到达宝藏城堡获胜。</p>
      </section>
    </GameAccessGate>
  </main>
</template>

<style scoped>
.game-page { min-height: 100dvh; padding: var(--space-4); background: var(--gradient-warm); }
.game-panel { padding: var(--space-4); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--space-3); color: var(--text-primary); }
.adventure-board { display: grid; grid-template-columns: repeat(6, 1fr); width: min(100%, 640px); margin: 0 auto; gap: var(--space-1); padding: var(--space-2); background: var(--game-jungle); border-radius: var(--radius-lg); }
.adventure-cell { position: relative; display: grid; place-items: center; aspect-ratio: 1; background: var(--bg-card); border: 2px solid var(--game-path); border-radius: var(--radius-sm); }
.adventure-cell.event { background: var(--game-event); }
.adventure-cell small { position: absolute; top: 2px; left: 4px; color: var(--text-tertiary); }
.event-icon { font-size: clamp(16px, 4vw, 28px); }
.pieces { position: absolute; right: 3px; bottom: 2px; display: flex; gap: 2px; }
.piece { font-size: clamp(16px, 4vw, 28px); text-shadow: var(--shadow-text); }
.piece.orange { color: var(--color-orange); }
.piece.purple { color: var(--subject-extracurricular); }
.controls { display: flex; align-items: center; justify-content: center; gap: var(--space-4); margin-top: var(--space-5); }
.dice { display: grid; place-items: center; width: 64px; height: 64px; background: var(--bg-card); color: var(--text-primary); border: 3px solid var(--color-success); border-radius: var(--radius-md); font-size: var(--text-xl); font-weight: var(--font-bold); }
.notice { min-height: 24px; margin-top: var(--space-3); text-align: center; color: var(--color-primary); font-weight: var(--font-medium); }
.rule { margin-top: var(--space-2); text-align: center; color: var(--text-secondary); font-size: var(--text-sm); }
@media (max-width: 480px) { .adventure-board { gap: 2px; padding: var(--space-1); } }
</style>
