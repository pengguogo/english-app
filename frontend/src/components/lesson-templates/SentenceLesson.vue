<!--
  SentenceLesson.vue - SENTENCE 类型课时模板
  用途: 句型/古诗朗诵学习，展示句子+释义，支持听发音和跟读朗诵评分。
        适用于英语句型课、语文古诗朗诵课。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description SENTENCE 类型课时学习模板，展示当前句子并提供听音+跟读评分交互。
 */
import { computed } from 'vue'
import StarBar from '../StarBar.vue'
import AudioButton from '../AudioButton.vue'
import RecordButton from '../RecordButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前学习项 { sentence, translation, emoji, image }
 * @property {number} currentIndex 当前索引
 * @property {number} totalItems 总项数
 * @property {number} currentScore 当前项评分
 * @property {number} currentStars 当前项星星数
 * @property {string} scoreMessage 评分反馈文案
 * @property {boolean} isScoring 是否正在评分
 * @property {boolean} isLastItem 是否最后一项
 */
const props = defineProps({
  currentItem: { type: Object, default: null },
  currentIndex: { type: Number, default: 0 },
  totalItems: { type: Number, default: 0 },
  currentScore: { type: Number, default: null },
  currentStars: { type: Number, default: 0 },
  scoreMessage: { type: String, default: '' },
  isScoring: { type: Boolean, default: false },
  isLastItem: { type: Boolean, default: false }
})

const emit = defineEmits({
  /** 录音完成，参数为 WAV Blob */
  recorded: null,
  /** 切换下一项 */
  next: null,
  /** 切换上一项 */
  prev: null
})

/**
 * 当前学习项对应的文本（sentence 字段）。
 */
const currentText = computed(() => {
  if (!props.currentItem) return ''
  return props.currentItem.sentence || ''
})

/**
 * 进度百分比（0-100）。
 */
const progressPercent = computed(() => {
  if (props.totalItems === 0) return 0
  return Math.round(((props.currentIndex + 1) / props.totalItems) * 100)
})
</script>

<template>
  <div class="card-area">
    <!-- 顶部进度条 -->
    <div class="progress-section">
      <div class="progress-info">
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 项</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画 -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 句型卡片 -->
    <div class="item-card">
      <div v-if="currentItem.image" class="sentence-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.sentence" class="sentence-image" />
      </div>
      <div v-else class="emoji">{{ currentItem.emoji || '💬' }}</div>
      <h1 class="sentence-text">{{ currentText }}</h1>
      <p class="translation">{{ currentItem.translation }}</p>
    </div>

    <!-- 操作区 -->
    <div class="actions">
      <AudioButton :text="currentText" :translation="currentItem.translation || ''" />
      <RecordButton @recorded="(blob) => emit('recorded', blob)" />
    </div>

    <!-- 评分结果区 -->
    <div v-if="currentScore !== null" class="score-area">
      <div class="score-row">
        <span class="score">{{ currentScore }} 分</span>
        <StarBar :stars="currentStars" size="sm" />
      </div>
      <p class="feedback">{{ scoreMessage }}</p>
    </div>
    <div v-else-if="isScoring" class="score-area scoring">
      <div class="scoring-dot"></div>
      <p class="feedback">{{ scoreMessage }}</p>
    </div>

    <!-- 上一步 / 下一步 / 完成本课 -->
    <div class="action-row">
      <!-- 上一步按钮:第一项不显示 -->
      <AppButton
        v-if="currentIndex > 0"
        variant="ghost"
        size="md"
        @click="emit('prev')"
      >← 上一项</AppButton>
      <span v-else class="action-placeholder"></span>
      <!-- 下一步 / 完成本课 -->
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

.progress-section { margin-bottom: var(--space-5); }
.progress-info { text-align: center; color: var(--text-tertiary); font-size: var(--text-sm); margin-bottom: var(--space-2); }
.progress-bar { height: 8px; background: var(--bg-card); border-radius: var(--radius-pill); overflow: hidden; box-shadow: var(--shadow-soft); }
.progress-fill { height: 100%; background: var(--gradient-primary); border-radius: var(--radius-pill); transition: width var(--duration-slow) var(--ease-smooth); }

.mascot-companion { position: absolute; top: 0; right: 0; width: 80px; height: 80px; object-fit: contain; border-radius: var(--radius-md); z-index: 1; opacity: 0.9; }

.item-card { background: var(--bg-card); border-radius: var(--radius-lg); padding: var(--space-8) var(--space-6); text-align: center; margin-bottom: var(--space-6); box-shadow: var(--shadow-card); }
.emoji { font-size: 5rem; margin-bottom: var(--space-4); }
.sentence-image-wrapper { margin-bottom: var(--space-4); }
.sentence-image { width: 180px; height: 180px; object-fit: cover; border-radius: var(--radius-lg); box-shadow: var(--shadow-soft); }
.sentence-text { font-size: 1.8rem; margin-bottom: var(--space-2); color: var(--text-primary); word-break: break-word; font-weight: var(--font-bold); line-height: 1.4; }
.translation { color: var(--text-secondary); font-size: var(--text-base); }

.actions { display: flex; gap: var(--space-4); justify-content: center; margin-bottom: var(--space-6); }

.score-area { text-align: center; background: var(--bg-card); border-radius: var(--radius-md); padding: var(--space-4); margin-bottom: var(--space-6); box-shadow: var(--shadow-soft); }
.score-row { display: flex; align-items: center; justify-content: center; gap: var(--space-3); margin-bottom: var(--space-2); }
.score { font-size: 2rem; font-weight: var(--font-bold); color: var(--color-orange); }

@media (prefers-reduced-motion: no-preference) {
  .score { animation: scoreBounce var(--duration-normal) var(--ease-bounce) both; }
}

@keyframes scoreBounce {
  0% { transform: translateY(8px) scale(0.8); opacity: 0; }
  60% { transform: translateY(-4px) scale(1.1); opacity: 1; }
  100% { transform: translateY(0) scale(1); opacity: 1; }
}

.feedback { color: var(--text-secondary); font-size: var(--text-sm); }

/* 按钮行:左右分布,上一项 + 下一项 */
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
}

/* 占位符:第一项时保持下一项按钮居右 */
.action-placeholder {
  flex: 1;
}

/* 下一项按钮占据主要空间 */
.action-next {
  flex: 1;
}
.score-area.scoring { display: flex; align-items: center; justify-content: center; gap: var(--space-3); }
.scoring-dot { width: 16px; height: 16px; border: 2px solid var(--border-light); border-top-color: var(--color-orange); border-radius: var(--radius-pill); }

@media (prefers-reduced-motion: no-preference) {
  .scoring-dot { animation: spin 0.8s linear infinite; }
}

@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .sentence-text { font-size: 1.5rem; }
  .emoji { font-size: 4rem; }
}
</style>
