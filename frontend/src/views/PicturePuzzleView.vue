<script setup>
import { computed, ref } from 'vue'
import AppButton from '../components/AppButton.vue'
import KidsGameLayout from '../components/games/KidsGameLayout.vue'
import { createPicturePieces, hintPicturePieces, swapPicturePieces } from '../utils/picturePuzzle'
const image = `${import.meta.env.BASE_URL}images/games/woodland-puzzle.png`
const size = ref(2)
const pieces = ref(createPicturePieces(2))
const selected = ref(null)
const moves = ref(0)
const preview = ref(false)
const imageError = ref(false)
const correct = computed(() => pieces.value.filter((value, index) => value === index).length)
const won = computed(() => correct.value === pieces.value.length)
function restart(nextSize = size.value) {
  size.value = nextSize
  pieces.value = createPicturePieces(nextSize)
  selected.value = null
  moves.value = 0
}
function select(index) {
  if (won.value || preview.value || imageError.value) return
  if (selected.value === null) selected.value = index
  else if (selected.value === index) selected.value = null
  else {
    pieces.value = swapPicturePieces(pieces.value, selected.value, index)
    moves.value++
    selected.value = null
  }
}
function hint() {
  pieces.value = hintPicturePieces(pieces.value)
  moves.value++
  selected.value = null
}
function pieceStyle(value) {
  return { '--piece-image': `url(${image})`, '--piece-scale': `${size.value * 100}% ${size.value * 100}%`,
    '--piece-position': `${value % size.value * 100 / (size.value - 1)}% ${Math.floor(value / size.value) * 100 / (size.value - 1)}%` }
}
</script>

<template>
  <KidsGameLayout title="童话拼图">
    <div class="puzzle-intro"><span class="eyebrow">WOODLAND STORIES</span><h2>拼出森林里的小美好</h2><p>点一块，再点另一块，就能交换位置。</p></div>
    <div class="kids-toolbar">
      <AppButton v-for="count in [2, 3, 4]" :key="count" :variant="size === count ? 'primary' : 'ghost'" @click="restart(count)">{{ count * count }} 块</AppButton>
    </div>
    <p class="puzzle-progress" role="status">{{ won ? '拼好了！森林朋友们在向你打招呼。' : `归位 ${correct} / ${pieces.length} · 交换 ${moves} 次` }}</p>
    <div class="picture-board" :style="{gridTemplateColumns: `repeat(${size}, minmax(0, 1fr))`}" :class="{ complete: won }">
      <AppButton v-for="(value, index) in pieces" :key="index" class="picture-piece" :class="{ selected: selected === index }"
        :style="pieceStyle(value)" :disabled="won || preview || imageError" :aria-pressed="selected === index"
        :aria-label="`第 ${index + 1} 格，图块 ${value + 1}`" @click="select(index)"><span v-if="selected === index" class="piece-marker">✓</span></AppButton>
      <img v-if="preview" class="preview-image" :src="image" alt="森林村庄完整拼图参考" />
    </div>
    <img class="image-check" :src="image" alt="" @error="imageError = true" />
    <p v-if="imageError" role="alert">插画加载失败，请刷新页面重试。</p>
    <p class="puzzle-message">{{ won ? '你完成了一幅漂亮的作品！' : selected !== null ? '再点另一块，把它们交换。' : '不着急，看看颜色和图案在哪里接得上。' }}</p>
    <div class="kids-toolbar">
      <AppButton variant="ghost" :aria-pressed="preview" @click="preview = !preview">{{ preview ? '继续拼图' : '看看原图' }}</AppButton>
      <AppButton variant="success" :disabled="won || preview || imageError" @click="hint">帮我拼一块</AppButton>
      <AppButton variant="ghost" @click="restart()">重新开始</AppButton>
    </div>
  </KidsGameLayout>
</template>

<style scoped>
.puzzle-intro { margin-bottom: var(--space-4); }
.eyebrow { color: var(--color-primary); font-size: var(--text-xs); letter-spacing: 0.2em; }
.puzzle-intro h2 { margin: var(--space-2) 0; font-size: var(--text-lg); }
.puzzle-intro p, .puzzle-message { color: var(--text-secondary); font-size: var(--text-sm); line-height: 1.7; }
.puzzle-progress { margin-bottom: var(--space-3); color: var(--color-primary); }
.picture-board { position: relative; display: grid; width: 100%; max-width: 480px; aspect-ratio: 1; margin: auto; gap: 3px; padding: var(--space-2); background: var(--bg-muted); border-radius: var(--radius-lg); box-shadow: var(--shadow-soft); }
.picture-board .picture-piece { width: 100%; min-width: 0; min-height: 0; padding: 0; border-radius: var(--radius-sm); background-image: var(--piece-image) !important; background-size: var(--piece-scale) !important; background-position: var(--piece-position) !important; background-repeat: no-repeat !important; box-shadow: none; border: 2px solid transparent; }
.picture-board .picture-piece:disabled { background-color: var(--bg-card) !important; opacity: 1; }
.picture-board .selected { border-color: var(--color-accent); outline: 3px solid var(--color-accent); z-index: 1; }
.piece-marker { width: 28px; height: 28px; border-radius: var(--radius-pill); background: var(--color-accent); color: var(--text-primary); }
.preview-image { position: absolute; inset: var(--space-2); width: calc(100% - var(--space-2) * 2); height: calc(100% - var(--space-2) * 2); border-radius: var(--radius-md); }
.image-check { display: none; }
.puzzle-message { margin: var(--space-4) 0; }
.complete { gap: 0; }
.complete .picture-piece { border: 0; border-radius: 0; }
</style>
