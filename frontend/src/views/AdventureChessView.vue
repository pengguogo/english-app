<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import GameAccessGate from '../components/games/GameAccessGate.vue'
import { useGameAccess } from '../composables/useGameAccess'

const LAST_CELL = 29
const events = {
  3: { icon: '🪜', text: '爬上古藤梯，飞跃 6 格', move: 6 },
  7: { icon: '🐍', text: '惊动树蛇，退回 4 格', move: -4 },
  12: { icon: '🌿', text: '抓住摆荡藤蔓，前进 5 格', move: 5 },
  17: { icon: '🌊', text: '被急流冲走，后退 5 格', move: -5 },
  22: { icon: '🦅', text: '乘上巨鹰，飞跃 4 格', move: 4 },
  27: { icon: '🕳️', text: '误入隐秘洞穴，后退 3 格', move: -3 },
}
const router = useRouter()
const accessState = useGameAccess()
const players = ref([
  { name: '狐狸队', avatar: '🦊', position: 0 },
  { name: '熊熊队', avatar: '🐻', position: 0 },
])
const turn = ref(0)
const dice = ref(null)
const winner = ref(null)
const rolling = ref(false)
const movingPlayer = ref(null)
const eventPulse = ref(null)
const notice = ref('狐狸队先出发！')
const cells = computed(() => Array.from({ length: 30 }, (_, visualIndex) => {
  const row = Math.floor(visualIndex / 6)
  const offset = visualIndex % 6
  return row % 2 === 0 ? row * 6 + offset : row * 6 + (5 - offset)
}))
const wait = (milliseconds) => new Promise((resolve) => window.setTimeout(resolve, milliseconds))

async function rollDice() {
  if (rolling.value || winner.value) return
  rolling.value = true
  const player = players.value[turn.value]
  notice.value = `${player.name}正在掷骰子…`
  for (let index = 0; index < 8; index += 1) {
    dice.value = Math.floor(Math.random() * 6) + 1
    await wait(65)
  }
  const steps = dice.value
  notice.value = `${player.name}前进 ${steps} 格`
  movingPlayer.value = turn.value
  for (let step = 0; step < steps && player.position < LAST_CELL; step += 1) {
    player.position += 1
    await wait(180)
  }
  const event = events[player.position]
  if (event) {
    eventPulse.value = player.position
    notice.value = `${player.name}：${event.text}`
    await wait(500)
    player.position = Math.max(0, Math.min(LAST_CELL, player.position + event.move))
    await wait(450)
  }
  movingPlayer.value = null
  eventPulse.value = null
  if (player.position === LAST_CELL) winner.value = player.name
  if (!winner.value) turn.value = (turn.value + 1) % players.value.length
  rolling.value = false
}

function restart() {
  players.value.forEach((player) => { player.position = 0 })
  turn.value = 0
  dice.value = null
  winner.value = null
  rolling.value = false
  movingPlayer.value = null
  eventPulse.value = null
  notice.value = '狐狸队先出发！'
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
        <div class="toolbar"><strong>{{ winner ? `${winner}找到宝藏！` : `${players[turn].avatar} ${players[turn].name}的回合` }}</strong><AppButton variant="ghost" @click="restart">重新开始</AppButton></div>
        <div class="adventure-board" aria-label="丛林冒险棋棋盘">
          <div v-for="cell in cells" :key="cell" class="adventure-cell" :class="{ event: events[cell], triggered: eventPulse === cell }">
            <small>{{ cell + 1 }}</small><span class="event-icon">{{ cell === LAST_CELL ? '🏰' : events[cell]?.icon }}</span>
            <div class="pieces"><span v-for="(player, index) in players.filter(item => item.position === cell)" :key="player.name" class="piece" :class="{ moving: movingPlayer === players.indexOf(player) }" :style="{ '--piece-index': index }">{{ player.avatar }}</span></div>
          </div>
        </div>
        <div class="controls"><div class="dice" :class="{ rolling }" aria-live="polite">{{ dice || '🎲' }}</div><AppButton size="lg" :disabled="rolling || !!winner" @click="rollDice">掷骰前进</AppButton></div>
        <p class="notice" aria-live="polite">{{ notice }}</p>
        <p class="rule">两队轮流掷骰，沿蛇形路线逐格前进；藤梯和巨鹰会送你飞跃，树蛇、急流与洞穴会让你后退。</p>
      </section>
    </GameAccessGate>
  </main>
</template>

<style scoped>
.game-page { min-height: 100dvh; padding: var(--space-4); background: var(--gradient-warm); }
.game-panel { padding: var(--space-4); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: var(--space-3); color: var(--text-primary); }
.adventure-board { display: grid; grid-template-columns: repeat(6, 1fr); width: min(100%, 640px); margin: 0 auto; gap: var(--space-1); padding: var(--space-2); background: var(--game-jungle); border-radius: var(--radius-lg); }
.adventure-cell { position: relative; display: grid; place-items: center; aspect-ratio: 1; background: var(--bg-card); border: 2px solid var(--game-path); border-radius: var(--radius-sm); transition: transform 220ms ease, box-shadow 220ms ease; }
.adventure-cell.event { background: var(--game-event); }
.adventure-cell.triggered { z-index: 2; transform: scale(1.12); box-shadow: var(--shadow-card); }
.adventure-cell small { position: absolute; top: 2px; left: 4px; color: var(--text-tertiary); }
.event-icon { font-size: clamp(16px, 4vw, 28px); }
.pieces { position: absolute; right: 1px; bottom: 0; display: flex; }
.piece { display: inline-block; font-size: clamp(18px, 4.5vw, 32px); filter: drop-shadow(var(--shadow-text)); transform: translateX(calc(var(--piece-index) * -8px)); }
.controls { display: flex; align-items: center; justify-content: center; gap: var(--space-4); margin-top: var(--space-5); }
.dice { display: grid; place-items: center; width: 64px; height: 64px; background: var(--bg-card); color: var(--text-primary); border: 3px solid var(--color-success); border-radius: var(--radius-md); font-size: var(--text-xl); font-weight: var(--font-bold); }
.notice { min-height: 24px; margin-top: var(--space-3); text-align: center; color: var(--color-primary); font-weight: var(--font-medium); }
.rule { margin-top: var(--space-2); text-align: center; color: var(--text-secondary); font-size: var(--text-sm); }
@media (prefers-reduced-motion: no-preference) {
  .dice.rolling { animation: dice-roll 260ms linear infinite; }
  .piece.moving { animation: piece-hop 360ms ease-in-out infinite; }
  .adventure-cell.triggered .event-icon { animation: event-pop 420ms ease-in-out infinite alternate; }
  @keyframes dice-roll { to { transform: rotate(360deg) scale(1.08); } }
  @keyframes piece-hop { 50% { transform: translateY(-12px) scale(1.12); } }
  @keyframes event-pop { to { transform: scale(1.35) rotate(8deg); } }
}
@media (max-width: 480px) { .adventure-board { gap: 2px; padding: var(--space-1); } }
</style>
