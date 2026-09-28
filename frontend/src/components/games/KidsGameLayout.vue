<script setup>
import { useRouter } from 'vue-router'
import BackBar from '../BackBar.vue'
import GameAccessGate from './GameAccessGate.vue'
import { useGameAccess } from '../../composables/useGameAccess'
defineProps({ title: { type: String, required: true } })
const router = useRouter()
const state = useGameAccess()
</script>

<template>
  <main class="kids-game">
    <BackBar :title="title" @back="router.push('/games')" />
    <GameAccessGate :access="state.access.value" :is-loading="state.isLoading.value"
      :error-msg="state.errorMsg.value" :progress-percent="state.progressPercent.value"
      @retry="state.refreshAccess" @learn="router.push('/')">
      <section class="kids-panel"><slot /></section>
    </GameAccessGate>
  </main>
</template>

<style>
.kids-game { min-height: 100dvh; padding: var(--space-4); background: var(--gradient-warm); }
.kids-panel { max-width: 680px; margin: auto; padding: var(--space-5); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); text-align: center; color: var(--text-primary); }
.kids-toolbar { display: flex; flex-wrap: wrap; align-items: center; justify-content: center; gap: var(--space-3); margin-bottom: var(--space-4); }
.kids-status { margin: var(--space-4) 0; min-height: 2em; font-size: var(--text-lg); }
.kids-rule { margin: var(--space-4) 0; color: var(--text-secondary); line-height: 1.8; }
.kids-grid { display: grid; gap: var(--space-3); max-width: 420px; margin: auto; }
.kids-game .kids-panel .kids-grid .app-btn { border-radius: var(--radius-md); min-height: 72px; padding: var(--space-2); font-size: 36px; }
.kids-game .kids-panel .kids-grid .app-btn:disabled { background: var(--color-success-bg) !important; color: var(--text-primary) !important; opacity: 1; }
</style>
