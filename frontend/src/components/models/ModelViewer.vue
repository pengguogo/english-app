<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import AppButton from '../AppButton.vue'
import { createModelScene } from './modelScene'
import { dieselParts } from './dieselTrain'
import { externalVehicleModels, isExternalVehicle } from './externalVehicleModels'

const props = defineProps({ model: { type: String, default: 'steam' } })
const selected = ref('')
const hoodOpen = ref(false)
function selectPart(id, open) {
  selected.value = id
  hoodOpen.value = open
  host.value?.scrollIntoView({ block: 'center', behavior: 'instant' })
}
function toggleHood() {
  hoodOpen.value = !hoodOpen.value
  if (!hoodOpen.value && selected.value === 'engine') selected.value = ''
  scene?.setHood(hoodOpen.value)
}
function reset() { selected.value = ''; scene?.view() }

const host = ref(null)
const error = ref('')
let scene
function contextLost(event) {
  event.preventDefault()
  error.value = '3D 显示暂时中断，请重新加载模型。'
}
function start() {
  if (isExternalVehicle(props.model)) return
  scene?.dispose()
  scene = undefined
  host.value.replaceChildren()
  error.value = ''
  selected.value = ''
  hoodOpen.value = false
  try {
    scene = createModelScene(host.value, props.model, selectPart)
  } catch (cause) {
    error.value = '当前设备暂时无法显示 3D 模型，请启用浏览器硬件加速后重试。'
    console.error('加载火车模型失败', cause)
  }
}
onMounted(() => {
  if (isExternalVehicle(props.model)) return
  host.value.addEventListener('webglcontextlost', contextLost, true)
  start()
})
onBeforeUnmount(() => {
  host.value?.removeEventListener('webglcontextlost', contextLost, true)
  scene?.dispose()
})
</script>

<template>
  <section v-if="isExternalVehicle(model)" class="viewer external-viewer" :aria-label="`${externalVehicleModels[model].title} 真实交通工具模型查看器`">
    <iframe
      :title="`${externalVehicleModels[model].viewerTitle} 交互式三维模型`"
      :src="`https://sketchfab.com/models/${externalVehicleModels[model].uid}/embed?autostart=1&ui_theme=dark&ui_infos=0&ui_watermark=0`"
      allow="autoplay; fullscreen; xr-spatial-tracking"
      allowfullscreen
      loading="eager"
    />
    <p class="model-credit">模型由 <a :href="externalVehicleModels[model].authorUrl" target="_blank" rel="noopener noreferrer">{{ externalVehicleModels[model].author }}</a> 制作，采用 <a href="https://creativecommons.org/licenses/by/4.0/" target="_blank" rel="noopener noreferrer">CC BY 4.0</a> 授权。</p>
  </section>
  <section v-else class="viewer" :aria-label="model === 'diesel' ? '燃油火车模型查看器' : '蒸汽火车模型查看器'">
    <div class="stage-wrap">
      <div ref="host" class="stage" />
      <div v-if="error" class="error" role="alert">
        <p>{{ error }}</p>
        <AppButton @click="start">重新加载</AppButton>
      </div>
      <span v-else class="stage-label">{{ model === 'diesel' ? '02 / DIESEL LOCOMOTIVE' : '01 / STEAM LOCOMOTIVE' }}</span>
    </div>
    <div v-if="model === 'diesel'" class="inspection" aria-label="零件近景">
      <p class="inspection-title">零件放大镜 <span>点击零件或选择下方名称</span></p>
      <div class="part-buttons">
        <AppButton v-for="part in dieselParts" :key="part.id" :variant="selected === part.id ? 'primary' : 'ghost'" :aria-pressed="selected === part.id" :disabled="!!error" @click="scene?.focus(part.id)">{{ part.label }}</AppButton>
        <AppButton variant="warning" :aria-pressed="hoodOpen" :disabled="!!error" @click="toggleHood">{{ hoodOpen ? '关闭机罩' : '打开机罩' }}</AppButton>
      </div>
      <p class="part-description" aria-live="polite">{{ dieselParts.find(part => part.id === selected)?.description || '选择一个零件，镜头会靠近它；继续放大，可以检查螺栓、管线与金属连接处。' }}</p>
    </div>
    <div class="toolbar" aria-label="模型操作">
      <AppButton variant="ghost" :disabled="!!error" @click="scene?.view('side')">侧面</AppButton>
      <AppButton variant="ghost" :disabled="!!error" @click="scene?.view('front')">正面</AppButton>
      <AppButton variant="ghost" :disabled="!!error" @click="scene?.view('top')">俯视</AppButton>
      <AppButton variant="ghost" :disabled="!!error" aria-label="放大模型" @click="scene?.zoom(.8)">放大 ＋</AppButton>
      <AppButton variant="ghost" :disabled="!!error" aria-label="缩小模型" @click="scene?.zoom(1.25)">缩小 −</AppButton>
      <AppButton :disabled="!!error" @click="reset">复位视角</AppButton>
    </div>
    <p class="hint">拖动旋转 · 滚轮或双指缩放<span v-if="model === 'diesel'"> · 右键拖动或双指移动可平移</span> · 按钮可切换视角</p>
  </section>
</template>

<style scoped>
.inspection { padding: var(--space-4); border-bottom: 1px solid var(--border-light); }
.inspection-title { font-weight: var(--font-bold); color: var(--text-primary); }
.inspection-title span { display: inline-block; margin-left: var(--space-2); font-size: var(--text-xs); color: var(--text-secondary); font-weight: var(--font-normal); }
.part-buttons { display: flex; flex-wrap: wrap; gap: var(--space-2); margin: var(--space-3) 0; }
.part-description { color: var(--text-secondary); line-height: 1.7; min-height: 3.4em; }
.viewer { border: 1px solid var(--border-light); border-radius: var(--radius-lg); overflow: hidden; background: var(--bg-card); box-shadow: var(--shadow-card); }
.stage-wrap { position: relative; }
.stage { height: clamp(320px, 55vw, 530px); background: var(--model-stage); touch-action: none; }
.stage :deep(canvas) { display: block; width: 100%; height: 100%; }
.stage-label { position: absolute; top: var(--space-5); left: var(--space-5); color: var(--text-secondary); letter-spacing: .13em; font-size: var(--text-xs); pointer-events: none; }
.toolbar { display: flex; justify-content: center; flex-wrap: wrap; gap: var(--space-2); padding: var(--space-4); }
.hint { text-align: center; color: var(--text-secondary); font-size: var(--text-sm); padding: 0 var(--space-4) var(--space-5); }
.error { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-4); padding: var(--space-6); background: var(--model-stage); text-align: center; }
.external-viewer iframe { display: block; width: 100%; height: clamp(420px, 60vw, 620px); border: 0; background: var(--model-stage); }
.model-credit { padding: var(--space-3) var(--space-4); text-align: center; color: var(--text-secondary); font-size: var(--text-xs); }
.model-credit a { color: var(--color-primary-hover); }
@media (max-width: 480px) { .toolbar { gap: var(--space-1); padding: var(--space-3); } }
</style>
