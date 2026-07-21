<!--
  DialogueLesson.vue - DIALOGUE 类型课时模板
  用途: 情景对话学习，展示聊天气泡式对话，支持逐句播放和角色扮演跟读。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description DIALOGUE 类型课时模板，展示对话气泡并提供逐句播放+跟读评分交互。
 */
import { computed } from 'vue'
import StarBar from '../StarBar.vue'
import AudioButton from '../AudioButton.vue'
import RecordButton from '../RecordButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前对话项 { speaker, text, translation, audio }
 * @property {number} currentIndex 当前索引（从0开始）
 * @property {number} totalItems 总句数
 * @property {number} currentScore 当前项评分（null 表示未评分）
 * @property {number} currentStars 当前项星星数
 * @property {string} scoreMessage 评分反馈文案
 * @property {boolean} isScoring 是否正在评分
 * @property {boolean} isLastItem 是否最后一项
 */
const props = defineProps({
  /** 当前对话项对象 */
  currentItem: { type: Object, default: null },
  /** 当前索引（从0开始） */
  currentIndex: { type: Number, default: 0 },
  /** 总句数 */
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
 * @event next 切换下一句
 * @event prev 切换上一句
 */
const emit = defineEmits({
  /** 录音完成，参数为 WAV Blob */
  recorded: null,
  /** 切换下一句 */
  next: null,
  /** 切换上一句 */
  prev: null
})

/**
 * 当前台词文本（用于 TTS 和跟读）。
 */
const currentText = computed(() => {
  if (!props.currentItem) return ''
  return props.currentItem.text || ''
})

/**
 * 是否为 Mimi 发言（左侧气泡）。
 */
const isMimi = computed(() => {
  return props.currentItem?.speaker === 'Mimi'
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
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 句</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画（右上角） -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 对话气泡: Mimi 左侧，NPC 右侧 -->
    <div v-if="currentItem" class="dialogue-card" :class="{ 'mimi-side': isMimi, 'npc-side': !isMimi }">
      <!-- Mimi 发言: 左侧 + 头像 -->
      <div v-if="isMimi" class="avatar-wrapper">
        <img :src="mascotCompanion" alt="Mimi 头像" class="avatar" />
        <span class="speaker-name">Mimi</span>
      </div>
      <div class="bubble" aria-live="polite">
        <p class="dialogue-text">{{ currentItem.text }}</p>
        <p class="dialogue-translation">{{ currentItem.translation }}</p>
      </div>
      <!-- NPC 发言: 右侧 + 角色标签 -->
      <div v-if="!isMimi" class="avatar-wrapper npc">
        <span class="speaker-badge">{{ currentItem.speaker }}</span>
      </div>
    </div>

    <!-- 操作区: 听发音 + 跟读录音 -->
    <div class="actions">
      <AudioButton :text="currentText" :translation="currentItem?.translation || ''" />
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

    <!-- 上一步 / 下一步 -->
    <div class="action-row">
      <AppButton
        v-if="currentIndex > 0"
        variant="ghost"
        size="md"
        @click="emit('prev')"
      >← 上一句</AppButton>
      <span v-else class="action-placeholder"></span>
      <AppButton
        variant="primary"
        size="md"
        class="action-next"
        @click="emit('next')"
      >
        {{ isLastItem ? '完成本课' : '下一句 →' }}
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

/* 对话气泡卡片 */
.dialogue-card {
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
  margin-bottom: var(--space-6);
}
.dialogue-card.mimi-side { flex-direction: row; }
.dialogue-card.npc-side { flex-direction: row-reverse; }

/* 头像/标签区域 */
.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-1);
  flex-shrink: 0;
}
.avatar {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-pill);
  object-fit: cover;
  box-shadow: var(--shadow-soft);
}
.speaker-name {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}
.speaker-badge {
  padding: var(--space-1) var(--space-2);
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-pill);
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
}

/* 对话气泡 */
.bubble {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-4) var(--space-5);
  box-shadow: var(--shadow-card);
  max-width: 70%;
}
.npc-side .bubble {
  background: var(--color-primary);
  color: white;
}
.dialogue-text {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
  word-break: break-word;
}
.dialogue-translation {
  font-size: var(--text-sm);
  opacity: 0.8;
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

/* 分数弹跳动画 + 气泡弹出动画 */
@media (prefers-reduced-motion: no-preference) {
  .score {
    animation: scoreBounce var(--duration-normal) var(--ease-bounce) both;
  }
  .bubble {
    animation: bubblePop var(--duration-normal) var(--ease-bounce) both;
  }
}
@keyframes scoreBounce {
  0% { transform: translateY(8px) scale(0.8); opacity: 0; }
  60% { transform: translateY(-4px) scale(1.1); opacity: 1; }
  100% { transform: translateY(0) scale(1); opacity: 1; }
}
@keyframes bubblePop {
  0% { transform: scale(0.8); opacity: 0; }
  60% { transform: scale(1.05); opacity: 1; }
  100% { transform: scale(1); opacity: 1; }
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

/* 按钮行 */
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
}
.action-placeholder { flex: 1; }
.action-next { flex: 1; }

/* 响应式: 手机 <480px */
@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .avatar { width: 48px; height: 48px; }
  .bubble { max-width: 75%; padding: var(--space-3) var(--space-4); }
  .dialogue-text { font-size: var(--text-base); }
}
</style>
