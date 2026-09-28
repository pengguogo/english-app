<script setup>
import { computed, ref } from 'vue'
import AppButton from '../components/AppButton.vue'
import KidsGameLayout from '../components/games/KidsGameLayout.vue'
import { ticTacToeResult } from '../utils/kidsGames'
const board = ref(Array(9).fill(null))
const player = ref('⭕')
const result = computed(() => ticTacToeResult(board.value))
const status = computed(() => result.value === 'draw' ? '🤝 平局！一起再试一次吧。' : result.value ? `🎉 ${result.value} 连成三格，获胜！` : `轮到 ${player.value} 点击一个空格`)
function play(index) {
  if (board.value[index] || result.value) return
  board.value[index] = player.value
  if (!result.value) player.value = player.value === '⭕' ? '❌' : '⭕'
}
function restart() {
  board.value = Array(9).fill(null)
  player.value = '⭕'
}
</script>

<template>
  <KidsGameLayout title="亲子井字棋">
    <p class="kids-rule">邀请爸爸妈妈或小伙伴一起玩。⭕ 和 ❌ 轮流点击，横着、竖着或斜着连成三个就获胜。</p>
    <p class="kids-status" role="status">{{ status }}</p>
    <div class="kids-grid board" aria-label="井字棋棋盘">
      <AppButton v-for="(cell, index) in board" :key="index" :disabled="Boolean(cell || result)"
        :aria-label="`第 ${Math.floor(index / 3) + 1} 行第 ${index % 3 + 1} 列：${cell || '空格'}`"
        @click="play(index)">{{ cell || '·' }}</AppButton>
    </div>
    <AppButton class="restart" variant="ghost" @click="restart">再玩一局</AppButton>
  </KidsGameLayout>
</template>

<style scoped>
.board { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.board .app-btn { aspect-ratio: 1; font-size: 48px; }
.restart { margin-top: var(--space-5); }
</style>
