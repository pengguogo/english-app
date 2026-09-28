<script setup>
import { computed, onUnmounted, ref } from 'vue'
import AppButton from '../components/AppButton.vue'
import KidsGameLayout from '../components/games/KidsGameLayout.vue'
import { shuffle } from '../utils/kidsGames'
const animals = ['🐱', '🐶', '🐰', '🐼', '🦊', '🐸']
const pairs = ref(4)
const cards = ref([])
const opened = ref([])
const matched = ref([])
const turns = ref(0)
let timer
const complete = computed(() => matched.value.length === cards.value.length)
function restart(count = pairs.value) {
  clearTimeout(timer)
  pairs.value = count
  cards.value = shuffle([...animals.slice(0, count), ...animals.slice(0, count)])
  opened.value = []
  matched.value = []
  turns.value = 0
}
function flip(index) {
  if (opened.value.length === 2 || opened.value.includes(index) || matched.value.includes(index)) return
  opened.value.push(index)
  if (opened.value.length !== 2) return
  turns.value++
  const [a, b] = opened.value
  if (cards.value[a] === cards.value[b]) {
    matched.value.push(a, b)
    opened.value = []
  } else timer = setTimeout(() => { opened.value = [] }, 1000)
}
restart()
onUnmounted(() => clearTimeout(timer))
</script>

<template>
  <KidsGameLayout title="动物翻翻乐">
    <div class="kids-toolbar">
      <AppButton :variant="pairs === 4 ? 'primary' : 'ghost'" @click="restart(4)">入门 · 4 对</AppButton>
      <AppButton :variant="pairs === 6 ? 'primary' : 'ghost'" @click="restart(6)">挑战 · 6 对</AppButton>
    </div>
    <p class="kids-rule">每次翻开两张卡片，找到相同的动物。不用抢时间，慢慢记住它们的位置。</p>
    <p class="kids-status" role="status">{{ complete ? '🎉 全部找到了！你的记忆力真棒！' : `已找到 ${matched.length / 2} / ${pairs} 对 · 翻了 ${turns} 次` }}</p>
    <div class="kids-grid memory-grid">
      <AppButton v-for="(animal, index) in cards" :key="index" :disabled="matched.includes(index)"
        :variant="opened.includes(index) ? 'success' : 'primary'"
        :aria-label="matched.includes(index) || opened.includes(index) ? `第 ${index + 1} 张：${animal}` : `翻开第 ${index + 1} 张卡片`"
        @click="flip(index)">{{ matched.includes(index) || opened.includes(index) ? animal : '？' }}</AppButton>
    </div>
    <AppButton class="restart" variant="ghost" @click="restart()">重新开始</AppButton>
  </KidsGameLayout>
</template>

<style scoped>
.memory-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); }
.restart { margin-top: var(--space-4); }
@media (max-width: 380px) { .memory-grid { gap: var(--space-2); } }
</style>
