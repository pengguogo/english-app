<!--
  StarBar.vue - 星级评价展示组件
  用途: 根据得分展示星星(SVG),面向儿童的英语学习场景。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
import { computed } from 'vue'

/**
 * 组件 props:
 * @param {Number} stars - 当前获得的星星数量(0 ~ max)
 * @param {Number} max - 最大星星数量,默认 3
 * @param {String} size - 星星尺寸: sm | md | lg
 */
const props = defineProps({
  stars: { type: Number, default: 0 },
  max: { type: Number, default: 3 },
  size: { type: String, default: 'md' }
})

/* 根据当前星星数与最大值生成展示数组: true=实心星, false=空心星 */
const displayStars = computed(() => {
  const arr = []
  for (let i = 0; i < props.max; i++) {
    arr.push(i < props.stars)
  }
  return arr
})
</script>

<template>
  <div class="star-bar" :class="`size-${size}`">
    <svg
      v-for="(filled, index) in displayStars"
      :key="index"
      class="star"
      :class="{ filled, 'star-pop': filled }"
      viewBox="0 0 24 24"
      fill="none"
    >
      <!-- 星星路径 -->
      <path
        d="M12 2L14.85 8.4L22 9.3L16.5 14.1L18.18 21L12 17.4L5.82 21L7.5 14.1L2 9.3L9.15 8.4L12 2Z"
        :fill="filled ? 'var(--color-accent)' : 'none'"
        :stroke="filled ? 'var(--color-orange)' : 'var(--border-light)'"
        stroke-width="1.5"
        stroke-linejoin="round"
      />
    </svg>
  </div>
</template>

<style scoped>
/* 星星容器: 水平排列 + 间距 */
.star-bar {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
}

/* 尺寸: 小(sm) / 中(md) / 大(lg) */
.star-bar.size-sm .star { width: 18px; height: 18px; }
.star-bar.size-md .star { width: 28px; height: 28px; }
.star-bar.size-lg .star { width: 40px; height: 40px; }

/* 实心星弹入动效: 缩放 + 淡入,带弹跳缓动 */
@media (prefers-reduced-motion: no-preference) {
  .star.star-pop {
    animation: starPop var(--duration-normal) var(--ease-bounce) both;
  }
}

@keyframes starPop {
  0% {
    transform: scale(0) rotate(-180deg);
    opacity: 0;
  }
  60% {
    transform: scale(1.2) rotate(10deg);
    opacity: 1;
  }
  100% {
    transform: scale(1) rotate(0);
    opacity: 1;
  }
}
</style>
