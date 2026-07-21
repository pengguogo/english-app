<!--
  WrongAnswersView.vue - 错题集页面
  用途: 展示用户答错的 QUIZ/CALCULATE 题目,支持按掌握状态筛选、标记已掌握、跳转重做。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description 错题集页面:统计卡片 + 筛选标签 + 错题卡片列表 + 空状态。
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getWrongAnswers,
  getWrongAnswerStats,
  resolveWrongAnswer
} from '../api/wrongAnswer'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import mascotCompanion from '../assets/mascot/mascot-companion.jpg'

const router = useRouter()

// 错题列表
const wrongAnswers = ref([])
// 统计数据 { total, unresolved, resolved }
const stats = ref({ total: 0, unresolved: 0, resolved: 0 })
// 当前筛选状态: 'all' | 'unresolved' | 'resolved'
const filter = ref('all')
// 加载状态
const isLoading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  await Promise.all([loadStats(), loadWrongAnswers()])
})

/**
 * 加载错题统计数据。
 * 失败时降级为零值,不影响列表展示。
 */
async function loadStats() {
  try {
    stats.value = await getWrongAnswerStats()
  } catch (e) {
    console.error('加载错题统计失败:', e)
    stats.value = { total: 0, unresolved: 0, resolved: 0 }
  }
}

/**
 * 加载错题列表,按当前筛选状态请求。
 */
async function loadWrongAnswers() {
  isLoading.value = true
  errorMsg.value = ''
  try {
    // 将筛选状态转换为 resolved 参数:all=null, unresolved=false, resolved=true
    const resolvedParam = filter.value === 'all' ? null : filter.value === 'resolved'
    wrongAnswers.value = await getWrongAnswers(resolvedParam)
  } catch (e) {
    errorMsg.value = '加载失败,请返回重试'
    console.error('加载错题列表失败:', e)
  } finally {
    isLoading.value = false
  }
}

/**
 * 切换筛选状态并重新加载列表。
 * @param {string} status 目标筛选状态
 */
async function switchFilter(status) {
  if (filter.value === status) return
  filter.value = status
  await loadWrongAnswers()
}

/**
 * 标记某条错题为已掌握。
 * 成功后从当前列表移除该条,并刷新统计。
 * @param {Object} item 错题对象
 */
async function handleResolve(item) {
  try {
    await resolveWrongAnswer(item.id)
    // 从列表中移除
    wrongAnswers.value = wrongAnswers.value.filter((w) => w.id !== item.id)
    // 刷新统计
    await loadStats()
  } catch (e) {
    console.error('标记已掌握失败:', e)
    alert('操作失败,请重试')
  }
}

/**
 * 跳转到对应课时重做。
 * @param {Object} item 错题对象
 */
function redoLesson(item) {
  router.push(`/lesson/${item.lessonId}`)
}

/**
 * 解析 questionSnapshot,提取题干用于展示。
 * 容错处理:解析失败时回退为原始字符串。
 * @param {Object} item 错题对象
 * @return {Object} 解析后的题目对象
 */
function parseQuestion(item) {
  if (!item.questionSnapshot) return {}
  try {
    return typeof item.questionSnapshot === 'string'
      ? JSON.parse(item.questionSnapshot)
      : item.questionSnapshot
  } catch (e) {
    return { question: String(item.questionSnapshot) }
  }
}

/**
 * 获取题干文本。
 * @param {Object} item 错题对象
 * @return {string} 题干
 */
function getQuestionText(item) {
  const q = parseQuestion(item)
  return q.question || q.sentence || q.word || '题目内容'
}

/**
 * 获取题目类型标签文本。
 * @param {string} type 题目类型
 * @return {string} 中文标签
 */
function getTypeText(type) {
  if (type === 'QUIZ') return '选择题'
  if (type === 'CALCULATE') return '计算题'
  return type || '题目'
}
</script>

<template>
  <div class="wrong-answers-view">
    <BackBar title="错题集" @back="router.back()" />

    <!-- 顶部统计卡片行 -->
    <div class="stats-row">
      <div class="stat-card stat-warning">
        <span class="stat-num">{{ stats.total }}</span>
        <span class="stat-label">总错题</span>
      </div>
      <div class="stat-card stat-primary">
        <span class="stat-num">{{ stats.unresolved }}</span>
        <span class="stat-label">未掌握</span>
      </div>
      <div class="stat-card stat-success">
        <span class="stat-num">{{ stats.resolved }}</span>
        <span class="stat-label">已掌握</span>
      </div>
    </div>

    <!-- 筛选标签栏 -->
    <div class="filter-bar">
      <button
        :class="['filter-pill', { active: filter === 'all' }]"
        @click="switchFilter('all')"
      >全部</button>
      <button
        :class="['filter-pill', { active: filter === 'unresolved' }]"
        @click="switchFilter('unresolved')"
      >未掌握</button>
      <button
        :class="['filter-pill', { active: filter === 'resolved' }]"
        @click="switchFilter('resolved')"
      >已掌握</button>
    </div>

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
    <div v-else-if="wrongAnswers.length === 0" class="empty-state">
      <img :src="mascotCompanion" alt="小老鼠陪伴" class="empty-mascot" />
      <p class="empty-text">还没有错题哦,继续加油！</p>
    </div>

    <!-- 错题卡片列表 -->
    <div v-else class="card-list">
      <div
        v-for="item in wrongAnswers"
        :key="item.id"
        class="wrong-card"
        :class="{ resolved: item.resolved }"
      >
        <!-- 顶部:类型标签 + 错误次数徽章 -->
        <div class="card-top">
          <span class="type-tag">{{ getTypeText(item.questionType) }}</span>
          <span class="count-badge">错 {{ item.wrongCount || 1 }} 次</span>
          <span v-if="item.resolved" class="resolved-tag">已掌握</span>
        </div>

        <!-- 题干 -->
        <h3 class="question-text">{{ getQuestionText(item) }}</h3>

        <!-- 来源课时 -->
        <p v-if="item.lessonName" class="lesson-source">来源: {{ item.lessonName }}</p>

        <!-- 答案对比 -->
        <div class="answer-row">
          <div class="answer-box answer-wrong">
            <span class="answer-label">你的答案</span>
            <span class="answer-text">{{ item.userAnswer || '未作答' }}</span>
          </div>
          <div class="answer-box answer-correct">
            <span class="answer-label">正确答案</span>
            <span class="answer-text">{{ item.correctAnswer || '-' }}</span>
          </div>
        </div>

        <!-- 操作区 -->
        <div class="card-actions">
          <AppButton
            v-if="!item.resolved"
            variant="ghost"
            size="md"
            @click="handleResolve(item)"
          >标记已掌握</AppButton>
          <AppButton
            variant="primary"
            size="md"
            class="action-redo"
            @click="redoLesson(item)"
          >跳转重做</AppButton>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 页面容器 */
.wrong-answers-view {
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

/* 总错题: 警示色顶条 */
.stat-warning {
  border-top-color: var(--color-warning);
}

/* 未掌握: 主色顶条 */
.stat-primary {
  border-top-color: var(--color-primary);
}

/* 已掌握: 成功色顶条 */
.stat-success {
  border-top-color: var(--color-success);
}

.stat-num {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.stat-warning .stat-num { color: var(--color-warning); }
.stat-primary .stat-num { color: var(--color-primary); }
.stat-success .stat-num { color: var(--color-success); }

.stat-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* ===== 筛选标签栏 ===== */
.filter-bar {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-5);
  flex-wrap: wrap;
}

.filter-pill {
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
  background: var(--bg-card);
  color: var(--text-secondary);
  border: 1px solid var(--border-light);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-smooth);
}

.filter-pill:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

/* 选中态: 主色填充 */
.filter-pill.active {
  background: var(--gradient-primary);
  color: white;
  border-color: transparent;
  box-shadow: 0 2px 8px rgba(107, 124, 255, 0.3);
}

/* ===== 错题卡片列表 ===== */
.card-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.wrong-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  box-shadow: var(--shadow-card);
  transition: transform var(--duration-fast) var(--ease-bounce),
              box-shadow var(--duration-fast) var(--ease-smooth);
}

.wrong-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover);
}

/* 已掌握错题: 降低饱和度 */
.wrong-card.resolved {
  opacity: 0.75;
}

/* 卡片顶部 */
.card-top {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-3);
}

.type-tag {
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
  background: rgba(107, 124, 255, 0.12);
  color: var(--color-primary);
}

.count-badge {
  padding: var(--space-1) var(--space-2);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
  background: var(--color-warning-bg);
  color: var(--color-warning);
}

.resolved-tag {
  padding: var(--space-1) var(--space-2);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
  background: var(--color-success-bg);
  color: var(--color-success);
}

/* 题干 */
.question-text {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  line-height: 1.5;
  margin-bottom: var(--space-2);
}

.lesson-source {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin-bottom: var(--space-3);
}

/* 答案对比行 */
.answer-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.answer-box {
  padding: var(--space-3);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.answer-wrong {
  background: var(--color-warning-bg);
}

.answer-correct {
  background: var(--color-success-bg);
}

.answer-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.answer-wrong .answer-text {
  color: var(--color-warning);
  font-weight: var(--font-medium);
}

.answer-correct .answer-text {
  color: var(--color-success);
  font-weight: var(--font-medium);
}

/* 操作区 */
.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
}

.action-redo {
  flex: 0 0 auto;
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
