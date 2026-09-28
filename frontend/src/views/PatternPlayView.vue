<script setup>
import { ref } from 'vue'
import AppButton from '../components/AppButton.vue'
import KidsGameLayout from '../components/games/KidsGameLayout.vue'
import { createPattern } from '../utils/kidsGames'
const round = ref(0)
const puzzle = ref(createPattern(0))
const solved = ref(false)
const message = ref('观察前面的图案，下一个是什么？')
function choose(symbol) {
  if (solved.value) return
  solved.value = symbol === puzzle.value.answer
  message.value = solved.value ? '🎉 找到规律了！' : '再看一看，图案是怎样重复的？'
}
function next() {
  round.value++
  puzzle.value = createPattern(round.value)
  solved.value = false
  message.value = '观察前面的图案，下一个是什么？'
}
</script>

<template>
  <KidsGameLayout title="图案接龙">
    <p class="kids-rule">图案会按照同一个顺序重复。找一找，应该接上哪一个？</p>
    <p>第 {{ round + 1 }} 题</p>
    <div class="sequence" aria-label="图案序列">
      <span v-for="(symbol, index) in puzzle.sequence" :key="index">{{ symbol }}</span>
      <strong>{{ solved ? puzzle.answer : '？' }}</strong>
    </div>
    <p class="kids-status" role="status">{{ message }}</p>
    <div class="kids-grid options">
      <AppButton v-for="symbol in puzzle.options" :key="symbol" :disabled="solved" :aria-label="`选择 ${symbol}`" @click="choose(symbol)">{{ symbol }}</AppButton>
    </div>
    <AppButton class="next" :disabled="!solved" @click="next">下一题</AppButton>
  </KidsGameLayout>
</template>

<style scoped>
.sequence { display: flex; flex-wrap: wrap; justify-content: center; gap: var(--space-2); margin-top: var(--space-5); font-size: clamp(24px, 6vw, 42px); }
.sequence strong { color: var(--color-primary); }
.options { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.next { margin-top: var(--space-5); }
</style>
