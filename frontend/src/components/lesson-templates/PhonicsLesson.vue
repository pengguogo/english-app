<!--
  PhonicsLesson.vue - PHONICS 类型课时模板
  用途: 字母发音规则学习，展示大号字母 + 发音 + 示例词汇，
        支持听发音和听音选字母练习。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description PHONICS 类型课时学习模板，字母发音规则学习 + 听音选字母练习。
 */
import { ref, computed, watch } from 'vue'
import StarBar from '../StarBar.vue'
import AudioButton from '../AudioButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前学习项 { letter, pronunciation, sound, tip, image, emoji, word, phonetic, translation, distractors }
 * @property {number} currentIndex 当前索引（从0开始）
 * @property {number} totalItems 总项数
 * @property {number} currentScore 当前项评分（null 表示未评分）
 * @property {number} currentStars 当前项星星数
 * @property {string} scoreMessage 评分反馈文案
 * @property {boolean} isScoring 是否正在评分
 * @property {boolean} isLastItem 是否最后一项
 */
const props = defineProps({
  /** 当前学习项对象 */
  currentItem: { type: Object, default: null },
  /** 当前索引（从0开始） */
  currentIndex: { type: Number, default: 0 },
  /** 总项数 */
  totalItems: { type: Number, default: 0 },
  /** 当前项评分（null 表示未评分） */
  currentScore: { type: Number, default: null },
  /** 当前项星星数 */
  currentStars: { type: Number, default: 0 },
  /** 评分反馈文案 */
  scoreMessage: { type: String, default: '' },
  /** 是否正在评分 */
  isScoring: { type: Boolean, default: false },
  /** 是否最后一项 */
  isLastItem: { type: Boolean, default: false }
})

/**
 * 组件 Emits
 * @event recorded 录音完成，传递 WAV Blob
 * @event next 切换下一项
 * @event prev 切换上一项
 * @event answered 听音选字母完成，上报是否正确及答案信息
 */
const emit = defineEmits({
  /** 录音完成，参数为 WAV Blob */
  recorded: null,
  /** 切换下一项 */
  next: null,
  /** 切换上一项 */
  prev: null,
  /** 听音选字母完成，参数为 { correct, userAnswer, correctAnswer } */
  answered: null
})

// 学习/练习模式切换: learn=学习模式, practice=听音选字母练习
const mode = ref('learn')
// 选中的选项索引（null 表示未选）
const selectedIndex = ref(null)
// 是否已答题（选中后锁定选项）
const hasAnswered = ref(false)

/**
 * 当前字母（如 "Ff"）。
 */
const currentLetter = computed(() => props.currentItem?.letter || '')

/**
 * 当前示例单词（用于 AudioButton 播放英文发音）。
 */
const currentWord = computed(() => props.currentItem?.word || '')

/**
 * 进度百分比（0-100）。
 */
const progressPercent = computed(() => {
  if (props.totalItems === 0) return 0
  return Math.round(((props.currentIndex + 1) / props.totalItems) * 100)
})

/**
 * 听音选字母的选项列表: 1 个正确字母 + 2 个干扰字母，随机排序。
 * 干扰字母优先使用 currentItem.distractors，不足时从 A-Z 自动生成。
 */
const practiceOptions = computed(() => {
  if (!currentLetter.value) return []
  const correct = currentLetter.value
  // 干扰项: 优先用 currentItem.distractors，否则自动生成补齐
  let distractors = props.currentItem?.distractors?.slice() || []
  if (distractors.length < 2) {
    distractors = distractors.concat(
      generateDistractors(correct, 2 - distractors.length)
    )
  }
  // 合并并随机排序，保证三选一
  return shuffle([correct, ...distractors.slice(0, 2)])
})

/**
 * 当前答题是否正确。
 */
const isCorrect = computed(() => {
  if (!hasAnswered.value || selectedIndex.value === null) return false
  return practiceOptions.value[selectedIndex.value] === currentLetter.value
})

/**
 * 从 A-Z 中随机生成 n 个干扰字母（排除正确字母），格式与 letter 一致（大小写并列）。
 * @param {string} correct 正确字母
 * @param {number} count 需要的干扰项数量
 * @return {string[]} 干扰字母数组
 */
function generateDistractors(correct, count) {
  const all = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('')
  // 排除正确字母的大写形式，避免重复
  const pool = all.filter(l => l !== correct.toUpperCase())
  return shuffle(pool)
    .slice(0, count)
    .map(l => l + l.toLowerCase())
}

/**
 * Fisher-Yates 随机打乱数组（返回新数组，不修改原数组）。
 * @param {Array} arr 原数组
 * @return {Array} 打乱后的新数组
 */
function shuffle(arr) {
  const result = arr.slice()
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[result[i], result[j]] = [result[j], result[i]]
  }
  return result
}

/**
 * 切换到练习模式并重置答题状态。
 */
function enterPractice() {
  mode.value = 'practice'
  selectedIndex.value = null
  hasAnswered.value = false
}

/**
 * 返回学习模式。
 */
function backToLearn() {
  mode.value = 'learn'
}

/**
 * 选择选项：首次选择时锁定并上报答题结果。
 * @param {number} index 选项索引
 */
function selectOption(index) {
  if (hasAnswered.value) return
  selectedIndex.value = index
  hasAnswered.value = true
  const userAnswer = practiceOptions.value[index] ?? ''
  const correctAnswer = currentLetter.value
  emit('answered', { correct: userAnswer === correctAnswer, userAnswer, correctAnswer })
}

/**
 * 获取选项的样式类（答题后高亮对错）。
 * @param {number} index 选项索引
 * @return {string} CSS 类名
 */
function getOptionClass(index) {
  if (!hasAnswered.value) return 'option-default'
  const optionLetter = practiceOptions.value[index]
  // 答题后正确答案始终高亮
  if (optionLetter === currentLetter.value) return 'option-correct'
  // 选错的选项标红
  if (index === selectedIndex.value && !isCorrect.value) return 'option-wrong'
  return 'option-disabled'
}

/**
 * 切换学习项时重置模式与答题状态，避免上一题状态残留。
 */
watch(() => props.currentIndex, () => {
  mode.value = 'learn'
  selectedIndex.value = null
  hasAnswered.value = false
})
</script>

<template>
  <div class="card-area">
    <!-- 顶部进度条 + 数字标注 -->
    <div class="progress-section">
      <div class="progress-info">
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 项</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画（右上角） -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- ============ 学习模式 ============ -->
    <div v-if="mode === 'learn'" class="learn-card">
      <!-- 大号字母（6rem，主色调） -->
      <h1 class="big-letter">{{ currentLetter }}</h1>
      <!-- 字母发音（如 /ef/） -->
      <p v-if="currentItem.pronunciation" class="letter-sound">
        /{{ currentItem.pronunciation }}/
      </p>
      <!-- 音标（如 /f/） -->
      <p v-if="currentItem.sound" class="phonetic">
        音标 /{{ currentItem.sound }}/
      </p>
      <!-- 发音提示 -->
      <p v-if="currentItem.tip" class="tip">{{ currentItem.tip }}</p>

      <!-- 图片或 emoji（有图显图，无图显 emoji） -->
      <div class="visual">
        <img
          v-if="currentItem.image"
          :src="currentItem.image"
          :alt="currentItem.word"
          class="word-image"
        />
        <div v-else class="emoji">{{ currentItem.emoji }}</div>
      </div>

      <!-- 示例单词 + 音标 + 翻译 -->
      <div class="word-info">
        <h2 class="word">{{ currentItem.word }}</h2>
        <p v-if="currentItem.phonetic" class="word-phonetic">
          /{{ currentItem.phonetic }}/
        </p>
        <p class="translation">{{ currentItem.translation }}</p>
      </div>

      <!-- 听发音按钮 -->
      <div class="actions">
        <AudioButton :text="currentWord" :translation="currentItem.translation || ''" />
      </div>
    </div>

    <!-- ============ 练习模式: 听音选字母 ============ -->
    <div v-else class="practice-card">
      <h2 class="practice-question">听一听，选出发这个音的字母</h2>
      <p v-if="currentItem.sound" class="practice-hint">
        音标 /{{ currentItem.sound }}/
      </p>

      <!-- 选项列表（3 选 1，随机排序） -->
      <div class="options">
        <button
          v-for="(option, index) in practiceOptions"
          :key="index"
          :class="['option', getOptionClass(index)]"
          :disabled="hasAnswered"
          @click="selectOption(index)"
        >
          <span class="option-letter">{{ option }}</span>
          <span v-if="hasAnswered && option === currentLetter" class="option-icon">✓</span>
          <span
            v-if="hasAnswered && index === selectedIndex && !isCorrect"
            class="option-icon"
          >✗</span>
        </button>
      </div>

      <!-- 答题反馈文字 -->
      <div v-if="hasAnswered" class="feedback-area">
        <p :class="['feedback-text', isCorrect ? 'feedback-correct' : 'feedback-wrong']">
          {{ isCorrect ? '回答正确！太棒了！' : '答错了，正确答案是 ' + currentLetter }}
        </p>
      </div>
    </div>

    <!-- 评分结果区（仅学习模式显示） -->
    <div v-if="mode === 'learn' && currentScore !== null" class="score-area">
      <div class="score-row">
        <span class="score">{{ currentScore }} 分</span>
        <StarBar :stars="currentStars" size="sm" />
      </div>
      <p class="feedback">{{ scoreMessage }}</p>
    </div>
    <div v-else-if="mode === 'learn' && isScoring" class="score-area scoring">
      <div class="scoring-dot"></div>
      <p class="feedback">{{ scoreMessage }}</p>
    </div>

    <!-- 操作按钮: 上一项 / 去练习(或返回学习) / 下一步 -->
    <div class="action-row">
      <!-- 上一项按钮:第一项不显示 -->
      <AppButton
        v-if="currentIndex > 0"
        variant="ghost"
        size="md"
        @click="emit('prev')"
      >← 上一项</AppButton>
      <span v-else class="action-placeholder"></span>

      <!-- 中间按钮:学习模式显示"去练习"，练习模式显示"返回学习" -->
      <AppButton
        v-if="mode === 'learn'"
        variant="warning"
        size="md"
        @click="enterPractice"
      >去练习</AppButton>
      <AppButton
        v-else
        variant="warning"
        size="md"
        @click="backToLearn"
      >返回学习</AppButton>

      <!-- 下一步/完成本课 -->
      <AppButton
        variant="primary"
        size="md"
        class="action-next"
        @click="emit('next')"
      >
        {{ isLastItem ? '完成本课' : '下一步 →' }}
      </AppButton>
    </div>
  </div>
</template>

<style scoped>
.card-area {
  max-width: 480px;
  margin: 0 auto;
  position: relative;
}

/* 顶部进度条 */
.progress-section { margin-bottom: var(--space-5); }
.progress-info {
  text-align: center;
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin-bottom: var(--space-2);
}
.progress-bar {
  height: 8px;
  background: var(--bg-card);
  border-radius: var(--radius-pill);
  overflow: hidden;
  box-shadow: var(--shadow-soft);
}
.progress-fill {
  height: 100%;
  background: var(--gradient-primary);
  border-radius: var(--radius-pill);
  transition: width var(--duration-slow) var(--ease-smooth);
}

/* 小老鼠陪伴插画 */
.mascot-companion {
  position: absolute;
  top: 0;
  right: 0;
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: var(--radius-md);
  z-index: 1;
  opacity: 0.9;
}

/* 学习模式卡片 */
.learn-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-8) var(--space-6);
  text-align: center;
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
}

/* 大号字母: 6rem 主色调 */
.big-letter {
  font-size: 6rem;
  font-weight: var(--font-bold);
  color: var(--color-primary);
  line-height: 1.1;
  margin-bottom: var(--space-3);
  word-break: break-word;
}

/* 字母弹出动画 */
@media (prefers-reduced-motion: no-preference) {
  .big-letter {
    animation: letterPop var(--duration-normal) var(--ease-bounce) both;
  }
}
@keyframes letterPop {
  0% { transform: scale(0.5); opacity: 0; }
  60% { transform: scale(1.15); opacity: 1; }
  100% { transform: scale(1); opacity: 1; }
}

.letter-sound {
  color: var(--color-orange);
  font-size: var(--text-lg);
  margin-bottom: var(--space-2);
  font-weight: var(--font-medium);
}
.phonetic {
  color: var(--text-secondary);
  font-size: var(--text-base);
  margin-bottom: var(--space-2);
}
.tip {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin-bottom: var(--space-4);
}

/* 图片或 emoji */
.visual { margin-bottom: var(--space-4); }
.word-image {
  width: 180px;
  height: 180px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft);
}
.emoji { font-size: 5rem; }

/* 示例单词信息 */
.word-info { margin-bottom: var(--space-5); }
.word {
  font-size: 2rem;
  color: var(--text-primary);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
}
.word-phonetic {
  color: var(--text-secondary);
  font-size: var(--text-base);
  margin-bottom: var(--space-2);
}
.translation {
  color: var(--text-secondary);
  font-size: var(--text-base);
}

/* 听发音按钮区 */
.actions {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
}

/* 练习模式卡片 */
.practice-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  text-align: center;
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
}
.practice-question {
  font-size: var(--text-lg);
  color: var(--text-primary);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
  line-height: 1.4;
}
.practice-hint {
  color: var(--color-orange);
  font-size: var(--text-base);
  margin-bottom: var(--space-5);
  font-weight: var(--font-medium);
}

/* 选项列表 */
.options {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}
.option {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  border-radius: var(--radius-md);
  border: 2px solid var(--border-light);
  background: var(--bg-card);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-smooth);
  font-size: 2rem;
  font-weight: var(--font-bold);
}
.option-default:hover {
  border-color: var(--color-primary);
  background: var(--bg-muted);
}
.option-correct {
  border-color: var(--color-success);
  background: var(--color-success-bg);
}
.option-wrong {
  border-color: var(--color-warning);
  background: var(--color-warning-bg);
}
.option-disabled { opacity: 0.5; }

.option-letter { color: var(--color-primary); }
.option-correct .option-letter { color: var(--color-success); }
.option-wrong .option-letter { color: var(--color-warning); }

.option-icon { font-size: 1.5rem; font-weight: bold; }
.option-correct .option-icon { color: var(--color-success); }
.option-wrong .option-icon { color: var(--color-warning); }

/* 答题反馈 */
.feedback-area {
  margin-top: var(--space-4);
  padding: var(--space-3);
  border-radius: var(--radius-md);
  text-align: center;
}
.feedback-correct { color: var(--color-success); font-weight: var(--font-bold); }
.feedback-wrong { color: var(--color-warning); font-weight: var(--font-bold); }

/* 选项对错动画 */
@media (prefers-reduced-motion: no-preference) {
  .option-correct { animation: correctPulse var(--duration-normal) var(--ease-bounce); }
  .option-wrong { animation: wrongShake var(--duration-fast) var(--ease-smooth); }
}
@keyframes correctPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.03); }
}
@keyframes wrongShake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  75% { transform: translateX(4px); }
}

/* 评分结果区 */
.score-area {
  text-align: center;
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: var(--space-4);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-soft);
}
.score-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}
.score {
  font-size: 2rem;
  font-weight: var(--font-bold);
  color: var(--color-orange);
}

/* 分数弹跳动画 */
@media (prefers-reduced-motion: no-preference) {
  .score {
    animation: scoreBounce var(--duration-normal) var(--ease-bounce) both;
  }
}
@keyframes scoreBounce {
  0% { transform: translateY(8px) scale(0.8); opacity: 0; }
  60% { transform: translateY(-4px) scale(1.1); opacity: 1; }
  100% { transform: translateY(0) scale(1); opacity: 1; }
}

.feedback {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.score-area.scoring {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
}
.scoring-dot {
  width: 16px;
  height: 16px;
  border: 2px solid var(--border-light);
  border-top-color: var(--color-orange);
  border-radius: var(--radius-pill);
}
@media (prefers-reduced-motion: no-preference) {
  .scoring-dot { animation: spin 0.8s linear infinite; }
}
@keyframes spin { to { transform: rotate(360deg); } }

/* 按钮行: 上一项 + 去练习/返回学习 + 下一步 */
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
}
/* 占位符:第一项时保持后续按钮居右 */
.action-placeholder { flex: 1; }
/* 下一步按钮占据主要空间 */
.action-next { flex: 1; }

/* 响应式: 手机 <480px 缩小字号和图片 */
@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .big-letter { font-size: 4.5rem; }
  .word-image { width: 140px; height: 140px; }
  .emoji { font-size: 4rem; }
  .word { font-size: 1.5rem; }
  .option { font-size: 1.5rem; padding: var(--space-3) var(--space-4); }
}
</style>
