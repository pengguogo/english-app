<!--
  ReadingLesson.vue - READING 类型课时模板
  用途: 图文翻页阅读，展示标题+正文+配图，支持朗读全文和翻页。
        适用于语文寓言故事、课外火车科普阅读。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description READING 类型课时学习模板，图文展示+翻页阅读+朗读功能。
 */
import { computed } from 'vue'
import AudioButton from '../AudioButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前阅读项 { title, content, image }
 * @property {number} currentIndex 当前索引
 * @property {number} totalItems 总项数
 * @property {boolean} isLastItem 是否最后一项
 */
const props = defineProps({
  /** 当前阅读项对象 */
  currentItem: { type: Object, default: null },
  /** 当前索引（从0开始） */
  currentIndex: { type: Number, default: 0 },
  /** 总项数 */
  totalItems: { type: Number, default: 0 },
  /** 是否最后一项 */
  isLastItem: { type: Boolean, default: false }
})

/**
 * 组件 Emits
 * @event next 切换下一项/完成
 */
const emit = defineEmits({
  /** 切换下一篇或完成阅读 */
  next: null
})

/**
 * 当前阅读项的朗读文本（标题+正文拼接）。
 */
const readAloudText = computed(() => {
  if (!props.currentItem) return ''
  const title = props.currentItem.title || ''
  const content = props.currentItem.content || ''
  return title ? `${title}。${content}` : content
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
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 页</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画 -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 阅读卡片: 配图 + 标题 + 正文 -->
    <div class="reading-card">
      <div v-if="currentItem.image" class="reading-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.title" class="reading-image" />
      </div>
      <h1 class="reading-title">{{ currentItem.title }}</h1>
      <p class="reading-content">{{ currentItem.content }}</p>
    </div>

    <!-- 操作区: 朗读 + 翻页 -->
    <div class="actions">
      <AudioButton :text="readAloudText" />
      <AppButton
        variant="primary"
        size="lg"
        block
        @click="emit('next')"
      >
        {{ isLastItem ? '完成阅读' : '下一页 →' }}
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

.reading-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
}

.reading-image-wrapper { margin-bottom: var(--space-4); text-align: center; }
.reading-image { width: 100%; max-width: 320px; height: 220px; object-fit: cover; border-radius: var(--radius-md); box-shadow: var(--shadow-soft); }

.reading-title { font-size: var(--text-lg); color: var(--text-primary); font-weight: var(--font-bold); margin-bottom: var(--space-3); text-align: center; }

.reading-content { font-size: var(--text-base); color: var(--text-secondary); line-height: 1.8; text-align: justify; }

.actions { display: flex; flex-direction: column; gap: var(--space-3); }

@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .reading-image { height: 180px; }
}
</style>
