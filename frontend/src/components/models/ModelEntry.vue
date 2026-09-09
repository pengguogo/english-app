<script setup>
import { useRouter } from 'vue-router'
import AppButton from '../AppButton.vue'
const router = useRouter()
</script>

<template>
  <section class="model-entry" aria-labelledby="model-entry-title">
    <div>
      <span class="eyebrow">小小探索家 · 3D</span>
      <h2 id="model-entry-title">走近火车，发现机械细节</h2>
      <p>蒸汽火车与燃油机车，旋转、放大，还能打开机罩看零件。</p>
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
