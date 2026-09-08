<script setup>
import { useRouter } from 'vue-router'
import AppButton from '../AppButton.vue'
const router = useRouter()
</script>

<template>
  <section class="book-entry" aria-labelledby="book-entry-title">
    <img :src="'/images/picturebooks/train-station.jpg'" alt="溪边车站的绿色小火车" />
    <div>
      <span class="eyebrow">看图 · 听故事 · 跟着说</span>
      <h2 id="book-entry-title">绘本小火车，准备发车！</h2>
      <p>不用认识字。点大图听故事，听声音选图片。</p>
      <AppButton size="lg" variant="success" @click="router.push('/picturebooks')"><span aria-hidden="true">🔊</span>开始听故事</AppButton>
    </div>
  </section>
</template>

<style scoped>
.book-entry { display: flex; align-items: center; gap: var(--space-6); background: var(--bg-card); border: 2px solid var(--border-light); border-radius: var(--radius-lg); overflow: hidden; margin-bottom: var(--space-6); }
img { width: 38%; max-height: 240px; object-fit: cover; }
.book-entry > div { padding: var(--space-5); }
.eyebrow { color: var(--text-secondary); font-size: var(--text-xs); letter-spacing: .08em; }
h2 { margin: var(--space-2) 0; color: var(--text-primary); font-size: var(--text-lg); }
p { color: var(--text-secondary); margin-bottom: var(--space-4); }
@media (max-width: 600px) { .book-entry { flex-direction: column; gap: 0; align-items: stretch; } img { width: 100%; max-height: 190px; } }
</style>
