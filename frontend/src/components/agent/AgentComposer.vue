<script setup>
import { ref } from 'vue'
import AppButton from '../AppButton.vue'
import { MAX_TEXT } from '../../composables/useCompanion'

const props = defineProps({
  phase: { type: String, required: true },
  prompt: { type: String, required: true },
  disabled: Boolean
})
const emit = defineEmits(['send', 'record', 'stop'])
const text = ref('')

function send() {
  if (!text.value.trim() || props.disabled) return
  emit('send', text.value)
  text.value = ''
}
</script>

<template>
  <form class="composer" @submit.prevent="send">
    <AppButton variant="ghost" class="mic" :disabled="disabled"
      :aria-label="phase === 'recording' ? '停下录音' : '开始说话'"
      :aria-pressed="phase === 'recording'"
      @click="phase === 'recording' ? $emit('stop') : $emit('record')">
      <span aria-hidden="true">🎙</span>{{ phase === 'recording' ? '说完了' : '说话' }}
    </AppButton>
    <input v-model="text" :maxlength="MAX_TEXT" :disabled="disabled || phase === 'recording'" :placeholder="prompt" :aria-label="prompt" />
    <AppButton type="submit" :disabled="disabled || phase === 'recording' || !text.trim()">发送</AppButton>
  </form>
</template>

<style scoped>
.composer { display: flex; align-items: center; gap: var(--space-2); padding: var(--space-2); background: var(--bg-card); border-radius: var(--radius-md); border: 1px solid var(--border-light); }
.composer input { min-width: 0; flex: 1; min-height: var(--touch-target); font: inherit; color: var(--text-primary); border: 0; background: var(--bg-muted); border-radius: var(--radius-sm); padding: 0 var(--space-3); }
.composer :deep(.app-btn) { padding-inline: var(--space-3); }
</style>
