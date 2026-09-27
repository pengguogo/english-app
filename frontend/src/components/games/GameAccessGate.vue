<script setup>
import AppButton from '../AppButton.vue'

defineProps({
  access: { type: Object, default: null },
  isLoading: { type: Boolean, default: false },
  errorMsg: { type: String, default: '' },
  progressPercent: { type: Number, default: 0 }
})
defineEmits(['retry', 'learn'])
</script>

<template>
  <div v-if="isLoading" class="gate state" role="status">正在检查今日学习时长…</div>
  <div v-else-if="errorMsg" class="gate state error" role="alert">
    <p>{{ errorMsg }}</p>
    <AppButton variant="ghost" @click="$emit('retry')">重新检查</AppButton>
  </div>
  <div v-else-if="access && !access.unlocked" class="gate locked">
    <span class="lock-icon" aria-hidden="true">🔒</span>
    <h2>再学习 {{ Math.ceil(access.remainingSeconds / 60) }} 分钟就能玩</h2>
    <p>今天已认真学习 {{ Math.floor(access.studiedSeconds / 60) }} 分 {{ access.studiedSeconds % 60 }} 秒</p>
    <div class="progress" role="progressbar" :aria-valuenow="progressPercent" aria-valuemin="0" aria-valuemax="100">
      <span :style="{ width: `${progressPercent}%` }"></span>
    </div>
    <AppButton size="lg" @click="$emit('learn')">去学习</AppButton>
  </div>
  <slot v-else />
</template>

<style scoped>
.gate { text-align: center; padding: var(--space-8); }
.state { color: var(--text-tertiary); }
.error { color: var(--color-warning); }
.error p { margin-bottom: var(--space-3); }
.locked { background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.lock-icon { display: block; font-size: 48px; margin-bottom: var(--space-3); }
.locked h2 { color: var(--text-primary); margin-bottom: var(--space-2); }
.locked p { color: var(--text-secondary); margin-bottom: var(--space-4); }
.progress { height: 14px; overflow: hidden; margin-bottom: var(--space-5); background: var(--bg-muted); border-radius: var(--radius-pill); }
.progress span { display: block; height: 100%; background: var(--gradient-success); border-radius: inherit; transition: width var(--duration-normal) var(--ease-smooth); }
</style>
