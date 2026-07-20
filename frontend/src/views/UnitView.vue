<!--
  UnitView.vue - 课时列表页:展示某单元下的所有课时
  用途: 学习卡片风格列表 + 类型标签药丸形 + 星星徽章 + 当前课时脉冲提示。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonsByUnit } from '../api/lesson'
import BackBar from '../components/BackBar.vue'

const route = useRoute()
const router = useRouter()
const lessons = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  const unitId = route.params.unitId
  try {
    lessons.value = await getLessonsByUnit(unitId)
  } catch (e) {
    errorMsg.value = '加载失败,请返回重试'
    console.error('加载课时失败:', e)
  } finally {
    isLoading.value = false
  }
})

/**
 * 根据课时类型返回标签配置(文字 + 颜色)。
 * @param {string} type 课时类型: WORD | SENTENCE
 * @return {Object} 标签配置 { text, class }
 */
function getTypeTag(type) {
  if (type === 'WORD') return { text: '单词课', class: 'tag-word' }
  return { text: '句型课', class: 'tag-sentence' }
}

/**
 * 判断课时是否可点击(当前仅第一课可学,后续按进度解锁)。
 * 简化逻辑: index === 0 可学,其余锁定。
 * @param {number} index 课时索引
 * @return {boolean} 是否可点击
 */
function isAvailable(index) {
  return index === 0
}
</script>

<template>
  <div class="unit-view">
    <BackBar title="课时列表" @back="router.back()" />

    <!-- 加载中 -->
    <div v-if="isLoading" class="state-tip">
      <div class="loading-dot"></div>
      <p>加载中...</p>
    </div>

    <!-- 加载失败 -->
    <div v-else-if="errorMsg" class="state-tip error">
      <p>{{ errorMsg }}</p>
    </div>

    <!-- 课时列表 -->
    <div v-else class="lesson-list">
      <div
        v-for="(lesson, index) in lessons"
        :key="lesson.id"
        class="lesson-card"
        :class="{
          locked: !isAvailable(index),
          current: isAvailable(index)
        }"
        @click="isAvailable(index) && router.push(`/lesson/${lesson.id}`)"
      >
        <!-- 左侧缩略图 -->
        <div class="lesson-thumb" :class="{ locked: !isAvailable(index) }">
          <span class="thumb-num">{{ index + 1 }}</span>
        </div>

        <!-- 中间信息 -->
        <div class="lesson-info">
          <h3 class="lesson-name">{{ lesson.name }}</h3>
          <span class="type-tag" :class="getTypeTag(lesson.type).class">
            {{ getTypeTag(lesson.type).text }}
          </span>
        </div>

        <!-- 右侧状态 -->
        <div class="lesson-status">
          <span v-if="isAvailable(index)" class="play-icon">▶</span>
          <span v-else class="lock-icon">🔒</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 页面容器 */
.unit-view {
  padding: var(--space-4);
  min-height: 100vh;
}

/* ===== 课时列表 ===== */
.lesson-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

/* 课时卡片: 学习卡片风格 */
.lesson-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: transform var(--duration-fast) var(--ease-bounce),
              box-shadow var(--duration-fast) var(--ease-smooth);
}

.lesson-card:not(.locked):hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-hover);
}

.lesson-card.locked {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 当前可学课时: 轻微脉冲提示 */
@media (prefers-reduced-motion: no-preference) {
  .lesson-card.current {
    animation: cardPulse 2s ease-in-out infinite;
  }
}

@keyframes cardPulse {
  0%, 100% { box-shadow: var(--shadow-card); }
  50% { box-shadow: 0 4px 20px rgba(107, 124, 255, 0.2); }
}

/* 左侧缩略图: 圆形数字 */
.lesson-thumb {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-md);
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(107, 124, 255, 0.2);
}

.lesson-thumb.locked {
  background: var(--bg-muted);
  box-shadow: none;
}

.thumb-num {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: white;
}

.lesson-thumb.locked .thumb-num {
  color: var(--text-tertiary);
}

/* 中间信息 */
.lesson-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.lesson-name {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

/* 类型标签: 药丸形 */
.type-tag {
  display: inline-block;
  width: fit-content;
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
}

/* 单词课: 蓝色 */
.type-tag.tag-word {
  background: rgba(107, 124, 255, 0.12);
  color: var(--color-primary);
}

/* 句型课: 橙色 */
.type-tag.tag-sentence {
  background: rgba(255, 152, 0, 0.12);
  color: var(--color-orange);
}

/* 右侧状态图标 */
.lesson-status {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.play-icon {
  color: var(--color-primary);
}

.lock-icon {
  opacity: 0.6;
}

/* ===== 状态提示 ===== */
.state-tip {
  text-align: center;
  padding: var(--space-8);
  color: var(--text-tertiary);
}

.state-tip.error {
  color: var(--color-warning);
}

.loading-dot {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-light);
  border-top-color: var(--color-primary);
  border-radius: var(--radius-pill);
  margin: 0 auto var(--space-3);
}

@media (prefers-reduced-motion: no-preference) {
  .loading-dot {
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
