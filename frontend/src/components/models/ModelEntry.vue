<script setup>
import { useRouter } from 'vue-router'
import AppButton from '../AppButton.vue'
const router = useRouter()
</script>

<template>
  <section class="model-entry" aria-labelledby="model-entry-title">
    <div>
      <span class="eyebrow">小小探索家 · 3D</span>
      <h2 id="model-entry-title">把蒸汽火车转过来看</h2>
      <p>走近立体模型，从每个角度发现新细节。</p>
    </div>
    <AppButton @click="router.push('/models')">探索 3D 模型 →</AppButton>
  </section>
</template>

<style scoped>
.model-entry { display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: var(--space-4); padding: var(--space-6); margin-bottom: var(--space-6); background: var(--model-stage); border: 1px solid var(--border-light); border-radius: var(--radius-lg); }
.eyebrow { color: var(--color-primary-hover); font-size: var(--text-xs); font-weight: var(--font-bold); letter-spacing: .12em; }
h2 { margin: var(--space-2) 0; font-size: var(--text-lg); color: var(--text-primary); }
p { color: var(--text-secondary); }
</style>
