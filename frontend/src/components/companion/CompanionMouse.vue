<!--
  CompanionMouse.vue - 学习伙伴小老鼠「豆豆」
  用途: 渲染内联 SVG 小老鼠，按 state 切换表情/耳朵/手势，色值映射到 tokens.css。
  作者: ea-mascot
  创建日期: 2026-11-06
-->
<script setup>
import { computed } from 'vue'
import mouseSvg from '../../assets/companion/mouse.svg?raw'

const STATE_LABELS = {
  idle: '在等你',
  listening: '正在听你说',
  thinking: '正在想',
  cheer: '在为你加油',
  safety: '有安全提示'
}

const props = defineProps({
  /** 状态：idle / listening / thinking / cheer / safety，非法值回退 idle */
  state: { type: String, default: 'idle' },
  /** 宽高像素 */
  size: { type: Number, default: 56 }
})

const safeState = computed(() => (STATE_LABELS[props.state] ? props.state : 'idle'))
const ariaLabel = computed(() => `学习伙伴小老鼠，${STATE_LABELS[safeState.value]}`)
</script>

<template>
  <!-- SVG 为本地静态资源（?raw 编译期内联），不含用户输入，v-html 安全 -->
  <span
    role="img"
    :aria-label="ariaLabel"
    :class="['companion-mouse', `cm-s-${safeState}`]"
    :style="{ width: `${size}px`, height: `${size}px` }"
    v-html="mouseSvg"
  />
</template>

<style scoped>
.companion-mouse {
  display: inline-block;
  flex: none;
  line-height: 0;
  /* 公共色 */
  --cm-ink: var(--text-primary);
  --cm-white: var(--bg-card);
  --cm-fx: var(--color-primary);
  --cm-accent: var(--color-accent);
  --cm-alert: var(--color-orange);
  /* 豆豆：粉色圆润 */
  --cm-body: var(--color-pink);
  --cm-body-dark: var(--color-warning);
  --cm-inner: var(--bg-page);
  --cm-belly: var(--bg-page);
  --cm-paw: var(--bg-page);
  --cm-nose: var(--color-warning);
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
.companion-mouse :deep(.cm-blink),
.companion-mouse :deep(.cm-dots > *),
.companion-mouse :deep(.cm-stars > *),
.companion-mouse :deep(.cm-fx) {
  transform-box: fill-box;
  transform-origin: center;
}
.companion-mouse :deep(.cm-ear-l) { transform-origin: 80% 90%; }
.companion-mouse :deep(.cm-ear-r) { transform-origin: 20% 90%; }
.companion-mouse :deep(.cm-head) { transform-origin: 50% 100%; }
/* 倾听时耳朵竖起 */
.cm-s-listening :deep(.cm-ear-l) { transform: rotate(-8deg) scale(1.06); }
.cm-s-listening :deep(.cm-ear-r) { transform: rotate(8deg) scale(1.06); }
/* 思考时歪头 */
.cm-s-thinking :deep(.cm-head) { transform: rotate(-6deg); }

@media (prefers-reduced-motion: no-preference) {
  .companion-mouse :deep(.cm-blink) { animation: cm-blink 4s infinite; }
  .cm-s-idle :deep(.cm-ear-r) { animation: cm-ear-twitch 5s infinite; }
  .cm-s-listening :deep(.cm-ear-l) { animation: cm-ear-l 1.2s var(--ease-smooth) infinite; }
  .cm-s-listening :deep(.cm-ear-r) { animation: cm-ear-r 1.2s var(--ease-smooth) infinite; }
  .cm-s-listening :deep(.cm-fx) { animation: cm-pulse 1.2s infinite; }
  .cm-s-thinking :deep(.cm-dots > *) { animation: cm-dot 1.2s infinite; }
  .cm-s-thinking :deep(.cm-dots > :nth-child(2)) { animation-delay: 0.2s; }
  .cm-s-thinking :deep(.cm-dots > :nth-child(3)) { animation-delay: 0.4s; }
  .cm-s-cheer :deep(.cm-head) { animation: cm-nod 0.9s var(--ease-bounce) infinite; }
  .cm-s-cheer :deep(.cm-stars > *) { animation: cm-twinkle 0.9s infinite alternate; }
  .cm-s-safety :deep(.cm-badge) { animation: cm-pulse 1.6s infinite; }
}

@keyframes cm-blink {
  0%, 92%, 100% { transform: scaleY(1); }
  95% { transform: scaleY(0.1); }
}
@keyframes cm-ear-twitch {
  0%, 88%, 100% { transform: rotate(0); }
  92% { transform: rotate(10deg); }
  96% { transform: rotate(-4deg); }
}
@keyframes cm-ear-l {
  0%, 100% { transform: rotate(-8deg) scale(1.06); }
  50% { transform: rotate(-14deg) scale(1.08); }
}
@keyframes cm-ear-r {
  0%, 100% { transform: rotate(8deg) scale(1.06); }
  50% { transform: rotate(14deg) scale(1.08); }
}
@keyframes cm-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.45; }
}
@keyframes cm-dot {
  0%, 100% { transform: translateY(0); opacity: 0.5; }
  50% { transform: translateY(-3px); opacity: 1; }
}
@keyframes cm-nod {
  0%, 100% { transform: rotate(0) translateY(0); }
  40% { transform: rotate(0) translateY(3px); }
}
@keyframes cm-twinkle {
  from { transform: scale(0.7); }
  to { transform: scale(1.15); }
}
</style>
