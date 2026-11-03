<!--
  LessonNextBar.vue - 课程底部操作栏（有“下一步”门槛的模板共用）
  用途: 上一项 + 次要的“稍后复习”小图标 + 大号「→」主按钮。
        未解锁时主按钮半透明但可点，点击只发出 hint 事件，由模板高亮该先做的按钮。
-->
<script setup>
import AppButton from '../AppButton.vue'

defineProps({
  unlocked: { type: Boolean, default: false },
  isLastItem: { type: Boolean, default: false },
  showPrev: { type: Boolean, default: false },
  prevLabel: { type: String, default: '上一项' },
  showSkip: { type: Boolean, default: false },
  // 未解锁时的读屏提示，如“先听一听”
  lockedHint: { type: String, default: '先听一听' }
})
const emit = defineEmits(['prev', 'skip', 'next', 'hint'])
</script>

<template>
  <div class="next-bar">
    <AppButton
      v-if="showPrev"
      variant="ghost"
      :aria-label="prevLabel"
      :title="prevLabel"
      @click="emit('prev')"
    >←</AppButton>
    <span v-else class="next-bar-spacer"></span>

    <AppButton
      v-if="showSkip"
      variant="ghost"
      class="skip-btn"
      aria-label="稍后复习"
      title="稍后复习"
      @click="emit('skip')"
    ><span aria-hidden="true">⏭</span></AppButton>

    <AppButton
      size="lg"
      class="next-btn"
      :class="{ 'is-locked': !unlocked }"
      :aria-disabled="!unlocked"
      :aria-label="!unlocked ? lockedHint : (isLastItem ? '完成本课' : '下一步')"
      :title="!unlocked ? lockedHint : (isLastItem ? '完成本课' : '下一步')"
      @click="unlocked ? emit('next') : emit('hint')"
    ><span aria-hidden="true">{{ isLastItem && unlocked ? '✓' : '→' }}</span></AppButton>
  </div>
</template>

<style scoped>
.next-bar {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.next-bar-spacer {
  width: var(--touch-target);
}

.skip-btn {
  min-width: var(--touch-target);
  padding-inline: var(--space-3);
  color: var(--text-tertiary);
}

.next-btn {
  flex: 1;
  min-height: 64px;
  font-size: var(--text-xl);
}

/* 未解锁：半透明但仍可点击，用于触发引导 */
.next-btn.is-locked {
  opacity: 0.4;
  box-shadow: none;
}
</style>
