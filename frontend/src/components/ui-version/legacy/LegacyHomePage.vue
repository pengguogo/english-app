<!--
  LegacyHomePage.vue - 老版首页
  用途: 保留原首页的学科选择与快捷入口逻辑，作为老版 UI 展示。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSubjects } from '../../../api/subject'
import MimiMascot from '../../MimiMascot.vue'
import AppButton from '../../AppButton.vue'
import PicturebookEntry from '../../picturebooks/PicturebookEntry.vue'
import ModelEntry from '../../models/ModelEntry.vue'

const router = useRouter()
const subjects = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

const subjectColorVars = {
  ENGLISH: 'var(--subject-english)',
  CHINESE: 'var(--subject-chinese)',
  MATH: 'var(--subject-math)',
  EXTRACURRICULAR: 'var(--subject-extracurricular)'
}

const subjectEmojis = {
  ENGLISH: 'ABC',
  CHINESE: '语',
  MATH: '123',
  EXTRACURRICULAR: '🚂'
}

onMounted(loadSubjects)

async function loadSubjects() {
  isLoading.value = true
  errorMsg.value = ''
  try {
    subjects.value = await getSubjects()
  } catch (error) {
    errorMsg.value = '加载失败，请刷新重试'
    console.error('加载学科失败:', error)
  } finally {
    isLoading.value = false
  }
}

function getSubjectColor(code) {
  return subjectColorVars[code] || 'var(--color-primary)'
}

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
    <ModelEntry />

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

    <section class="quick-section">
      <h2 class="section-title">快捷功能</h2>
      <div class="quick-grid">
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

        <button
          type="button"
          class="quick-card"
          :style="{ '--card-accent': 'var(--color-success)' }"
          @click="router.push('/games')"
        >
          <div class="quick-left" :style="{ background: 'var(--color-success)' }"></div>
          <div class="quick-body">
            <span class="quick-icon">🎮</span>
            <div class="quick-text">
              <h3 class="quick-title">游戏专区</h3>
              <p class="quick-sub">学习 5 分钟解锁</p>
            </div>
          </div>
        </button>
      </div>
    </section>

    <section class="subject-section">
      <h2 class="section-title">选择学科</h2>

      <div v-if="isLoading" class="state-tip" role="status" aria-live="polite">
        <div class="loading-dot"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="errorMsg" class="state-tip error" role="alert">
        <p>{{ errorMsg }}</p>
        <AppButton variant="ghost" @click="loadSubjects">重新加载</AppButton>
      </div>

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

.decor {
  position: absolute;
  z-index: 0;
}

.decor.cloud {
  font-size: 28px;
}

.decor.star {
  font-size: 20px;
}

.decor.c1 {
  top: var(--space-3);
  right: 40%;
}

.decor.c2 {
  bottom: var(--space-4);
  left: var(--space-4);
}

.decor.c3 {
  top: var(--space-6);
  right: var(--space-6);
}

@media (prefers-reduced-motion: no-preference) {
  .decor.cloud {
    animation: float 6s ease-in-out infinite;
  }

  .decor.star {
    animation: float 4s ease-in-out infinite;
  }

  .decor.c3 {
    animation-delay: 1s;
  }
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-8px);
  }
}

.section-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

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

.adventure-card:hover {
  transform: translateY(-4px) scale(1.01);
}

.adventure-card:active {
  transform: scale(0.99);
}

.adventure-image {
  width: 136px;
  height: 136px;
  flex-shrink: 0;
  object-fit: cover;
  border: 5px solid white;
  border-radius: 32px;
  box-shadow: var(--shadow-soft);
}

.adventure-copy {
  position: relative;
  z-index: 1;
}

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
  .adventure-stars {
    animation: fruitSparkle 1.8s ease-in-out infinite;
  }
}

@keyframes fruitSparkle {
  50% {
    transform: translateY(-5px) rotate(3deg);
  }
}

.quick-section {
  margin-bottom: var(--space-6);
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: var(--space-3);
}

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
  transition:
    transform var(--duration-fast) var(--ease-bounce),
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

.quick-left {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  flex-shrink: 0;
}

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

.subject-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-4);
}

.subject-card {
  width: 100%;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  text-align: center;
  cursor: pointer;
  box-shadow: var(--shadow-card);
  border-top: 4px solid var(--card-accent, var(--color-primary));
  transition:
    transform var(--duration-fast) var(--ease-bounce),
    box-shadow var(--duration-fast) var(--ease-smooth);
}

.subject-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

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
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 480px) {
  .greeting h1 {
    font-size: var(--text-lg);
  }

  .adventure-card {
    min-height: 156px;
    padding: var(--space-4);
    gap: var(--space-3);
  }

  .adventure-image {
    width: 92px;
    height: 92px;
    border-radius: 24px;
  }

  .adventure-copy p {
    font-size: var(--text-sm);
  }

  .adventure-stars {
    display: none;
  }
}
</style>
