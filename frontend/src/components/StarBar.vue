<!--
  StarBar.vue - 星级评价展示组件
  用途:根据得分展示星星(⭐)与空星(☆),面向儿童的英语学习场景。
  作者:english-app
  创建日期:2026-07-20
-->
<script setup>
import { computed } from 'vue'

// 组件 props:
// - stars: 当前获得的星星数量(0 ~ max)
// - max:   最大星星数量,默认 3
const props = defineProps({
  stars: { type: Number, default: 0 },
  max: { type: Number, default: 3 }
})

// 根据当前星星数与最大值生成展示数组:
// 已获得的显示实心星 ⭐,未获得的显示空心星 ☆
const displayStars = computed(() => {
  const arr = []
  for (let i = 0; i < props.max; i++) {
    arr.push(i < props.stars ? '⭐' : '☆')
  }
  return arr
})
</script>

<template>
  <span class="star-bar">{{ displayStars.join('') }}</span>
</template>

<style scoped>
/* 星星展示:大字体 + 字间距,便于儿童识别 */
.star-bar {
  font-size: 1.5rem;
  letter-spacing: 4px;
}
</style>
