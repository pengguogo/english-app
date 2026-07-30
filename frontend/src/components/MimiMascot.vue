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
    'mimi',
    `mimi--${props.size}`,
    `mimi--${props.variant}`,
    `mimi--motion-${preset.value.motion}`,
    { 'mimi--sticker': props.hasSticker }
  ]
})
</script>

<template>
  <span class="mimi-wrap" :class="classes">
    <img class="mimi-img" :src="src" :alt="altText" />
  </span>
</template>

<style scoped>
/* 容器：统一占位与可控的贴纸包装 */
.mimi-wrap {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
  transform-origin: 50% 65%;
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

.mimi-img { position: relative; z-index: 1; }

/* 动效（遵循 prefers-reduced-motion） */
@media (prefers-reduced-motion: no-preference) {
  .mimi--motion-float { animation: mimiFloat var(--duration-mascot-float) var(--ease-smooth) infinite; }
  .mimi--motion-breathe { animation: mimiBreathe var(--duration-mascot-breathe) var(--ease-smooth) infinite; }
  .mimi--motion-celebrate { animation: mimiCelebrate var(--duration-mascot-celebrate) var(--ease-bounce) both; }
}

@keyframes mimiFloat {
  0%, 100% { transform: translateY(0) rotate(-1deg); }
  50% { transform: translateY(-8px) rotate(1deg); }
}

@keyframes mimiBreathe {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-3px) scale(1.03); }
}

@keyframes mimiCelebrate {
  0% { transform: scale(0.75) rotate(-10deg); opacity: 0; }
  55% { transform: scale(1.12) rotate(6deg); opacity: 1; }
  100% { transform: scale(1) rotate(0); opacity: 1; }
}

/* 移动端：缩小 20%（项目规范） */
@media (max-width: 480px) {
  .mimi--lg { width: 128px; height: 128px; }
  .mimi--md { width: 64px; height: 64px; }
}
</style>

