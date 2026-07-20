<!--
  AppButton.vue - 统一按钮组件
  用途: 提供统一的按钮样式,支持多种变体(primary/success/warning/ghost)和尺寸(md/lg)。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
/**
 * 组件 props:
 * @param {String} variant - 按钮变体: primary | success | warning | ghost
 * @param {String} size - 按钮尺寸: md | lg
 * @param {Boolean} block - 是否撑满父容器宽度
 * @param {Boolean} disabled - 是否禁用
 */
const props = defineProps({
  variant: { type: String, default: 'primary' },
  size: { type: String, default: 'md' },
  block: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false }
})
</script>

<template>
  <button
    class="app-btn"
    :class="[`variant-${variant}`, `size-${size}`, { block, disabled }]"
    :disabled="disabled"
  >
    <slot />
  </button>
</template>

<style scoped>
/* 按钮基础: 胶囊形 + 按压回弹动效 */
.app-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  border-radius: var(--radius-pill);
  font-weight: var(--font-medium);
  transition: transform var(--duration-fast) var(--ease-bounce),
              box-shadow var(--duration-fast) var(--ease-smooth),
              background var(--duration-fast) var(--ease-smooth);
  white-space: nowrap;
  user-select: none;
}

/* 尺寸: 中号(md)与大号(lg) */
.app-btn.size-md {
  padding: var(--space-3) var(--space-5);
  font-size: var(--text-base);
}
.app-btn.size-lg {
  padding: var(--space-4) var(--space-6);
  font-size: var(--text-lg);
}

/* 撑满宽度 */
.app-btn.block {
  width: 100%;
}

/* ===== 变体配色 ===== */

/* 主色按钮: 蓝紫渐变 */
.app-btn.variant-primary {
  background: var(--gradient-primary);
  color: white;
  box-shadow: 0 4px 12px rgba(107, 124, 255, 0.3);
}
.app-btn.variant-primary:not(:disabled):hover {
  box-shadow: 0 6px 16px rgba(107, 124, 255, 0.4);
}

/* 成功按钮: 绿色渐变 */
.app-btn.variant-success {
  background: var(--gradient-success);
  color: white;
  box-shadow: 0 4px 12px rgba(76, 217, 100, 0.3);
}
.app-btn.variant-success:not(:disabled):hover {
  box-shadow: 0 6px 16px rgba(76, 217, 100, 0.4);
}

/* 警示按钮: 珊瑚红 */
.app-btn.variant-warning {
  background: var(--color-warning);
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}
.app-btn.variant-warning:not(:disabled):hover {
  filter: brightness(1.05);
}

/* 幽灵按钮: 透明底 + 主色文字 */
.app-btn.variant-ghost {
  background: transparent;
  color: var(--color-primary);
}
.app-btn.variant-ghost:not(:disabled):hover {
  background: rgba(107, 124, 255, 0.08);
}

/* 按压回弹动效 */
.app-btn:not(:disabled):active {
  transform: scale(0.96);
}

/* 禁用态 */
.app-btn:disabled,
.app-btn.disabled {
  background: var(--text-tertiary) !important;
  color: white !important;
  box-shadow: none !important;
  cursor: not-allowed;
  opacity: 0.6;
}
</style>
