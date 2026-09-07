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
import MimiMascot from '../components/MimiMascot.vue'
import AppButton from '../components/AppButton.vue'
import PicturebookEntry from '../components/picturebooks/PicturebookEntry.vue'

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

onMounted(loadSubjects)

async function loadSubjects() {
  isLoading.value = true
  errorMsg.value = ''
  try {
    subjects.value = await getSubjects()
  } catch (e) {
    errorMsg.value = '加载失败，请刷新重试'
    console.error('加载学科失败:', e)
  } finally {
    isLoading.value = false
  }
}

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

function startFruitAdventure() {
  router.push({
    path: '/theme/1',
    query: {
      subjectId: '1',
      subjectName: '英语',
      themeName: '水果乐园'
    }
  })
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
          <h1>Mimi 启蒙乐园</h1>
          <p>每天 15 分钟，陪孩子快乐成长 🎈</p>
        </div>
        <MimiMascot variant="welcome" size="lg" />
      </div>
    </header>

    <PicturebookEntry />

    <!-- 英语趣味化试点：首屏一次点击开始 -->
    <section class="adventure-section" aria-labelledby="adventure-title">
      <button type="button" class="adventure-card" @click="startFruitAdventure">
        <img :src="'/images/fruit/apple.jpg'" alt="红苹果" class="adventure-image" />
        <div class="adventure-copy">
          <span class="adventure-kicker">今日冒险 · ENGLISH</span>
          <h2 id="adventure-title">帮 Mimi 收集水果</h2>
          <p>听一听、说一说、猜一猜，闯过 3 个小关卡。</p>
          <span class="adventure-action">▶ 开始冒险</span>
        </div>
        <span class="adventure-stars" aria-hidden="true">⭐ 🍌 ✨</span>
      </button>
    </section>

    <!-- 快捷功能区 -->
    <section class="quick-section">
      <h2 class="section-title">快捷功能</h2>
      <div class="quick-grid">
        <!-- 错题集入口 -->
        <button
          type="button"
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
        </button>
        <!-- 我学过的入口 -->
        <button
          type="button"
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
        </button>
      </div>
    </section>

    <!-- 学科选择区 -->
    <section class="subject-section">
      <h2 class="section-title">选择学科</h2>

      <!-- 加载中 -->
      <div v-if="isLoading" class="state-tip" role="status" aria-live="polite">
        <div class="loading-dot"></div>
        <p>加载中...</p>
      </div>

      <!-- 加载失败 -->
      <div v-else-if="errorMsg" class="state-tip error" role="alert">
        <p>{{ errorMsg }}</p>
        <AppButton variant="ghost" @click="loadSubjects">重新加载</AppButton>
      </div>

      <!-- 学科卡片网格 -->
      <div v-else class="subject-grid">
        <button
          v-for="subject in subjects"
          :key="subject.id"
          type="button"
          class="subject-card"
          :style="{ '--card-accent': getSubjectColor(subject.code) }"
          @click="router.push(`/subject/${subject.id}`)"
        >
          <div class="card-icon" :style="{ background: getSubjectColor(subject.code) }">
            {{ getSubjectIcon(subject.code) }}
          </div>
          <h3 class="card-title">{{ subject.name }}</h3>
          <p class="card-desc">点击进入 →</p>
        </button>
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

/* ===== 今日英语冒险 ===== */
.adventure-section {
  margin-bottom: var(--space-6);
}

.adventure-card {
  width: 100%;
  min-height: 184px;
  display: flex;
  align-items: center;
  gap: var(--space-5);
  position: relative;
  overflow: hidden;
  padding: var(--space-5);
  text-align: left;
  background: var(--gradient-adventure);
  border: 3px solid var(--color-warning);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-hover);
  cursor: pointer;
  transition: transform var(--duration-fast) var(--ease-bounce);
}

.adventure-card:hover { transform: translateY(-4px) scale(1.01); }
.adventure-card:active { transform: scale(0.99); }

.adventure-image {
  width: 136px;
  height: 136px;
  flex-shrink: 0;
  object-fit: cover;
  border: 5px solid white;
  border-radius: 32px;
  box-shadow: var(--shadow-soft);
}

.adventure-copy { position: relative; z-index: 1; }
.adventure-kicker {
  display: inline-block;
  margin-bottom: var(--space-2);
  color: var(--color-orange);
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  letter-spacing: 0.08em;
}

.adventure-copy h2 {
  margin-bottom: var(--space-2);
  color: var(--text-primary);
  font-size: clamp(var(--text-lg), 3vw, var(--text-xl));
}

.adventure-copy p {
  margin-bottom: var(--space-3);
  color: var(--text-secondary);
}

.adventure-action {
  display: inline-flex;
  padding: var(--space-2) var(--space-4);
  color: white;
  background: var(--color-orange);
  border-radius: var(--radius-pill);
  font-weight: var(--font-bold);
}

.adventure-stars {
  position: absolute;
  top: var(--space-3);
  right: var(--space-4);
  font-size: 24px;
}

@media (prefers-reduced-motion: no-preference) {
  .adventure-stars { animation: fruitSparkle 1.8s ease-in-out infinite; }
}

@keyframes fruitSparkle {
  50% { transform: translateY(-5px) rotate(3deg); }
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
  width: 100%;
  text-align: left;
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

.quick-card:active,
.subject-card:active {
  transform: scale(0.98);
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
  width: 100%;
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

.state-tip.error p {
  margin-bottom: var(--space-3);
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
  .adventure-card { min-height: 156px; padding: var(--space-4); gap: var(--space-3); }
  .adventure-image { width: 92px; height: 92px; border-radius: 24px; }
  .adventure-copy p { font-size: var(--text-sm); }
  .adventure-stars { display: none; }
}
</style>
