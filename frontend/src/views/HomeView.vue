<!--
  HomeView.vue - 首页：学科选择
  用途: 吉祥物欢迎 header + 四大学科卡片网格，儿童友好的视觉风格。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description 学科选择首页，展示英语/语文/数学/课外四个学科入口卡片。
  点击学科卡片跳转到 /subject/:subjectId 展示该学科下的主题列表。
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSubjects } from '../api/subject'
import mascotWelcome from '../assets/mascot/mascot-welcome.jpg'

const router = useRouter()
const subjects = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

// 学科卡片配色映射（CSS 变量名）
const subjectColorVars = {
  'ENGLISH': 'var(--subject-english)',
  'CHINESE': 'var(--subject-chinese)',
  'MATH': 'var(--subject-math)',
  'EXTRACURRICULAR': 'var(--subject-extracurricular)'
}

// 学科 emoji 映射（后续替换为 AI 生成图标）
const subjectEmojis = {
  'ENGLISH': 'ABC',
  'CHINESE': '语',
  'MATH': '123',
  'EXTRACURRICULAR': '🚂'
}

onMounted(async () => {
  try {
    subjects.value = await getSubjects()
  } catch (e) {
    errorMsg.value = '加载失败，请刷新重试'
    console.error('加载学科失败:', e)
  } finally {
    isLoading.value = false
  }
})

/**
 * 获取学科卡片的主题色
 * @param {string} code 学科代码
 * @return {string} CSS 变量
 */
function getSubjectColor(code) {
  return subjectColorVars[code] || 'var(--color-primary)'
}

/**
 * 获取学科卡片的图标文字
 * @param {string} code 学科代码
 * @return {string} 图标文字
 */
function getSubjectIcon(code) {
  return subjectEmojis[code] || '📚'
}
</script>

<template>
  <div class="home">
    <!-- 顶部欢迎 header -->
    <header class="welcome-header">
      <span class="decor cloud c1">☁️</span>
      <span class="decor star c2">⭐</span>
      <span class="decor star c3">✨</span>
      <div class="header-content">
        <div class="greeting">
          <h1>嗨，小朋友！</h1>
          <p>今天想学什么呢？🎈</p>
        </div>
        <img :src="mascotWelcome" alt="小老鼠 Mimi" class="mascot" />
      </div>
    </header>

    <!-- 快捷功能区 -->
    <section class="quick-section">
      <h2 class="section-title">快捷功能</h2>
      <div class="quick-grid">
        <!-- 错题集入口 -->
        <div
          class="quick-card"
          :style="{ '--card-accent': 'var(--color-warning)' }"
          @click="router.push('/wrong-answers')"
        >
          <div class="quick-left" :style="{ background: 'var(--color-warning)' }"></div>
          <div class="quick-body">
            <span class="quick-icon">📝</span>
            <div class="quick-text">
              <h3 class="quick-title">错题集</h3>
              <p class="quick-sub">复习错题</p>
            </div>
          </div>
        </div>
        <!-- 我学过的入口 -->
        <div
          class="quick-card"
          :style="{ '--card-accent': 'var(--color-primary)' }"
          @click="router.push('/learned')"
        >
          <div class="quick-left" :style="{ background: 'var(--color-primary)' }"></div>
          <div class="quick-body">
            <span class="quick-icon">📚</span>
            <div class="quick-text">
              <h3 class="quick-title">我学过的</h3>
              <p class="quick-sub">学习记录</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 学科选择区 -->
    <section class="subject-section">
      <h2 class="section-title">选择学科</h2>

      <!-- 加载中 -->
      <div v-if="isLoading" class="state-tip">
        <div class="loading-dot"></div>
        <p>加载中...</p>
      </div>

      <!-- 加载失败 -->
      <div v-else-if="errorMsg" class="state-tip error">
        <p>{{ errorMsg }}</p>
      </div>

      <!-- 学科卡片网格 -->
      <div v-else class="subject-grid">
        <div
          v-for="subject in subjects"
          :key="subject.id"
          class="subject-card"
          :style="{ '--card-accent': getSubjectColor(subject.code) }"
          @click="router.push(`/subject/${subject.id}`)"
        >
          <div class="card-icon" :style="{ background: getSubjectColor(subject.code) }">
            {{ getSubjectIcon(subject.code) }}
          </div>
          <h3 class="card-title">{{ subject.name }}</h3>
          <p class="card-desc">点击进入 →</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home {
  padding: var(--space-4);
  min-height: 100vh;
}

/* 欢迎 header */
.welcome-header {
  position: relative;
  background: var(--gradient-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  z-index: 1;
}

.greeting h1 {
  color: white;
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
}

.greeting p {
  color: rgba(255, 255, 255, 0.9);
  font-size: var(--text-base);
}

.mascot {
  width: 120px;
  height: 120px;
  object-fit: contain;
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

/* 飘浮装饰 */
.decor {
  position: absolute;
  z-index: 0;
}
.decor.cloud { font-size: 28px; }
.decor.star { font-size: 20px; }
.decor.c1 { top: var(--space-3); right: 40%; }
.decor.c2 { bottom: var(--space-4); left: var(--space-4); }
.decor.c3 { top: var(--space-6); right: var(--space-6); }

@media (prefers-reduced-motion: no-preference) {
  .decor.cloud { animation: float 6s ease-in-out infinite; }
  .decor.star { animation: float 4s ease-in-out infinite; }
  .decor.c3 { animation-delay: 1s; }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

/* 学科选择区 */
.section-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

/* ===== 快捷功能区 ===== */
.quick-section {
  margin-bottom: var(--space-6);
}

/* 快捷卡片网格:两个并排 */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-3);
}

/* 快捷卡片:白底圆角 + 左侧彩色竖条 */
.quick-card {
  position: relative;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  display: flex;
  align-items: center;
  gap: var(--space-3);
  cursor: pointer;
  box-shadow: var(--shadow-card);
  overflow: hidden;
  transition: transform var(--duration-fast) var(--ease-bounce),
              box-shadow var(--duration-fast) var(--ease-smooth);
}

.quick-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

/* 左侧彩色竖条 */
.quick-left {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  flex-shrink: 0;
}

/* 主体内容 */
.quick-body {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding-left: var(--space-2);
}

.quick-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.quick-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.quick-title {
  font-size: var(--text-base);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.quick-sub {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* 学科卡片网格 */
.subject-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-4);
}

/* 学科卡片 */
.subject-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  text-align: center;
  cursor: pointer;
  box-shadow: var(--shadow-card);
  border-top: 4px solid var(--card-accent, var(--color-primary));
  transition: transform var(--duration-fast) var(--ease-bounce),
              box-shadow var(--duration-fast) var(--ease-smooth);
}

.subject-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

/* 学科图标：彩色圆形背景 */
.card-icon {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-pill);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--space-3);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: white;
}

.card-title {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.card-desc {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

/* 状态提示 */
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

/* 响应式 */
@media (max-width: 480px) {
  .mascot { width: 96px; height: 96px; }
  .greeting h1 { font-size: var(--text-lg); }
}
</style>
