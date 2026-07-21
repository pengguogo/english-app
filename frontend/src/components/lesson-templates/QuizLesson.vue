<!--
  QuizLesson.vue - QUIZ 类型课时模板
  用途: 选择题学习，展示题目+选项，选择后即时判对错并反馈。
        适用于数学图形/应用题、课外火车互动问答。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description QUIZ 类型课时学习模板，选择题答题+即时反馈。
 */
import { ref, computed, watch } from 'vue'
import StarBar from '../StarBar.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前题目 { question, options[], answer, image }
 * @property {number} currentIndex 当前题号
 * @property {number} totalItems 总题数
 * @property {boolean} isLastItem 是否最后一题
 */
const props = defineProps({
  /** 当前题目对象 */
  currentItem: { type: Object, default: null },
  /** 当前题号（从0开始） */
  currentIndex: { type: Number, default: 0 },
  /** 总题数 */
  totalItems: { type: Number, default: 0 },
  /** 是否最后一题 */
  isLastItem: { type: Boolean, default: false }
})

/**
 * 组件 Emits
 * @event answered 答题完成，上报是否正确
 * @event next 切换下一题/完成
 * @event prev 切换上一题
 */
const emit = defineEmits({
  /** 答题完成，参数为是否答对 */
  answered: null,
  /** 切换下一题或完成 */
  next: null,
  /** 切换上一题 */
  prev: null
})

// 选中的选项索引（null 表示未选）
const selectedIndex = ref(null)
// 是否已答题（选中后锁定选项）
const hasAnswered = ref(false)

/**
 * 当前题是否答对。
 */
const isCorrect = computed(() => {
  if (!hasAnswered.value || selectedIndex.value === null) return false
  return selectedIndex.value === props.currentItem?.answer
})

/**
 * 切换题目时重置答题状态。
 */
watch(() => props.currentIndex, () => {
  selectedIndex.value = null
  hasAnswered.value = false
})

/**
 * 进度百分比。
 */
const progressPercent = computed(() => {
  if (props.totalItems === 0) return 0
  return Math.round(((props.currentIndex + 1) / props.totalItems) * 100)
})

/**
 * 选择选项：首次选择时锁定并上报答题结果。
 * @param {number} index 选项索引
 */
function selectOption(index) {
  if (hasAnswered.value) return
  selectedIndex.value = index
  hasAnswered.value = true
  const correct = index === props.currentItem?.answer
  // 上报答题结果,同时携带用户答案与正确答案,便于父组件记录错题
  const userAnswer = props.currentItem?.options?.[index] ?? ''
  const correctAnswer = props.currentItem?.options?.[props.currentItem.answer] ?? ''
  emit('answered', { correct, userAnswer, correctAnswer })
}

/**
 * 获取选项的样式类（答题后高亮对错）。
 * @param {number} index 选项索引
 * @return {string} CSS 类名
 */
function getOptionClass(index) {
  if (!hasAnswered.value) return 'option-default'
  if (index === props.currentItem?.answer) return 'option-correct'
  if (index === selectedIndex.value && !isCorrect.value) return 'option-wrong'
  return 'option-disabled'
}
</script>

<template>
  <div class="card-area">
    <!-- 顶部进度条 -->
    <div class="progress-section">
      <div class="progress-info">
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 题</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画 -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 题目卡片 -->
    <div class="quiz-card">
      <div v-if="currentItem.image" class="quiz-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.question" class="quiz-image" />
      </div>
      <h1 class="quiz-question">{{ currentItem.question }}</h1>

      <!-- 选项列表 -->
      <div class="options">
        <button
          v-for="(option, index) in currentItem.options"
          :key="index"
          :class="['option', getOptionClass(index)]"
          :disabled="hasAnswered"
          @click="selectOption(index)"
        >
          <span class="option-label">{{ String.fromCharCode(65 + index) }}</span>
          <span class="option-text">{{ option }}</span>
          <span v-if="hasAnswered && index === currentItem.answer" class="option-icon">✓</span>
          <span v-if="hasAnswered && index === selectedIndex && !isCorrect" class="option-icon">✗</span>
        </button>
      </div>

      <!-- 答题反馈 -->
      <div v-if="hasAnswered" class="feedback-area">
        <p :class="['feedback-text', isCorrect ? 'feedback-correct' : 'feedback-wrong']">
          {{ isCorrect ? '回答正确！太棒了！' : '答错了，正确答案是 ' + String.fromCharCode(65 + currentItem.answer) + '：' + currentItem.options[currentItem.answer] }}
        </p>
      </div>
    </div>

    <!-- 上一题/下一题按钮(答题后显示) -->
    <div v-if="hasAnswered" class="action-row">
      <!-- 上一题按钮:第一题不显示 -->
      <AppButton
        v-if="currentIndex > 0"
        variant="ghost"
        size="md"
        @click="emit('prev')"
      >← 上一题</AppButton>
      <span v-else class="action-placeholder"></span>
      <!-- 下一题/完成按钮 -->
      <AppButton
        variant="primary"
        size="md"
        class="action-next"
        @click="emit('next')"
      >
        {{ isLastItem ? '完成答题' : '下一题 →' }}
      </AppButton>
    </div>
  </div>
</template>

<style scoped>
.card-area { max-width: 480px; margin: 0 auto; position: relative; }

.progress-section { margin-bottom: var(--space-5); }
.progress-info { text-align: center; color: var(--text-tertiary); font-size: var(--text-sm); margin-bottom: var(--space-2); }
.progress-bar { height: 8px; background: var(--bg-card); border-radius: var(--radius-pill); overflow: hidden; box-shadow: var(--shadow-soft); }
.progress-fill { height: 100%; background: var(--gradient-primary); border-radius: var(--radius-pill); transition: width var(--duration-slow) var(--ease-smooth); }

.mascot-companion { position: absolute; top: 0; right: 0; width: 80px; height: 80px; object-fit: contain; border-radius: var(--radius-md); z-index: 1; opacity: 0.9; }

.quiz-card { background: var(--bg-card); border-radius: var(--radius-lg); padding: var(--space-6); margin-bottom: var(--space-6); box-shadow: var(--shadow-card); }

.quiz-image-wrapper { margin-bottom: var(--space-4); text-align: center; }
.quiz-image { width: 100%; max-width: 280px; height: 200px; object-fit: cover; border-radius: var(--radius-md); box-shadow: var(--shadow-soft); }

.quiz-question { font-size: var(--text-lg); color: var(--text-primary); font-weight: var(--font-bold); margin-bottom: var(--space-5); text-align: center; line-height: 1.5; }

.options { display: flex; flex-direction: column; gap: var(--space-3); }

.option { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-3) var(--space-4); border-radius: var(--radius-md); border: 2px solid var(--border-light); background: var(--bg-card); cursor: pointer; transition: all var(--duration-fast) var(--ease-smooth); font-size: var(--text-base); text-align: left; }

.option-default:hover { border-color: var(--color-primary); background: var(--bg-muted); }
.option-correct { border-color: var(--color-success); background: var(--color-success-bg); }
.option-wrong { border-color: var(--color-warning); background: var(--color-warning-bg); }
.option-disabled { opacity: 0.5; }

.option-label { display: inline-flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: var(--radius-pill); background: var(--color-primary); color: white; font-weight: var(--font-bold); font-size: var(--text-sm); flex-shrink: 0; }
.option-correct .option-label { background: var(--color-success); }
.option-wrong .option-label { background: var(--color-warning); }

.option-text { flex: 1; color: var(--text-primary); }

.option-icon { font-size: 1.2rem; font-weight: bold; }
.option-correct .option-icon { color: var(--color-success); }
.option-wrong .option-icon { color: var(--color-warning); }

.feedback-area { margin-top: var(--space-4); padding: var(--space-3); border-radius: var(--radius-md); text-align: center; }
.feedback-correct { color: var(--color-success); font-weight: var(--font-bold); }
.feedback-wrong { color: var(--color-warning); font-weight: var(--font-bold); }

/* 按钮行:左右分布,上一题 + 下一题 */
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
}

/* 占位符:第一题时保持下一题按钮居右 */
.action-placeholder {
  flex: 1;
}

/* 下一题按钮占据主要空间 */
.action-next {
  flex: 1;
}

@media (prefers-reduced-motion: no-preference) {
  .option-correct { animation: correctPulse var(--duration-normal) var(--ease-bounce); }
  .option-wrong { animation: wrongShake var(--duration-fast) var(--ease-smooth); }
}

@keyframes correctPulse { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.02); } }
@keyframes wrongShake { 0%, 100% { transform: translateX(0); } 25% { transform: translateX(-4px); } 75% { transform: translateX(4px); } }

@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .quiz-image { height: 160px; }
}
</style>
