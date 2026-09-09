<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import AppButton from '../AppButton.vue'
import { createKenneyTrainScene } from './kenneyTrainScene'

const host = ref(null)
const status = ref('loading')
let scene
function start() {
  scene?.dispose()
  scene = undefined
  host.value.replaceChildren()
  status.value = 'loading'
  try {
    scene = createKenneyTrainScene(host.value, value => { status.value = value })
  } catch (cause) {
    console.error('创建 Kenney 火车展台失败', cause)
    status.value = 'error'
  }
}
function contextLost(event) { event.preventDefault(); status.value = 'error' }
onMounted(start)
onBeforeUnmount(() => scene?.dispose())
</script>

<template>
  <section class="kenney-exhibit" aria-labelledby="kenney-title">
    <header>
      <span class="eyebrow">新展品 · KENNEY TRAIN KIT</span>
      <h2 id="kenney-title">柴油小火车 <span>Diesel train</span></h2>
      <p>转动这辆小火车，找找驾驶室、车窗和前后两组车轮。</p>
    </header>
    <div class="stage-wrap">
      <div ref="host" class="stage" @webglcontextlost.capture="contextLost" />
      <p v-if="status === 'loading'" class="overlay" role="status">小火车正在驶入展台…</p>
      <div v-if="status === 'error'" class="overlay" role="alert">
        <p>模型暂时无法显示，请检查网络或浏览器的 3D 支持后重试。</p>
        <AppButton @click="start">重新加载</AppButton>
      </div>
    </div>
    <div class="toolbar" aria-label="Kenney 火车操作">
      <AppButton v-for="(label, key) in { side: '侧面', front: '正面', top: '俯视' }" :key="key" variant="ghost" :disabled="status !== 'ready'" @click="scene?.view(key)">{{ label }}</AppButton>
      <AppButton variant="ghost" :disabled="status !== 'ready'" @click="scene?.zoom(.8)">放大 ＋</AppButton>
      <AppButton variant="ghost" :disabled="status !== 'ready'" @click="scene?.zoom(1.25)">缩小 −</AppButton>
      <AppButton :disabled="status !== 'ready'" @click="scene?.view()">复位视角</AppButton>
    </div>
    <footer>拖动旋转 · 滚轮或双指缩放 · 素材：<a href="https://kenney.nl/assets/train-kit" target="_blank" rel="noopener noreferrer">Kenney Train Kit</a>（CC0）</footer>
  </section>
</template>

<style scoped>
.kenney-exhibit { margin-top: var(--space-4); margin-bottom: var(--space-6); overflow: hidden; border: 1px solid var(--border-light); border-radius: var(--radius-lg); background: var(--bg-card); }
header { padding: var(--space-5); }
.eyebrow { color: var(--color-primary-hover); font-size: var(--text-xs); font-weight: var(--font-bold); }
h2 { margin: var(--space-2) 0; color: var(--text-primary); font-size: var(--text-xl); }
h2 span { font-size: var(--text-base); font-weight: var(--font-normal); color: var(--text-secondary); }
p, footer { color: var(--text-secondary); line-height: 1.8; }
.stage-wrap { position: relative; }
.stage { height: clamp(320px, 50vw, 480px); background: var(--model-stage); touch-action: none; }
.stage :deep(canvas) { display: block; }
.overlay { position: absolute; inset: 0; display: flex; flex-direction: column; justify-content: center; align-items: center; gap: var(--space-4); padding: var(--space-5); text-align: center; background: var(--model-stage); }
.toolbar { display: flex; flex-wrap: wrap; justify-content: center; gap: var(--space-2); padding: var(--space-4); }
footer { padding: 0 var(--space-4) var(--space-4); text-align: center; font-size: var(--text-xs); }
a { color: var(--color-primary-hover); }
</style>
