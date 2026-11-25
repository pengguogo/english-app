<!--
  CompanionMouse.vue - 学习伙伴小老鼠「豆豆」
  用途: 渲染内联 SVG 小老鼠，按 state 切换表情/耳朵/手势，色值映射到 tokens.css。
  作者: ea-mascot
  创建日期: 2026-11-06
-->
<script setup>
import { computed, useId } from 'vue'
import mouseSvg from '../../assets/companion/mouse.svg?raw'

const STATE_LABELS = {
  idle: '在等你',
  listening: '正在听你说',
  thinking: '正在想',
  cheer: '在为你加油',
  speaking: '正在讲解',
  hint: '在给你提示',
  wave: '在打招呼',
  safety: '有安全提示'
}

const props = defineProps({
  state: { type: String, default: 'idle' },
  /** 宽高像素 */
  size: { type: Number, default: 56 },
  gesture: { type: String, default: '' },
  still: Boolean,
  flight: Boolean
})

const svg = mouseSvg.replaceAll('UID', useId())
const safeState = computed(() => (Object.hasOwn(STATE_LABELS, props.state) ? props.state : 'idle'))
const ariaLabel = computed(() => `学习伙伴小老鼠，${STATE_LABELS[safeState.value]}`)
</script>

<template>
  <!-- SVG 为本地静态资源（?raw 编译期内联），不含用户输入，v-html 安全 -->
  <span
    role="img"
    :aria-label="ariaLabel"
    :class="['companion-mouse', `cm-s-${safeState}`, `cm-a-${gesture}`, { 'cm-still': still, 'cm-flight': flight }]"
    :style="{ width: `${size}px`, height: `${size}px` }"
    v-html="svg"
  />
</template>

<style scoped>
.companion-mouse {
  display: inline-block;
  flex: none;
  line-height: 0;
  /* 公共色 */
  --cm-ink: var(--pilot-ink);
  --cm-white: var(--pilot-cream);
  --cm-fx: var(--color-primary);
  --cm-accent: var(--color-accent);
  --cm-alert: var(--color-orange);
  --cm-body: var(--pilot-fur);
  --cm-highlight: var(--pilot-cream);
  --cm-fur-shadow: var(--pilot-shade);
  --cm-body-dark: var(--pilot-shade);
  --cm-inner: var(--pilot-cream);
  --cm-paw: var(--pilot-rim);
  --cm-nose: var(--pilot-shade);
  --cm-light: var(--cabin-cyan);
}
.companion-mouse :deep(svg) {
  width: 100%;
  height: 100%;
  overflow: visible;
}
/* 状态切换：只显示当前状态的元素 */
.companion-mouse :deep([data-s]) {
  display: none;
}
.cm-s-speaking :deep([data-s='idle']),
.cm-s-wave :deep([data-s='idle']),
.cm-s-hint :deep([data-s='idle']),
.cm-s-idle :deep([data-s='idle']),
.cm-s-listening :deep([data-s='listening']),
.cm-s-thinking :deep([data-s='thinking']),
.cm-s-cheer :deep([data-s='cheer']),
.cm-s-safety :deep([data-s='safety']) {
  display: inline;
}
.companion-mouse :deep(.cm-head),
.companion-mouse :deep(.cm-ear-l),
.companion-mouse :deep(.cm-ear-r),
.companion-mouse :deep(.cm-blink) {
  transform-box: fill-box;
  transform-origin: center;
}
.companion-mouse :deep(.cm-ear-l) { transform-origin: 80% 90%; }
.companion-mouse :deep(.cm-ear-r) { transform-origin: 20% 90%; }
.companion-mouse :deep(.cm-head) { transform-origin: 50% 100%; }
/* 主动反馈有静止姿势，减少动态效果时也看得懂。 */
.companion-mouse :deep(.cm-mouth),
.companion-mouse :deep(.cm-wave-pose),
.companion-mouse :deep(.cm-point),
.cm-s-speaking :deep(path[data-s='idle']),
.cm-s-wave :deep(.cm-rest-paws),
.cm-s-hint :deep(.cm-rest-paws) { display: none; }
.cm-s-speaking :deep(.cm-mouth),
.cm-s-wave :deep(.cm-wave-pose),
.cm-s-hint :deep(.cm-point) { display: inline; }
.companion-mouse :deep(.cm-equipment-light) { fill: var(--cm-light); }
.cm-s-thinking { --cm-light: var(--cabin-violet); }
.cm-s-cheer { --cm-light: var(--color-success); }
.cm-s-hint, .cm-s-safety { --cm-light: var(--cabin-warning); }
.cm-s-listening :deep(.cm-head) { transform: translateY(1px) scale(1.04); }
.cm-s-listening :deep(.cm-ear-l) { transform: rotate(-10deg); }
.cm-s-listening :deep(.cm-ear-r) { transform: rotate(10deg); }
.cm-s-thinking :deep(.cm-head) { transform: rotate(-6deg); }
.cm-s-hint :deep(.cm-head) { transform: rotate(7deg) translateY(2px); }
.cm-s-wave :deep(.cm-wave) { transform-box: fill-box; transform-origin: 10% 95%; }

@media (prefers-reduced-motion: no-preference) {
  .cm-flight.cm-s-idle:not(.cm-still) { animation: cm-float 5.6s ease-in-out infinite; }
  .cm-flight :deep(.cm-head) { transition: transform .28s var(--ease-smooth); }
  .cm-s-listening :deep(.cm-fx) { animation: cm-signal 1.6s ease-in-out infinite; }
  .cm-s-thinking :deep(.cm-dots > *) { animation: cm-think 1.5s ease-in-out infinite; }
  .cm-s-thinking :deep(.cm-dots > :nth-child(2)) { animation-delay: .15s; }
  .cm-s-thinking :deep(.cm-dots > :nth-child(3)) { animation-delay: .3s; }
  .cm-s-speaking :deep(.cm-mouth) { transform-box: fill-box; transform-origin: center; animation: cm-talk .62s ease-in-out infinite; }
  .cm-s-cheer :deep(svg) { transform-origin: center 86%; animation: cm-hop .78s var(--ease-smooth); }
  .cm-s-cheer :deep(.cm-stars) { transform-origin: center; animation: cm-stars 1.2s ease-out both; }
  .cm-s-wave :deep(.cm-wave) { animation: cm-wave 1.05s var(--ease-smooth) both; }
  .cm-s-hint :deep(.cm-point) { transform-origin: 63px 80px; animation: cm-point .62s ease-out; }
  .cm-a-blink :deep(.cm-blink) { animation: cm-blink .34s ease-in-out; }
  .cm-a-ear :deep(.cm-ear-r) { animation: cm-twitch .28s ease-in-out 2; }
  .cm-a-sniff :deep(.cm-nose),
  .cm-a-sniff :deep(.cm-whiskers) { animation: cm-sniff .28s ease-in-out 3; }
  .cm-a-stretch :deep(svg) { transform-origin: center bottom; animation: cm-stretch 1.05s ease-in-out; }
  .cm-a-tail :deep(.cm-tail) { transform-origin: 68px 90px; animation: cm-twitch .4s ease-in-out 2; }
  .cm-a-tilt :deep(.cm-head) { animation: cm-tilt 1s ease-in-out; }
  .cm-still :deep(*) { animation: none !important; transition: none !important; }
}
@keyframes cm-float { 50% { transform: translateY(-2.5%) rotate(-1deg); } }
@keyframes cm-signal { 0%, 100% { opacity: .54; } 45% { opacity: 1; } }
@keyframes cm-think { 0%, 85%, 100% { opacity: .46; } 35% { opacity: 1; } }
@keyframes cm-talk { 0%, 62%, 100% { transform: scale(.84, .43); } 28%, 79% { transform: scale(1.04, 1.25); } }
@keyframes cm-hop {
  14% { transform: translateY(1%) scale(1.05, .94); }
  43% { transform: translateY(-7.5%) rotate(-6deg) scale(.96, 1.05); }
  75% { transform: scale(1.035, .965); }
}
@keyframes cm-stars {
  0% { opacity: 0; transform: scale(.76); }
  37% { opacity: 1; transform: scale(1.025); }
  83% { opacity: .72; }
  100% { opacity: .25; }
}
@keyframes cm-wave {
  0%, 100% { transform: rotate(5deg); }
  20%, 53%, 81% { transform: rotate(-23deg); }
  36%, 68% { transform: rotate(10deg); }
}
@keyframes cm-point { 0% { opacity: .65; transform: rotate(-15deg); } 64% { transform: rotate(5deg); } }
@keyframes cm-blink { 34%, 48% { transform: scaleY(.12); } 75% { transform: scaleY(.84); } }
@keyframes cm-twitch { 50% { transform: rotate(13deg); } }
@keyframes cm-sniff { 50% { transform: translateY(-1.8px); } }
@keyframes cm-stretch { 50% { transform: scale(.94, 1.09); } }
@keyframes cm-tilt { 36%, 62% { transform: rotate(12deg) translateY(1px); } }
</style>
