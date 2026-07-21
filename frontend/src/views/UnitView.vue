<!--
  UnitView.vue - 课时列表页:展示某单元下的所有课时
  用途: 学习卡片风格列表 + 类型标签药丸形 + 星星徽章 + 当前课时脉冲提示。
  修改: 2026-07-21 去掉锁定逻辑,所有课时均可点击学习,保留已完成/当前可学的状态区分。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonsByUnit } from '../api/lesson'
import { getUnitProgress } from '../api/progress'
import BackBar from '../components/BackBar.vue'
import StarBar from '../components/StarBar.vue'

const route = useRoute()
const router = useRouter()
const lessons = ref([])
// 课时进度映射: lessonId → { status, stars, score }
const progressMap = ref(new Map())
const isLoading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  const unitId = route.params.unitId
  try {
    // 并行请求课时列表与单元进度,提升首屏加载速度
    // 进度接口失败时降级为 null,课时列表仍可正常展示
    const [lessonList, progressList] = await Promise.all([
      getLessonsByUnit(unitId),
      getUnitProgress(unitId).catch(() => null)
    ])
    lessons.value = lessonList
    if (progressList) {
      progressMap.value = new Map(progressList.map(p => [p.lessonId, p]))
    }
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
 * 获取某课时的进度对象。
 * @param {Object} lesson 课时对象
 * @return {Object|undefined} 进度对象 { status, stars, score }
 */
function getProgress(lesson) {
  return progressMap.value.get(lesson.id)
}

/**
 * 判断课时是否可点击。
 * 去除锁定逻辑后,所有课时均可点击学习。
 * @param {Object} lesson 课时对象
 * @return {boolean} 是否可点击(恒为 true)
 */
function isAvailable(lesson) {
  return true
}

/**
 * 判断课时是否为当前正在学习的课时(IN_PROGRESS)。
 * @param {Object} lesson 课时对象
 * @return {boolean} 是否为当前课时
 */
function isCurrent(lesson) {
  return getProgress(lesson)?.status === 'IN_PROGRESS'
}

/**
 * 判断课时是否已完成(COMPLETED)。
 * @param {Object} lesson 课时对象
 * @return {boolean} 是否已完成
 */
function isCompleted(lesson) {
  return getProgress(lesson)?.status === 'COMPLETED'
}

/**
 * 获取已完成课时的星星数。
 * @param {Object} lesson 课时对象
 * @return {number} 星星数(0-3)
 */
function getStars(lesson) {
  return getProgress(lesson)?.stars || 0
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
          current: isCurrent(lesson),
          completed: isCompleted(lesson)
        }"
        @click="router.push(`/lesson/${lesson.id}`)"
      >
        <!-- 左侧缩略图 -->
        <div
          class="lesson-thumb"
          :class="{
            completed: isCompleted(lesson)
          }"
        >
          <span v-if="isCompleted(lesson)" class="thumb-check">✓</span>
          <span v-else class="thumb-num">{{ index + 1 }}</span>
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
          <!-- 已完成: 显示星星徽章 -->
          <StarBar v-if="isCompleted(lesson)" :stars="getStars(lesson)" size="sm" />
          <!-- 当前可学: 播放图标 -->
          <span v-else class="play-icon">▶</span>
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

.lesson-card:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-hover);
}

/* 已完成课时: 成功色左边框,给予成就感 */
.lesson-card.completed {
  border-left: 4px solid var(--color-success);
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

/* 已完成课时缩略图: 成功色背景 + 对勾 */
.lesson-thumb.completed {
  background: var(--color-success);
  box-shadow: 0 2px 8px rgba(76, 217, 100, 0.3);
}

.thumb-num {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: white;
}

.thumb-check {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: white;
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
  display: flex;
  align-items: center;
}

.play-icon {
  color: var(--color-primary);
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
