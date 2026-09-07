<script setup>
import { computed } from 'vue'
import MimiMascot from './MimiMascot.vue'

const props = defineProps({
  mood: { type: String, default: 'encourage' },
  message: { type: String, default: '' }
})

const preset = computed(() => {
  if (props.mood === 'celebrate') {
    return { variant: 'celebrate', icon: '✨', label: '太棒了' }
  }
  if (props.mood === 'happy') {
    return { variant: 'celebrate', icon: '⭐', label: '答对啦' }
  }
  return { variant: 'companion', icon: '💛', label: '再试一次' }
})
</script>

<template>
  <aside class="mascot-feedback" :class="`mood-${mood}`" role="status" aria-live="polite">
    <span class="feedback-spark" aria-hidden="true">{{ preset.icon }}</span>
    <MimiMascot :variant="preset.variant" size="md" />
    <div class="feedback-bubble">
      <strong>{{ preset.label }}</strong>
      <span>{{ message }}</span>
    </div>
  </aside>
</template>

<style scoped>
.mascot-feedback {
  position: fixed;
  left: 50%;
  bottom: var(--space-5);
  z-index: 20;
  display: flex;
  align-items: center;
  gap: var(--space-3);
  width: min(420px, calc(100vw - 32px));
  padding: var(--space-3) var(--space-4);
  background: var(--bg-card);
  border: 3px solid var(--color-accent);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-hover);
  pointer-events: none;
  transform: translateX(-50%);
}

.mood-encourage { border-color: var(--color-primary-light); }
.mood-happy,
.mood-celebrate { border-color: var(--color-success); }

.feedback-bubble {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.feedback-bubble strong { color: var(--text-primary); font-size: var(--text-base); }
.feedback-bubble span { color: var(--text-secondary); font-size: var(--text-sm); }

.feedback-spark {
  position: absolute;
  top: calc(var(--space-3) * -1);
  right: var(--space-4);
  font-size: 28px;
}

@media (prefers-reduced-motion: no-preference) {
  .mascot-feedback { animation: feedbackPop var(--duration-normal) var(--ease-bounce); }
  .feedback-spark { animation: sparkTwinkle 700ms ease-in-out 2; }
}

@keyframes feedbackPop {
  from { opacity: 0; transform: translate(-50%, 24px) scale(0.9); }
  to { opacity: 1; transform: translate(-50%, 0) scale(1); }
}

@keyframes sparkTwinkle {
  50% { transform: scale(1.35) rotate(12deg); }
}
</style>
