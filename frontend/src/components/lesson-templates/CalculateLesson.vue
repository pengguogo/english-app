<!--
  CalculateLesson.vue - CALCULATE 类型课时模板
  用途: 计算题学习，展示题目，用户通过数字键盘输入答案，即时判对错。
        适用于数学加减运算。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description CALCULATE 类型课时学习模板，数字输入答题+即时反馈。
 */
import { ref, computed, watch } from 'vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前题目 { question, answer, image }
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
 */
const emit = defineEmits({
  /** 答题完成，参数为是否答对 */
  answered: null,
  /** 切换下一题或完成 */
  next: null
})

// 用户输入的答案
const userInput = ref('')
// 是否已提交（锁定输入）
const hasAnswered = ref(false)

/**
 * 当前题是否答对（字符串比较）。
 */
const isCorrect = computed(() => {
  if (!hasAnswered.value) return false
  return userInput.value === String(props.currentItem?.answer)
})

/**
 * 切换题目时重置状态。
 */
watch(() => props.currentIndex, () => {
  userInput.value = ''
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
 * 数字键盘按钮（0-9）。
 */
const keypadButtons = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '0']

/**
 * 点击数字键盘按钮，追加数字到输入。
 * @param {string} num 数字字符
 */
function pressKey(num) {
  if (hasAnswered.value) return
  // 限制最大输入长度（防止溢出）
  if (userInput.value.length >= 4) return
  userInput.value += num
}

/**
 * 删除最后一个字符。
 */
function deleteLast() {
  if (hasAnswered.value) return
  userInput.value = userInput.value.slice(0, -1)
}

/**
 * 清空输入。
 */
function clearInput() {
  if (hasAnswered.value) return
  userInput.value = ''
}

/**
 * 提交答案：判定对错并上报。
 */
function submitAnswer() {
  if (hasAnswered.value || userInput.value === '') return
  hasAnswered.value = true
  const correct = userInput.value === String(props.currentItem?.answer)
  emit('answered', correct)
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
    <div class="calc-card">
      <div v-if="currentItem.image" class="calc-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.question" class="calc-image" />
      </div>
      <h1 class="calc-question">{{ currentItem.question }}</h1>

      <!-- 答案显示区 -->
      <div class="answer-display" :class="{
        'answer-correct': hasAnswered && isCorrect,
        'answer-wrong': hasAnswered && !isCorrect
      }">
        <span class="answer-text">{{ userInput || '?' }}</span>
        <span v-if="hasAnswered && !isCorrect" class="correct-answer">
          正确答案：{{ currentItem.answer }}
        </span>
      </div>

      <!-- 数字键盘 -->
      <div v-if="!hasAnswered" class="keypad">
        <button
          v-for="num in keypadButtons.slice(0, 9)"
          :key="num"
          class="key-btn"
          @click="pressKey(num)"
        >{{ num }}</button>
        <button class="key-btn key-action" @click="clearInput">C</button>
        <button class="key-btn" @click="pressKey('0')">0</button>
        <button class="key-btn key-action" @click="deleteLast">⌫</button>
      </div>

      <!-- 答题反馈 -->
      <div v-if="hasAnswered" class="feedback-area">
        <p :class="['feedback-text', isCorrect ? 'feedback-correct' : 'feedback-wrong']">
          {{ isCorrect ? '回答正确！太棒了！' : '答错了，再接再厉！' }}
        </p>
      </div>
    </div>

    <!-- 提交/下一题按钮 -->
    <AppButton
      v-if="!hasAnswered"
      variant="primary"
      size="lg"
      block
      :disabled="userInput === ''"
      @click="submitAnswer"
    >提交答案</AppButton>

    <AppButton
      v-if="hasAnswered"
      variant="primary"
      size="lg"
      block
      @click="emit('next')"
    >{{ isLastItem ? '完成答题' : '下一题 →' }}</AppButton>
  </div>
</template>

<style scoped>
.card-area { max-width: 480px; margin: 0 auto; position: relative; }

.progress-section { margin-bottom: var(--space-5); }
.progress-info { text-align: center; color: var(--text-tertiary); font-size: var(--text-sm); margin-bottom: var(--space-2); }
.progress-bar { height: 8px; background: var(--bg-card); border-radius: var(--radius-pill); overflow: hidden; box-shadow: var(--shadow-soft); }
.progress-fill { height: 100%; background: var(--gradient-primary); border-radius: var(--radius-pill); transition: width var(--duration-slow) var(--ease-smooth); }

.mascot-companion { position: absolute; top: 0; right: 0; width: 80px; height: 80px; object-fit: contain; border-radius: var(--radius-md); z-index: 1; opacity: 0.9; }

.calc-card { background: var(--bg-card); border-radius: var(--radius-lg); padding: var(--space-6); margin-bottom: var(--space-6); box-shadow: var(--shadow-card); }

.calc-image-wrapper { margin-bottom: var(--space-4); text-align: center; }
.calc-image { width: 100%; max-width: 240px; height: 180px; object-fit: cover; border-radius: var(--radius-md); box-shadow: var(--shadow-soft); }

.calc-question { font-size: var(--text-xl); color: var(--text-primary); font-weight: var(--font-bold); margin-bottom: var(--space-5); text-align: center; }

.answer-display { background: var(--bg-muted); border-radius: var(--radius-md); padding: var(--space-4); text-align: center; margin-bottom: var(--space-4); min-height: 60px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-1); transition: all var(--duration-fast) var(--ease-smooth); }

.answer-correct { background: var(--color-success-bg); }
.answer-wrong { background: var(--color-warning-bg); }

.answer-text { font-size: var(--text-xl); font-weight: var(--font-bold); color: var(--text-primary); }
.answer-correct .answer-text { color: var(--color-success); }
.answer-wrong .answer-text { color: var(--color-warning); }

.correct-answer { font-size: var(--text-sm); color: var(--color-success); }

.keypad { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--space-2); margin-bottom: var(--space-4); }

.key-btn { height: 56px; border: 2px solid var(--border-light); border-radius: var(--radius-md); background: var(--bg-card); font-size: var(--text-lg); font-weight: var(--font-bold); color: var(--text-primary); cursor: pointer; transition: all var(--duration-fast) var(--ease-smooth); }
.key-btn:hover { border-color: var(--color-primary); background: var(--bg-muted); }
.key-btn:active { transform: scale(0.95); }
.key-action { color: var(--color-warning); }

.feedback-area { margin-top: var(--space-4); padding: var(--space-3); border-radius: var(--radius-md); text-align: center; }
.feedback-correct { color: var(--color-success); font-weight: var(--font-bold); }
.feedback-wrong { color: var(--color-warning); font-weight: var(--font-bold); }

@media (prefers-reduced-motion: no-preference) {
  .answer-correct { animation: correctPulse var(--duration-normal) var(--ease-bounce); }
  .answer-wrong { animation: wrongShake var(--duration-fast) var(--ease-smooth); }
}

@keyframes correctPulse { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.02); } }
@keyframes wrongShake { 0%, 100% { transform: translateX(0); } 25% { transform: translateX(-4px); } 75% { transform: translateX(4px); } }

@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .key-btn { height: 48px; }
}
</style>
