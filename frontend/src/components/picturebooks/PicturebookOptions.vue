<script setup>
import { ref } from 'vue'
import AppButton from '../AppButton.vue'
defineProps({ options: { type: Array, required: true }, ready: Boolean, saving: Boolean, selected: Number, answer: Number })
defineEmits(['choose', 'listen'])
const failed = ref({})
const text = option => typeof option === 'string' ? option : option.text
</script>

<template>
  <div class="options" role="group" aria-label="听一听，选一幅图；喇叭只试听，不会作答">
    <div v-for="(option, position) in options" :key="position" class="option-card">
      <AppButton class="answer-option" :variant="ready && position === answer ? 'success' : 'ghost'" :disabled="ready || saving" :aria-label="`选择${position + 1}：${text(option)}`" :aria-pressed="selected === position" @click="$emit('choose', position)">
        <img v-if="option.image && !failed[position]" :src="option.image" :alt="text(option)" @error="failed[position] = true" />
        <span v-else class="option-text">{{ text(option) }}</span>
        <span class="option-label" aria-hidden="true">{{ ready && position === answer ? '✓' : position + 1 }}</span>
      </AppButton>
      <AppButton class="listen-option" variant="ghost" :aria-label="`试听选项${position + 1}：${text(option)}`" @click="$emit('listen', text(option))"><span aria-hidden="true">🔊</span> 听一听</AppButton>
    </div>
  </div>
</template>

<style scoped>
.options { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: var(--space-3); margin-top: var(--space-5); }
.option-card { min-width: 0; border: 2px solid var(--border-light); border-radius: var(--radius-md); overflow: hidden; background: var(--bg-card); }
.option-card .answer-option { width: 100%; height: calc(100% - 56px); min-height: 132px; flex-direction: column; padding: var(--space-2); border-radius: 0; white-space: normal; }
.answer-option img { width: 100%; height: 156px; object-fit: contain; border-radius: var(--radius-sm); }
.option-text { font-size: var(--text-lg); overflow-wrap: anywhere; }
.option-label { font-size: var(--text-base); font-weight: var(--font-bold); }
.option-card .listen-option { width: 100%; min-height: 56px; border-top: 1px solid var(--border-light); border-radius: 0; white-space: normal; }
.listen-option span { font-size: var(--text-lg); }
@media(max-width: 420px) { .answer-option img { height: 125px; } }
</style>
