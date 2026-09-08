<script setup>
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import ModelViewer from '../components/models/ModelViewer.vue'
const router = useRouter()
</script>

<template>
  <main class="models-page">
    <BackBar title="3D 模型馆" @back="router.push('/')" />
    <header class="intro">
      <span class="eyebrow">模型 01 · 交通工具</span>
      <h1>蒸汽火车 <span>Steam locomotive</span></h1>
      <p>一座可以转动的小小火车展台。看看圆圆的锅炉，再找找驾驶室和大车轮。</p>
    </header>
    <ModelViewer />
    <aside class="note">
      <h2>试着换个角度</h2>
      <p>从侧面观察车轮和连杆，从正面看看车灯，从上方找一找装煤的车厢。</p>
      <span>风格化展示模型，比例与细节经过简化。</span>
    </aside>
  </main>
</template>

<style scoped>
.models-page { padding: var(--space-4); }
.intro { padding: var(--space-4) 0 var(--space-6); }
.eyebrow { font-size: var(--text-xs); font-weight: var(--font-bold); letter-spacing: .12em; color: var(--color-primary-hover); }
h1 { font-size: clamp(26px, 4vw, 36px); margin: var(--space-3) 0; color: var(--text-primary); }
h1 span { display: inline-block; font-size: var(--text-base); font-weight: var(--font-normal); color: var(--text-secondary); }
p { color: var(--text-secondary); line-height: 1.8; }
.note { margin-top: var(--space-6); padding: var(--space-5); border-radius: var(--radius-md); background: var(--bg-card); }
.note h2 { font-size: var(--text-base); margin-bottom: var(--space-2); }
.note span { display: block; margin-top: var(--space-3); font-size: var(--text-xs); color: var(--text-secondary); }
</style>
