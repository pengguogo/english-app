<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import GameAccessGate from '../components/games/GameAccessGate.vue'
import { useGameAccess } from '../composables/useGameAccess'

const router = useRouter()
const accessState = useGameAccess()
const gameUrl = computed(() => `${import.meta.env.BASE_URL}vendor/chinese-ludo/game.html`)
</script>

<template>
  <main class="game-page">
    <BackBar title="飞行棋" @back="router.push('/games')" />
    <GameAccessGate
      :access="accessState.access.value"
      :is-loading="accessState.isLoading.value"
      :error-msg="accessState.errorMsg.value"
      :progress-percent="accessState.progressPercent.value"
      @retry="accessState.refreshAccess"
      @learn="router.push('/')"
    >
      <section class="game-shell">
        <iframe :src="gameUrl" title="中国飞行棋" allow="fullscreen" />
      </section>
      <p class="source-note">完整中国飞行棋规则 · 支持 2–4 人、飞跃、撞子和棋子动画</p>
    </GameAccessGate>
  </main>
</template>

<style scoped>
.game-page { min-height: 100dvh; padding: var(--space-4); background: var(--gradient-warm); }
.game-shell { overflow: hidden; height: min(820px, calc(100dvh - 120px)); min-height: 650px; background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.game-shell iframe { width: 100%; height: 100%; border: 0; }
.source-note { margin-top: var(--space-3); text-align: center; color: var(--text-tertiary); font-size: var(--text-xs); }
@media (max-width: 600px) {
  .game-page { padding: var(--space-2); }
  .game-shell { min-height: 720px; }
}
</style>
