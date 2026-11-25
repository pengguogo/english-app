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
  still: Boolean
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
    :class="['companion-mouse', `cm-s-${safeState}`, `cm-a-${gesture}`, { 'cm-still': still }]"
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
.companion-mouse :deep(.cm-wave),
.companion-mouse :deep(.cm-point),
.cm-s-speaking :deep(path[data-s='idle']),
.cm-s-wave :deep(.cm-rest-paws),
.cm-s-hint :deep(.cm-rest-paws) { display: none; }
.cm-s-speaking :deep(.cm-mouth),
.cm-s-wave :deep(.cm-wave),
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
.cm-s-wave :deep(.cm-wave) { transform-box: fill-box; transform-origin: 78% 90%; }

@media (prefers-reduced-motion: no-preference) {
  .cm-s-speaking :deep(.cm-mouth) { transform-box: fill-box; transform-origin: center; animation: cm-talk .47s ease-in-out infinite alternate; }
  .cm-s-cheer:not(.cm-still) :deep(svg) { animation: cm-hop .6s ease-out; }
  .cm-s-wave :deep(.cm-wave) { animation: cm-wave .3s ease-in-out 3 alternate; }
  .cm-a-blink :deep(.cm-blink) { animation: cm-blink .26s ease-in-out; }
  .cm-a-ear :deep(.cm-ear-r) { animation: cm-twitch .28s ease-in-out 2; }
  .cm-a-sniff :deep(.cm-nose),
  .cm-a-sniff :deep(.cm-whiskers) { animation: cm-sniff .28s ease-in-out 3; }
  .cm-a-stretch :deep(svg) { transform-origin: center bottom; animation: cm-stretch 1.05s ease-in-out; }
  .cm-a-tail :deep(.cm-tail) { transform-origin: 68px 90px; animation: cm-twitch .4s ease-in-out 2; }
  .cm-a-tilt :deep(.cm-head) { animation: cm-tilt 1s ease-in-out; }
  .cm-still :deep(*) { animation: none !important; }
}
@keyframes cm-talk { 0%, 60% { transform: scaleY(.42); } 36%, 100% { transform: scaleY(1.3); } }
@keyframes cm-hop { 42% { transform: translateY(-7px) rotate(-5deg); } 73% { transform: translateY(1px); } }
@keyframes cm-wave { 45% { transform: rotate(-15deg); } }
@keyframes cm-blink { 50% { transform: scaleY(.12); } }
@keyframes cm-twitch { 50% { transform: rotate(13deg); } }
@keyframes cm-sniff { 50% { transform: translateY(-1.8px); } }
@keyframes cm-stretch { 50% { transform: scale(.94, 1.09); } }
@keyframes cm-tilt { 40%, 66% { transform: rotate(9deg); } }
</style>
