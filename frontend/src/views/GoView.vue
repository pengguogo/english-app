<script setup>
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import GameAccessGate from '../components/games/GameAccessGate.vue'
import { useGameAccess } from '../composables/useGameAccess'
import { useGoGame } from '../composables/useGoGame'

const router = useRouter()
const accessState = useGameAccess()
const game = useGoGame()
</script>

<template>
  <main class="game-page">
    <BackBar title="九路围棋" @back="router.push('/games')" />
    <GameAccessGate
      :access="accessState.access.value" :is-loading="accessState.isLoading.value"
      :error-msg="accessState.errorMsg.value" :progress-percent="accessState.progressPercent.value"
      @retry="accessState.refreshAccess" @learn="router.push('/')"
    >
      <section class="game-panel">
        <div class="toolbar"><strong>{{ game.message.value }}</strong><div class="toolbar-actions"><AppButton variant="ghost" @click="game.undo">悔棋</AppButton><AppButton variant="ghost" @click="game.restart">重新开始</AppButton></div></div>
        <div class="score-bar"><span>⚫ 黑 {{ game.score.value.black }}</span><span>⚪ 白 {{ game.score.value.white }}</span></div>
        <div class="go-board" role="grid" aria-label="九路围棋棋盘">
          <button v-for="(_, index) in game.board.value" :key="index" class="point" type="button" role="gridcell" @click="game.play(index)">
            <span v-if="game.board.value[index]" class="stone" :class="game.board.value[index]"></span>
          </button>
        </div>
        <div class="actions"><AppButton :disabled="game.ended.value" @click="game.pass">停一手</AppButton></div>
        <p class="rule">Tenuki 规则引擎 · 中国数子法 · 全局同形禁着。双方连续停一手后，可点击棋子标记死活并计分。</p>
      </section>
    </GameAccessGate>
  </main>
</template>

<style scoped>
.game-page { min-height: 100dvh; padding: var(--space-4); background: var(--gradient-warm); }
.game-panel { padding: var(--space-4); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.toolbar, .score-bar { display: flex; align-items: center; justify-content: space-between; color: var(--text-primary); }
.toolbar-actions { display: flex; gap: var(--space-2); }
.score-bar { width: min(100%, 540px); margin: 0 auto var(--space-3); padding: var(--space-2) var(--space-4); background: var(--bg-muted); border-radius: var(--radius-pill); }
.go-board { display: grid; grid-template-columns: repeat(9, 1fr); width: min(100%, 540px); aspect-ratio: 1; margin: 0 auto; padding: var(--space-5); background: var(--game-board); border: 3px solid var(--game-board-edge); border-radius: var(--radius-sm); }
.point { position: relative; min-width: 0; background-image: linear-gradient(var(--game-grid), var(--game-grid)), linear-gradient(90deg, var(--game-grid), var(--game-grid)); background-size: 100% 1px, 1px 100%; background-position: center; background-repeat: no-repeat; }
.stone { position: absolute; z-index: 1; inset: 7%; border-radius: var(--radius-pill); box-shadow: var(--shadow-soft); }
.stone.black { background: var(--game-stone-black); }
.stone.white { background: var(--game-stone-white); border: 1px solid var(--border-light); }
.stone { animation: stone-drop 180ms ease-out; }
.actions { display: flex; justify-content: center; margin-top: var(--space-4); }
.rule { margin-top: var(--space-4); text-align: center; color: var(--text-secondary); font-size: var(--text-sm); }
@media (prefers-reduced-motion: no-preference) {
  @keyframes stone-drop { from { opacity: 0; transform: scale(1.55); } to { opacity: 1; transform: scale(1); } }
}
</style>
