<!--
  LearnedView.vue - 我学过的页面
  用途: 展示用户已学课时记录,顶部统计卡片 + 学科分布 + 已学课时列表,支持复习跳转。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description 我学过的页面:统计卡片 + 学科分布进度条 + 已学课时列表。
 */
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getLearnedLessons, getLearnedStats } from '../api/progress'
import BackBar from '../components/BackBar.vue'
import StarBar from '../components/StarBar.vue'
import mascotCompanion from '../assets/mascot/mascot-companion.jpg'

const router = useRouter()

// 已学课时列表
const learnedLessons = ref([])
// 统计数据 { learnedCount, totalStars, averageScore, subjectDistribution }
const stats = ref({ learnedCount: 0, totalStars: 0, averageScore: 0, subjectDistribution: [] })
const isLoading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  await Promise.all([loadStats(), loadLearnedLessons()])
})

/**
 * 加载学习统计数据。
 * 失败时降级为默认零值,不影响列表展示。
 */
async function loadStats() {
  try {
    stats.value = await getLearnedStats()
  } catch (e) {
    console.error('加载学习统计失败:', e)
    stats.value = { learnedCount: 0, totalStars: 0, averageScore: 0, subjectDistribution: [] }
  }
}

/**
 * 加载已学课时列表。
 */
async function loadLearnedLessons() {
  isLoading.value = true
  errorMsg.value = ''
  try {
    learnedLessons.value = await getLearnedLessons()
  } catch (e) {
    errorMsg.value = '加载失败,请返回重试'
    console.error('加载已学课时失败:', e)
  } finally {
    isLoading.value = false
  }
}

/**
 * 跳转到对应课时复习。
 * @param {Object} lesson 课时对象
 */
function reviewLesson(lesson) {
  router.push(`/lesson/${lesson.lessonId}`)
}

/**
 * 根据课时类型返回标签文本。
 * @param {string} type 课时类型
 * @return {string} 中文标签
 */
function getTypeText(type) {
  const map = {
    WORD: '单词课',
    SENTENCE: '句型课',
    READING: '阅读课',
    QUIZ: '选择题',
    CALCULATE: '计算课'
  }
  return map[type] || type || '课时'
}

/**
 * 格式化完成时间为简短日期字符串。
 * @param {string} iso 时间字符串
 * @return {string} 格式化后的日期
 */
function formatTime(iso) {
  if (!iso) return ''
  try {
    const d = new Date(iso)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  } catch (e) {
    return ''
  }
}

/**
 * 学科分布数据,带最大值用于进度条归一化。
 */
const subjectDistribution = computed(() => {
  const dist = stats.value.subjectDistribution || []
  const maxCount = dist.reduce((m, s) => Math.max(m, s.count || 0), 1)
  return dist.map((s) => ({
    ...s,
    percent: Math.round(((s.count || 0) / maxCount) * 100)
  }))
})
</script>

<template>
  <div class="learned-view">
    <BackBar title="我学过的" @back="router.back()" />

    <!-- 顶部统计卡片行 -->
    <div class="stats-row">
      <div class="stat-card stat-primary">
        <span class="stat-num">{{ stats.learnedCount }}</span>
        <span class="stat-label">已学课时</span>
      </div>
      <div class="stat-card stat-accent">
        <span class="stat-num">{{ stats.totalStars }}</span>
        <span class="stat-label">总星星</span>
      </div>
      <div class="stat-card stat-orange">
        <span class="stat-num">{{ stats.averageScore }}</span>
        <span class="stat-label">平均分</span>
      </div>
    </div>

    <!-- 学科分布展示 -->
    <section v-if="subjectDistribution.length > 0" class="distribution-section">
      <h2 class="section-title">学科分布</h2>
      <div class="distribution-list">
        <div
          v-for="subject in subjectDistribution"
          :key="subject.subjectName"
          class="distribution-item"
        >
          <div class="dist-header">
            <span class="dist-name">{{ subject.subjectName }}</span>
            <span class="dist-count">{{ subject.count }} 课</span>
          </div>
          <div class="dist-bar">
            <div
              class="dist-fill"
              :style="{ width: subject.percent + '%' }"
            ></div>
          </div>
        </div>
      </div>
    </section>

    <!-- 加载中 -->
    <div v-if="isLoading" class="state-tip">
      <div class="loading-dot"></div>
      <p>加载中...</p>
    </div>

    <!-- 加载失败 -->
    <div v-else-if="errorMsg" class="state-tip error">
      <p>{{ errorMsg }}</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="learnedLessons.length === 0" class="empty-state">
      <img :src="mascotCompanion" alt="小老鼠陪伴" class="empty-mascot" />
      <p class="empty-text">快去学习第一课吧！</p>
    </div>

    <!-- 已学课时列表 -->
    <section v-else class="lesson-section">
      <h2 class="section-title">已学课时</h2>
      <div class="lesson-list">
        <div
          v-for="lesson in learnedLessons"
          :key="lesson.lessonId"
          class="lesson-card"
          @click="reviewLesson(lesson)"
        >
          <!-- 顶部:课时名 + 类型标签 -->
          <div class="card-header">
            <h3 class="lesson-name">{{ lesson.lessonName }}</h3>
            <span class="type-tag">{{ getTypeText(lesson.lessonType) }}</span>
          </div>

          <!-- 中部:星级 + 分数 -->
          <div class="card-mid">
            <StarBar :stars="lesson.stars || 0" size="sm" />
            <span class="lesson-score">{{ lesson.score || 0 }} 分</span>
          </div>

          <!-- 底部:层级路径 + 完成时间 -->
          <div class="card-footer">
            <span class="lesson-path">
              {{ lesson.subjectName }} > {{ lesson.themeName }} > {{ lesson.unitName }}
            </span>
            <span v-if="lesson.completedAt" class="lesson-time">
              {{ formatTime(lesson.completedAt) }}
            </span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 页面容器 */
.learned-view {
  padding: var(--space-4);
  min-height: 100vh;
}

/* ===== 统计卡片行 ===== */
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-3);
  margin-bottom: var(--space-5);
}

.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: var(--space-4) var(--space-2);
  text-align: center;
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  border-top: 4px solid var(--color-primary);
}

/* 已学课时: 主色顶条 */
.stat-primary {
  border-top-color: var(--color-primary);
}

/* 总星星: 强调色顶条 */
.stat-accent {
  border-top-color: var(--color-accent);
}

/* 平均分: 橙色顶条 */
.stat-orange {
  border-top-color: var(--color-orange);
}

.stat-num {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.stat-primary .stat-num { color: var(--color-primary); }
.stat-accent .stat-num { color: var(--color-orange); }
.stat-orange .stat-num { color: var(--color-orange); }

.stat-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* ===== 学科分布 ===== */
.distribution-section {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
}

.section-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

.distribution-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.distribution-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.dist-header {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-sm);
}

.dist-name {
  color: var(--text-primary);
  font-weight: var(--font-medium);
}

.dist-count {
  color: var(--text-tertiary);
}

.dist-bar {
  height: 8px;
  background: var(--bg-muted);
  border-radius: var(--radius-pill);
  overflow: hidden;
}

.dist-fill {
  height: 100%;
  background: var(--gradient-primary);
  border-radius: var(--radius-pill);
  transition: width var(--duration-slow) var(--ease-smooth);
}

/* ===== 已学课时列表 ===== */
.lesson-section {
  margin-bottom: var(--space-6);
}

.lesson-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.lesson-card {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}

.lesson-name {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

.type-tag {
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
  background: rgba(107, 124, 255, 0.12);
  color: var(--color-primary);
  flex-shrink: 0;
}

.card-mid {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}

.lesson-score {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-orange);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.lesson-path {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.lesson-time {
  flex-shrink: 0;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: var(--space-8) var(--space-4);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-4);
}

.empty-mascot {
  width: 160px;
  height: 160px;
  object-fit: contain;
  border-radius: var(--radius-md);
}

@media (prefers-reduced-motion: no-preference) {
  .empty-mascot {
    animation: gentleBounce 3s ease-in-out infinite;
  }
}

@keyframes gentleBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.empty-text {
  font-size: var(--text-base);
  color: var(--text-secondary);
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

/* ===== 响应式 ===== */
@media (max-width: 480px) {
  .empty-mascot { width: 128px; height: 128px; }
  .stat-num { font-size: var(--text-lg); }
}
</style>
