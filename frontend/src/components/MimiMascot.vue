<!--
  MimiMascot.vue - Mimi 吉祥物统一组件
  用途: 全站统一欢迎/陪伴/庆祝/头像/空状态的吉祥物展示与动效，避免页面分散实现。
  作者: english-app
  创建日期: 2026-07-30
-->
<script setup>
/**
 * @description Mimi 吉祥物统一组件：根据 variant 选择资源与动效 preset，提供贴纸感包装与尺寸体系。
 */
import { computed } from 'vue'
import { getMimiPreset } from '../utils/mimiMascotPresets'

import mascotWelcome from '../assets/mascot/mascot-welcome.jpg'
import mascotCompanion from '../assets/mascot/mascot-companion.jpg'
import mascotCelebrate from '../assets/mascot/mascot-celebrate.jpg'

const props = defineProps({
  /** 场景类型：welcome/companion/celebrate/avatar/empty */
  variant: {
    type: String,
    default: 'companion'
  },
  /** 尺寸：sm/md/lg */
  size: {
    type: String,
    default: 'md'
  },
  /** 是否显示贴纸包装（头像场景可关闭） */
  hasSticker: {
    type: Boolean,
    default: true
  },
  /** 自定义 alt（无则按场景默认） */
  alt: {
    type: String,
    default: ''
  }
})

const preset = computed(() => getMimiPreset(props.variant))

const src = computed(() => {
  if (props.variant === 'welcome') return mascotWelcome
  if (props.variant === 'celebrate') return mascotCelebrate
  // companion/avatar/empty 默认使用 companion 图
  return mascotCompanion
})

const altText = computed(() => {
  if (props.alt) return props.alt
  if (props.variant === 'welcome') return '小老鼠 Mimi 欢迎'
  if (props.variant === 'celebrate') return '小老鼠 Mimi 庆祝'
  if (props.variant === 'avatar') return 'Mimi 头像'
  return '小老鼠 Mimi 陪伴'
})

const classes = computed(() => {
  return [
    'mimi-outer',
    `mimi--${props.size}`,
    `mimi--${props.variant}`,
    `mimi--motion-${preset.value.motion}`,
    `mimi--secondary-${preset.value.secondaryMotion}`,
    `mimi--aura-${preset.value.auraMotion}`,
    { 'mimi--sticker': props.hasSticker }
  ]
})
</script>

<template>
  <span class="mimi-wrap" :class="classes">
    <span class="mimi-aura"></span>
    <span class="mimi-inner">
      <img class="mimi-img" :src="src" :alt="altText" />
    </span>
  </span>
</template>

<style scoped>
/* 容器：统一占位与可控的贴纸包装 */
.mimi-wrap {
  --mimi-bounce-distance: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
}

/* 图片：尽量保持清晰，不做裁切 */
.mimi-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: var(--radius-md);
  display: block;
  box-shadow: var(--shadow-soft);
}

/* 尺寸体系 */
.mimi--sm { width: 56px; height: 56px; }
.mimi--md { width: 80px; height: 80px; }
.mimi--lg { width: 160px; height: 160px; }

.mimi-inner {
  width: 100%;
  height: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transform-origin: 50% 65%;
}

.mimi-aura {
  position: absolute;
  inset: -10px;
  border-radius: calc(var(--radius-md) + 18px);
  background: radial-gradient(circle at 50% 50%, var(--mascot-glow) 0%, rgba(255, 255, 255, 0) 72%);
  opacity: 0;
  pointer-events: none;
  z-index: 0;
}

/* 贴纸感：通过伪元素做“白边 + 柔光”，即使 jpg 也更像品牌角色 */
.mimi--sticker::before {
  content: '';
  position: absolute;
  inset: -6px;
  border-radius: calc(var(--radius-md) + 10px);
  background: radial-gradient(circle at 30% 20%, var(--mascot-glow) 0%, rgba(255, 255, 255, 0) 60%),
              linear-gradient(180deg, rgba(255, 255, 255, 0.88) 0%, rgba(255, 255, 255, 0.68) 100%);
  filter: blur(0.2px);
  z-index: 0;
}

.mimi--sticker::after {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: calc(var(--radius-md) + 6px);
  box-shadow:
    0 0 0 2px var(--mascot-outline),
    0 6px 18px rgba(107, 124, 255, 0.14);
  z-index: 0;
}

.mimi-inner,
.mimi-img { position: relative; z-index: 1; }

/* 动效（遵循 prefers-reduced-motion） */
@media (prefers-reduced-motion: no-preference) {
  .mimi--motion-float { animation: mimiFloat var(--duration-mascot-float) var(--ease-smooth) infinite; }
  .mimi--motion-breathe { animation: mimiBreathe var(--duration-mascot-breathe) var(--ease-smooth) infinite; }
  .mimi--motion-bounce { animation: mimiBounce 1080ms var(--ease-bounce) infinite; }

  .mimi--secondary-sway .mimi-inner { animation: mimiSway 2600ms ease-in-out infinite; }
  .mimi--secondary-nod .mimi-inner { animation: mimiNod 2200ms ease-in-out infinite; }
  .mimi--secondary-wiggle .mimi-inner { animation: mimiWiggle 900ms ease-in-out infinite; }

  .mimi--aura-pulse .mimi-aura { animation: mimiAuraPulse 1400ms ease-in-out infinite; }
}

@keyframes mimiFloat {
  0%, 100% { transform: translateY(0) rotate(-1deg); }
  50% { transform: translateY(-8px) rotate(1deg); }
}

@keyframes mimiBreathe {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-3px) scale(1.03); }
}

@keyframes mimiBounce {
  0%, 100% { transform: translateY(0) scale(1); }
  35% { transform: translateY(calc(var(--mimi-bounce-distance) * -1)) scale(1.02); }
  65% { transform: translateY(0) scale(0.985); }
}

@keyframes mimiSway {
  0%, 100% { transform: rotate(-2deg); }
  50% { transform: rotate(2deg); }
}

@keyframes mimiNod {
  0%, 100% { transform: rotate(0deg) translateY(0); }
  30% { transform: rotate(-1deg) translateY(-1px); }
  60% { transform: rotate(1deg) translateY(1px); }
}

@keyframes mimiWiggle {
  0%, 100% { transform: rotate(-3deg) scale(1); }
  25% { transform: rotate(3deg) scale(1.02); }
  50% { transform: rotate(-1deg) scale(1); }
  75% { transform: rotate(4deg) scale(1.03); }
}

@keyframes mimiAuraPulse {
  0%, 100% { opacity: 0.2; transform: scale(0.96); }
  50% { opacity: 0.55; transform: scale(1.08); }
}

/* 移动端：缩小 20%（项目规范） */
@media (max-width: 480px) {
  .mimi--lg { width: 128px; height: 128px; }
  .mimi--md { width: 64px; height: 64px; }
  .mimi-wrap { --mimi-bounce-distance: 7px; }
}
</style>
