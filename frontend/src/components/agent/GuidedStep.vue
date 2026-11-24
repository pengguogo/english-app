<script setup>
import AppButton from '../AppButton.vue'

defineProps({
  screen: { type: Object, required: true },
  pending: Boolean,
  playing: Boolean,
  audioError: { type: String, default: '' }
})
defineEmits(['act', 'listen', 'book'])
</script>

<template>
  <article class="step">
    <img class="scene" :src="screen.image" :alt="screen.title" />
    <div class="step-body">
      <h2>{{ screen.title }}</h2>
      <p>{{ screen.speech }}</p>
      <p v-if="screen.feedback" class="feedback" role="status">{{ screen.feedback }}</p>
      <div class="options">
        <AppButton v-for="option in screen.options" :key="option.id" size="lg" :disabled="pending"
          @click="$emit('act', 'ANSWER', option.id)">{{ option.text }}</AppButton>
        <AppButton v-if="screen.step === 'INTRO'" size="lg" :disabled="pending"
          @click="$emit('act', 'NEXT')">我准备好了</AppButton>
        <AppButton v-if="screen.book" size="lg" :disabled="pending" @click="$emit('book')">打开绘本 →</AppButton>
        <AppButton v-if="screen.step === 'DONE'" size="lg" :disabled="pending"
          @click="$emit('act', 'START')">再玩一次</AppButton>
        <AppButton variant="ghost" :disabled="pending" @click="$emit('listen')">
          {{ playing ? '停止播报' : '听豆豆说' }}
        </AppButton>
      </div>
      <p v-if="audioError" role="status">{{ audioError }}</p>
    </div>
  </article>
</template>

<style scoped>
.step { overflow: hidden; border-radius: var(--radius-lg); background: var(--bg-card); box-shadow: var(--shadow-card); }
.scene { display: block; width: 100%; aspect-ratio: 2.5; object-fit: cover; object-position: center 52%; }
.step-body { display: grid; gap: var(--space-3); padding: clamp(16px, 4vw, 28px); }
.step-body h2 { font-size: var(--text-lg); line-height: 1.3; }
.step-body p { color: var(--text-secondary); line-height: 1.65; }
.step-body .feedback { color: var(--text-primary); background: var(--color-warning-bg); padding: var(--space-2) var(--space-3); border-radius: var(--radius-sm); }
.options { display: flex; flex-wrap: wrap; gap: var(--space-2); }
@media (max-width: 480px) {
  .scene { aspect-ratio: 1.8; }
  .options { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .options :deep(.app-btn) { white-space: normal; min-width: 0; padding-inline: var(--space-2); font-size: var(--text-base); }
}
</style>
