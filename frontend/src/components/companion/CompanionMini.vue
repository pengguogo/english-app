<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { loadMiniPet, loadMiniPetRun, paintMiniPet } from './miniPetSprites'

const props = defineProps({
  state: { type: String, required: true },
  gesture: { type: String, default: '' },
  still: Boolean
})
const canvas = ref(null)
const ready = ref(false)
const running = ref(false)
const position = ref(0)
const facing = ref(1)
const pose = computed(() => running.value ? 'running' : props.state)
let poses = []
let run = []
let context
let frame
let target = 0
let startTime = 0
let latestTime = 0

function draw(time = startTime) {
  if (context) paintMiniPet(context, poses, run, pose.value, Math.floor((time - startTime) / 78))
}

function step(time) {
  const distance = target - position.value
  if (Math.abs(distance) <= .012) {
    position.value = target
    stop()
    return
  }
  const seconds = Math.min((time - latestTime) / 1000, .05)
  latestTime = time
  position.value += distance * (1 - Math.exp(-seconds * 9))
  draw(time)
  frame = requestAnimationFrame(step)
}

function follow(x) {
  if (!ready.value || props.still || props.state !== 'idle') return
  target = x
  if (Math.abs(target - position.value) < .16) return
  facing.value = target < position.value ? 1 : -1
  if (running.value) return
  running.value = true
  startTime = latestTime = performance.now()
  frame = requestAnimationFrame(step)
}

function stop() {
  cancelAnimationFrame(frame)
  running.value = false
  draw()
}

watch([() => props.state, () => props.still], stop)
onMounted(async () => {
  try {
    run = []
    void loadMiniPetRun().then(frames => { run = frames }).catch(() => {})
    poses = await loadMiniPet()
    context = canvas.value?.getContext('2d')
    if (!context) return
    draw()
    ready.value = true
  } catch {
    // 精灵图不可用时保留原头像。
  }
})
onUnmounted(stop)
defineExpose({ follow })
</script>

<template>
  <span v-show="ready" class="mini-track" :data-pose="pose" :data-gesture="gesture" :class="{ still }" aria-hidden="true"
    :style="{ '--mini-position': `${50 + position * 19}%`, '--mini-facing': facing }">
    <canvas ref="canvas" width="256" height="256" />
  </span>
  <slot v-if="!ready" />
</template>

<style scoped>
.mini-track {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 1;
}
canvas {
  position: absolute;
  left: var(--mini-position);
  bottom: 10%;
  width: 66%;
  height: auto;
  transform-origin: center 90%;
  translate: -50% 0;
  scale: var(--mini-facing) 1;
}
@media (prefers-reduced-motion: no-preference) {
  .mini-track:not(.still)[data-pose='idle'] canvas { animation: mini-breathe 3s ease-in-out infinite; }
  .mini-track:not(.still)[data-pose='speaking'] canvas { animation: mini-speak 1.12s ease-in-out infinite; }
  .mini-track:not(.still):is([data-pose='wave'], [data-pose='cheer']) canvas { animation: mini-hop .78s var(--ease-smooth) both; }
  .mini-track:not(.still)[data-pose='idle'][data-gesture='tilt'] canvas { animation: mini-speak .92s ease-in-out; }
}
@keyframes mini-breathe { 50% { transform: scaleY(1.045); } }
@keyframes mini-speak { 35% { transform: rotate(-4deg) scaleY(.98); } 70% { transform: rotate(2deg) scaleY(1.015); } }
@keyframes mini-hop { 18% { transform: scale(1.07, .93); } 44% { transform: translateY(-15%) rotate(-5deg); } 78% { transform: scale(1.035, .96); } }
</style>
