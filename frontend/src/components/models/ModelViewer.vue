<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import AppButton from '../AppButton.vue'
import { createModelScene } from './modelScene'

const host = ref(null)
const error = ref('')
let scene
function contextLost(event) {
  event.preventDefault()
  error.value = '3D 显示暂时中断，请重新加载模型。'
}
function start() {
  scene?.dispose()
  scene = undefined
  host.value.replaceChildren()
  error.value = ''
  try {
    scene = createModelScene(host.value)
  } catch (cause) {
    error.value = '当前设备暂时无法显示 3D 模型，请启用浏览器硬件加速后重试。'
    console.error('加载蒸汽火车模型失败', cause)
  }
}
onMounted(() => {
  host.value.addEventListener('webglcontextlost', contextLost, true)
  start()
})
onBeforeUnmount(() => {
  host.value?.removeEventListener('webglcontextlost', contextLost, true)
  scene?.dispose()
})
</script>

<template>
  <section class="viewer" aria-label="蒸汽火车模型查看器">
    <div class="stage-wrap">
      <div ref="host" class="stage" />
      <div v-if="error" class="error" role="alert">
        <p>{{ error }}</p>
        <AppButton @click="start">重新加载</AppButton>
      </div>
      <span v-else class="stage-label">01 / STEAM LOCOMOTIVE</span>
    </div>
    <div class="toolbar" aria-label="模型操作">
      <AppButton variant="ghost" :disabled="!!error" @click="scene?.view('side')">侧面</AppButton>
      <AppButton variant="ghost" :disabled="!!error" @click="scene?.view('front')">正面</AppButton>
      <AppButton variant="ghost" :disabled="!!error" @click="scene?.view('top')">俯视</AppButton>
      <AppButton variant="ghost" :disabled="!!error" aria-label="放大模型" @click="scene?.zoom(.8)">放大 ＋</AppButton>
      <AppButton variant="ghost" :disabled="!!error" aria-label="缩小模型" @click="scene?.zoom(1.25)">缩小 −</AppButton>
      <AppButton :disabled="!!error" @click="scene?.view()">复位视角</AppButton>
    </div>
    <p class="hint">拖动旋转 · 滚轮或双指缩放 · 也可以点击按钮切换视角</p>
  </section>
</template>

<style scoped>
.viewer { border: 1px solid var(--border-light); border-radius: var(--radius-lg); overflow: hidden; background: var(--bg-card); box-shadow: var(--shadow-card); }
.stage-wrap { position: relative; }
.stage { height: clamp(320px, 55vw, 530px); background: var(--model-stage); touch-action: none; }
.stage :deep(canvas) { display: block; width: 100%; height: 100%; }
.stage-label { position: absolute; top: var(--space-5); left: var(--space-5); color: var(--text-secondary); letter-spacing: .13em; font-size: var(--text-xs); pointer-events: none; }
.toolbar { display: flex; justify-content: center; flex-wrap: wrap; gap: var(--space-2); padding: var(--space-4); }
.hint { text-align: center; color: var(--text-secondary); font-size: var(--text-sm); padding: 0 var(--space-4) var(--space-5); }
.error { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-4); padding: var(--space-6); background: var(--model-stage); text-align: center; }
@media (max-width: 480px) { .toolbar { gap: var(--space-1); padding: var(--space-3); } }
</style>
