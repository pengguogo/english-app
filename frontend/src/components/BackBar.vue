<!--
  BackBar.vue - 统一返回栏组件
  用途: 提供统一的返回导航栏,左侧返回箭头 + 可选标题 + 右侧插槽。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
/**
 * 组件 props:
 * @param {String} title - 可选的居中标题
 */
defineProps({
  title: { type: String, default: '' }
})

/**
 * 组件 emits:
 * @event back - 点击返回按钮时触发
 */
const emit = defineEmits(['back'])
</script>

<template>
  <div class="back-bar">
    <button class="back-btn" @click="emit('back')">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
        <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2.5"
              stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>返回</span>
    </button>
    <h2 v-if="title" class="bar-title">{{ title }}</h2>
    <div class="bar-right">
      <slot name="right" />
    </div>
  </div>
</template>

<style scoped>
/* 返回栏: 左右分布,垂直居中 */
.back-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-3) 0;
  margin-bottom: var(--space-4);
}

/* 返回按钮: 透明底 + 主色文字 + 箭头图标 */
.back-btn {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--color-primary);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  transition: background var(--duration-fast) var(--ease-smooth);
}
.back-btn:hover {
  background: rgba(107, 124, 255, 0.08);
}

/* 居中标题 */
.bar-title {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

/* 右侧插槽区域 */
.bar-right {
  display: flex;
  align-items: center;
}
</style>
