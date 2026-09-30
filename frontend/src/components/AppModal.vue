<!--
  AppModal.vue - 通用弹窗组件
  用途: 提供统一的弹窗容器(遮罩 + 内容面板),带标题栏与关闭按钮;
        移动端从底部滑出、桌面端居中显示,内容通过默认插槽分发。
  作者: english-app
  创建日期: 2026-09-30
-->
<script setup>
import { onBeforeUnmount, watch } from 'vue'

/**
 * 组件 props:
 * @param {Boolean} open - 是否显示弹窗
 * @param {String} title - 弹窗标题(同时作为无障碍对话标签)
 */
const props = defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: '' }
})

/**
 * 组件事件:
 * @event close - 点击遮罩空白处、关闭按钮或按 ESC 键时触发
 */
const emit = defineEmits(['close'])

// 桌面端支持 ESC 关闭;仅在弹窗打开期间监听,避免干扰页面其他按键
function onKeydown(event) {
  if (event.key === 'Escape') emit('close')
}

// 弹窗打开时锁定页面滚动,防止背景内容跟随滚动造成误操作
watch(() => props.open, (isOpen) => {
  window.removeEventListener('keydown', onKeydown)
  if (isOpen) {
    window.addEventListener('keydown', onKeydown)
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
}, { immediate: true })
onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydown)
  document.body.style.overflow = ''
})
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="open" class="modal-mask" @click.self="emit('close')">
        <section class="modal-panel" role="dialog" aria-modal="true" :aria-label="title">
          <header class="modal-head">
            <h2>{{ title }}</h2>
            <button type="button" class="modal-close" aria-label="关闭弹窗" @click="emit('close')">✕</button>
          </header>
          <div class="modal-body">
            <slot />
          </div>
        </section>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* 遮罩: 深色半透明;默认底部对齐(移动端 bottom-sheet 形态) */
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  background: var(--overlay-dark);
}

/* 面板: 白底大圆角,限高可滚动 */
.modal-panel {
  display: flex;
  flex-direction: column;
  width: min(100%, 480px);
  max-height: 88vh;
  background: var(--bg-card);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
  box-shadow: var(--shadow-hover);
}
.modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5) 0;
}
.modal-head h2 { margin: 0; font-size: var(--text-base); font-weight: var(--font-bold); color: var(--text-primary); }
.modal-close {
  border: 0;
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-base);
  cursor: pointer;
  min-width: var(--touch-target);
  min-height: var(--touch-target);
  margin-right: calc(var(--touch-target) - 2em - 12px);
}
.modal-body { padding: var(--space-4) var(--space-5) var(--space-5); overflow-y: auto; }

/* 桌面端: 弹窗居中,四周留出遮罩边距 */
@media (min-width: 640px) {
  .modal-mask { align-items: center; padding: var(--space-4); }
  .modal-panel { border-radius: var(--radius-lg); }
}

/* 进出场动效: 遮罩淡入 + 面板上滑,尊重系统"减弱动态效果"设置 */
@media (prefers-reduced-motion: no-preference) {
  .modal-enter-active, .modal-leave-active { transition: opacity var(--duration-normal) var(--ease-smooth); }
  .modal-enter-from, .modal-leave-to { opacity: 0; }
  .modal-enter-active .modal-panel, .modal-leave-active .modal-panel {
    transition: transform var(--duration-normal) var(--ease-bounce);
  }
  .modal-enter-from .modal-panel, .modal-leave-to .modal-panel { transform: translateY(32px); }
}
</style>
