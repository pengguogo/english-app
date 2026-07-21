<!--
  LessonComplete.vue - 课时完成结算页
  用途: 展示课时完成后的庆祝插画、星星、平均分和完成按钮。
        所有课型（WORD/SENTENCE/READING/QUIZ/CALCULATE）共用此结算组件。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description 课时完成结算组件，展示最终成绩和庆祝动画。
 */
import StarBar from '../StarBar.vue'
import AppButton from '../AppButton.vue'
import mascotCelebrate from '../../assets/mascot/mascot-celebrate.jpg'

/**
 * 组件 Props
 * @property {string} lessonName 课时名称
 * @property {number} totalStars 最终获得星星数（0-3）
 * @property {number} totalScore 平均得分（0-100）
 * @property {boolean} isSubmitting 是否正在提交进度
 */
const props = defineProps({
  /** 课时名称 */
  lessonName: {
    type: String,
    default: ''
  },
  /** 最终获得星星数（0-3） */
  totalStars: {
    type: Number,
    default: 0
  },
  /** 平均得分（0-100） */
  totalScore: {
    type: Number,
    default: 0
  },
  /** 是否正在提交进度 */
  isSubmitting: {
    type: Boolean,
    default: false
  }
})

/**
 * 组件 Emits
 * @event finish 用户点击完成按钮
 */
const emit = defineEmits({
  /** 用户点击完成按钮时触发 */
  finish: null
})
</script>

<template>
  <div class="complete-area">
    <!-- 小老鼠庆祝插画（主视觉） -->
    <img :src="mascotCelebrate" alt="小老鼠庆祝" class="mascot-celebrate" />

    <h2>太棒了!</h2>
    <p class="complete-lesson-name">你完成了《{{ lessonName }}》</p>

    <StarBar :stars="totalStars" size="lg" />

    <p class="total-score">平均得分:{{ totalScore }} 分</p>

    <AppButton
      variant="success"
      size="lg"
      :disabled="isSubmitting"
      @click="emit('finish')"
    >
      {{ isSubmitting ? '保存中...' : '完成' }}
    </AppButton>
  </div>
</template>

<style scoped>
.complete-area {
  text-align: center;
  padding: var(--space-6) var(--space-4);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  max-width: 480px;
  margin: var(--space-6) auto;
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

/* 小老鼠庆祝插画: 主视觉 */
.mascot-celebrate {
  width: 160px;
  height: 160px;
  object-fit: contain;
  border-radius: var(--radius-md);
}

@media (prefers-reduced-motion: no-preference) {
  .mascot-celebrate {
    animation: celebrateBounce var(--duration-slow) var(--ease-bounce) both;
  }
}

@keyframes celebrateBounce {
  0% {
    transform: scale(0) rotate(-180deg);
    opacity: 0;
  }
  60% {
    transform: scale(1.15) rotate(10deg);
    opacity: 1;
  }
  100% {
    transform: scale(1) rotate(0);
    opacity: 1;
  }
}

.complete-area h2 {
  font-size: var(--text-xl);
  color: var(--color-orange);
  font-weight: var(--font-bold);
}

.complete-lesson-name {
  color: var(--text-secondary);
  font-size: var(--text-base);
}

.total-score {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* 响应式: 手机端缩小吉祥物 */
@media (max-width: 480px) {
  .mascot-celebrate { width: 128px; height: 128px; }
}
</style>
