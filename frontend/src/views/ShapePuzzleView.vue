<script setup>
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import GameAccessGate from '../components/games/GameAccessGate.vue'
import { useGameAccess } from '../composables/useGameAccess'
const router = useRouter()
const state = useGameAccess()
const gameUrl = `${import.meta.env.BASE_URL}vendor/scrollzz/index.html`
</script>

<template>
  <main class="shape-page">
    <BackBar title="几何转转拼图" @back="router.push('/games')" />
    <GameAccessGate :access="state.access.value" :is-loading="state.isLoading.value"
      :error-msg="state.errorMsg.value" :progress-percent="state.progressPercent.value"
      @retry="state.refreshAccess" @learn="router.push('/')">
      <p class="shape-tip">在小格里上下滑动，拼出完整图案。先从四角星开始吧！</p>
      <iframe class="shape-frame" :src="gameUrl" title="几何转转拼图游戏" sandbox="allow-same-origin" />
    </GameAccessGate>
  </main>
</template>

<style scoped>
.shape-page { min-height: 100dvh; padding: var(--space-3); background: var(--gradient-warm); }
.shape-tip { text-align: center; color: var(--text-secondary); font-size: var(--text-sm); line-height: 1.7; margin: var(--space-3) auto; max-width: 520px; }
.shape-frame { display: block; width: 100%; max-width: 680px; height: max(500px, calc(100dvh - 170px)); margin: auto; border: 0; border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
</style>
