<!--
  WordLesson.vue - WORD 类型课时模板
  用途: 单词认字学习，展示图片/emoji+英文+音标+释义，支持听发音和跟读评分。
        适用于英语单词课、语文汉字/拼音课、数学数字课。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description WORD 类型课时学习模板，展示当前学习项并提供听音+跟读评分交互。
 */
import { computed } from 'vue'
import StarBar from '../StarBar.vue'
import AudioButton from '../AudioButton.vue'
import RecordButton from '../RecordButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前学习项 { word, phonetic, translation, emoji, image }
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
  currentItem: {
    type: Object,
    default: null
  },
  /** 当前索引（从0开始） */
  currentIndex: {
    type: Number,
    default: 0
  },
  /** 总项数 */
  totalItems: {
    type: Number,
    default: 0
  },
  /** 当前项评分（null 表示未评分） */
  currentScore: {
    type: Number,
    default: null
  },
  /** 当前项星星数 */
  currentStars: {
    type: Number,
    default: 0
  },
  /** 评分反馈文案 */
  scoreMessage: {
    type: String,
    default: ''
  },
  /** 是否正在评分 */
  isScoring: {
    type: Boolean,
    default: false
  },
  /** 是否最后一项 */
  isLastItem: {
    type: Boolean,
    default: false
  }
})

/**
 * 组件 Emits
 * @event recorded 录音完成，传递 WAV Blob
 * @event next 切换下一项
 */
const emit = defineEmits({
  /** 录音完成，参数为 WAV Blob */
  recorded: null,
  /** 切换下一项 */
  next: null
})

/**
 * 当前学习项对应的文本（word 字段）。
 */
const currentText = computed(() => {
  if (!props.currentItem) return ''
  return props.currentItem.word || ''
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

    <!-- 学习项卡片: 图片/emoji + 英文 + 音标 + 中文释义 -->
    <div class="item-card">
      <div v-if="currentItem.image" class="word-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.word" class="word-image" />
      </div>
      <div v-else class="emoji">{{ currentItem.emoji }}</div>
      <h1 class="word">{{ currentText }}</h1>
      <p v-if="currentItem.phonetic" class="phonetic">/{{ currentItem.phonetic }}/</p>
      <p class="translation">{{ currentItem.translation }}</p>
    </div>

    <!-- 操作区: 听发音 + 跟读录音 -->
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

    <!-- 下一步 / 完成本课 -->
    <AppButton
      variant="primary"
      size="lg"
      block
      @click="emit('next')"
    >
      {{ isLastItem ? '完成本课' : '下一步 →' }}
    </AppButton>
  </div>
</template>

<style scoped>
.card-area {
  max-width: 480px;
  margin: 0 auto;
  position: relative;
}

/* 顶部进度条 */
.progress-section {
  margin-bottom: var(--space-5);
}

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

/* 学习项卡片 */
.item-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-8) var(--space-6);
  text-align: center;
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
}

.emoji {
  font-size: 5rem;
  margin-bottom: var(--space-4);
}

.word-image-wrapper {
  margin-bottom: var(--space-4);
}

.word-image {
  width: 180px;
  height: 180px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft);
}

.word {
  font-size: 2.5rem;
  margin-bottom: var(--space-2);
  color: var(--text-primary);
  word-break: break-word;
  font-weight: var(--font-bold);
}

.phonetic {
  color: var(--text-secondary);
  font-size: var(--text-lg);
  margin-bottom: var(--space-2);
}

.translation {
  color: var(--text-secondary);
  font-size: var(--text-base);
}

/* 操作区 */
.actions {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
  margin-bottom: var(--space-6);
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

/* 响应式 */
@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .word { font-size: 2rem; }
  .emoji { font-size: 4rem; }
}
</style>
