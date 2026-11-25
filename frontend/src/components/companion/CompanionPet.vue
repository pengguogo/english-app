<script setup>
import { computed, onMounted, onUnmounted, ref, shallowRef, watch } from 'vue'
import AppButton from '../AppButton.vue'
import CompanionMouse from './CompanionMouse.vue'

const props = defineProps({
  state: { type: String, default: 'idle' },
  preview: Boolean
})

const idleActions = ['blink', 'ear', 'sniff', 'stretch', 'tail']
const visible = ref(true)
const reduced = ref(false)
const action = ref('')
const response = ref('')
const compare = ref(false)
const canvas = ref(null)
const unavailable = ref(false)
const model = shallowRef(null)
const state = computed(() => response.value === 'wave' ? 'wave' : props.state)
const still = computed(() => !visible.value || reduced.value)
const portState = computed(() => response.value || state.value)
const greeting = computed(() => response.value === 'wave' ? '你好呀！' : response.value === 'tilt' ? '嗯？我在这儿。' : '')

let media
let timer
let lastIdle
let nextResponse = 'wave'

function clearAction() {
  clearTimeout(timer)
  action.value = ''
  response.value = ''
}

function scheduleIdle() {
  if (props.state !== 'idle' || still.value) return
  clearTimeout(timer)
  timer = setTimeout(() => {
    const choices = idleActions.filter(item => item !== lastIdle)
    lastIdle = choices[Math.floor(Math.random() * choices.length)]
    action.value = lastIdle
    timer = setTimeout(() => { action.value = ''; scheduleIdle() }, 1200)
  }, 3800 + Math.random() * 4400)
}

function greet() {
  if (props.state !== 'idle' || response.value) return
  clearAction()
  response.value = nextResponse
  action.value = nextResponse === 'tilt' && !still.value ? 'tilt' : ''
  nextResponse = nextResponse === 'wave' ? 'tilt' : 'wave'
  timer = setTimeout(() => { clearAction(); scheduleIdle() }, 1300)
}

function syncMotion() {
  visible.value = !document.hidden
  reduced.value = media.matches
}

onMounted(() => {
  media = window.matchMedia('(prefers-reduced-motion: reduce)')
  document.addEventListener('visibilitychange', syncMotion)
  media.addEventListener('change', syncMotion)
  syncMotion()
  if (!still.value) scheduleIdle()
})
watch([() => props.state, still], () => { clearAction(); scheduleIdle() })
onUnmounted(() => {
  clearAction()
  document.removeEventListener('visibilitychange', syncMotion)
  media?.removeEventListener('change', syncMotion)
})

// 对比区出现后才下载 Three.js 场景。关闭或离开首页立即释放资源。
watch(canvas, async (element, _, onCleanup) => {
  if (!element) return
  let active = true
  onCleanup(() => {
    active = false
    model.value?.dispose()
    model.value = null
  })
  unavailable.value = false
  try {
    const { createMousePreview } = await import('./mousePreview.js')
    if (active) model.value = createMousePreview(element)
  } catch {
    if (active) unavailable.value = true
  }
})
watch([model, state, action, visible, reduced], () => {
  model.value?.update({ state: state.value, action: action.value, visible: visible.value, reduced: reduced.value })
})

function toggleCompare() {
  unavailable.value = false
  compare.value = !compare.value
}
</script>

<template>
  <div class="pet" :class="{ comparing: preview && compare, 'motion-off': still }"
    :data-state="state" :data-feedback="portState">
    <div class="pet-pair">
      <AppButton variant="ghost" class="pet-button pet-port" aria-label="和豆豆打招呼"
        :aria-disabled="props.state !== 'idle' || !!response" @click="greet">
        <CompanionMouse :state="state" :gesture="action" :still="still" :size="88" flight />
      </AppButton>
      <div v-if="preview && compare" class="pet-model pet-port">
        <canvas v-if="!unavailable" ref="canvas" role="img" aria-label="Three.js 立体豆豆"
          @webglcontextlost="unavailable = true" />
        <span v-else role="status">当前浏览器无法显示 3D</span>
      </div>
    </div>
    <span class="pet-greeting" role="status">{{ greeting }}</span>
    <AppButton v-if="preview" variant="ghost" class="preview-button" :aria-expanded="compare"
      @click="toggleCompare">{{ compare ? '关闭 3D 对比' : '对比 3D 豆豆' }}</AppButton>
  </div>
</template>

<style scoped>
.pet {
  --port-light: var(--cabin-cyan);
  --port-size: clamp(112px, 35vw, 220px);
  position: relative;
  text-align: center;
}
.pet[data-state='thinking'] { --port-light: var(--cabin-violet); }
.pet[data-state='hint'], .pet[data-state='safety'] { --port-light: var(--cabin-warning); }
.pet[data-state='cheer'] { --port-light: var(--color-success); }
.pet.comparing { --port-size: clamp(54px, 16vw, 124px); }
.pet-pair { display: flex; justify-content: center; gap: var(--space-1); }
.pet-button, .pet-model { width: var(--port-size); height: var(--port-size); }
.pet .pet-button.app-btn, .pet-model {
  position: relative;
  overflow: hidden;
  padding: 0;
  border: 2px solid var(--port-light);
  border-radius: var(--radius-pill);
  background:
    radial-gradient(circle at 48% 103%, var(--port-light), transparent 31%),
    repeating-linear-gradient(0deg, transparent 0 12px, color-mix(in srgb, var(--port-light) 14%, transparent) 13px 14px),
    radial-gradient(circle, var(--cabin-planet), var(--cabin-space) 78%);
  box-shadow: 0 0 18px color-mix(in srgb, var(--port-light) 45%, transparent),
    inset 0 0 0 5px var(--cabin-recess);
}
.pet .pet-port::before {
  position: absolute;
  inset: 8%;
  border: 1px dashed color-mix(in srgb, var(--port-light) 48%, transparent);
  border-radius: inherit;
  pointer-events: none;
  content: '';
}
.pet .pet-port::after {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  pointer-events: none;
  content: '';
}
.pet-button :deep(.companion-mouse) { width: 88% !important; height: 88% !important; z-index: 1; }
.pet-model { display: grid; place-items: center; color: var(--cabin-text); font-size: var(--text-xs); }
.pet-model canvas { width: 100%; height: 100%; }
.pet-greeting:not(:empty) {
  position: absolute;
  z-index: 2;
  bottom: 4px;
  left: 50%;
  translate: -50% 0;
  white-space: nowrap;
  padding: var(--space-1) var(--space-2);
  border: 1px solid var(--cabin-border);
  border-radius: var(--radius-sm);
  background: var(--cabin-panel);
  color: var(--cabin-cyan);
  font-size: var(--text-xs);
}
.pet:has(.preview-button) .pet-greeting { bottom: var(--space-12); }
.preview-button.app-btn { margin-top: var(--space-1); padding: var(--space-1) var(--space-2); font-size: var(--text-xs); }
@media (max-width: 365px) {
  .pet.comparing .pet-pair { flex-wrap: wrap; }
}
</style>
<style scoped src="./companionPortMotion.css"></style>
