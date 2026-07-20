<!--
  HomeView.vue - 首页:展示学习主题列表
  用途: 吉祥物欢迎 header + 主题卡片网格,儿童友好的视觉风格。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getThemes } from '../api/theme'
import mascotWelcome from '../assets/mascot/mascot-welcome.jpg'

const router = useRouter()
const themes = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  try {
    themes.value = await getThemes()
  } catch (e) {
    errorMsg.value = '加载失败,请刷新重试'
    console.error('加载主题失败:', e)
  } finally {
    isLoading.value = false
  }
})

/**
 * 根据主题 ID 返回对应的 emoji 图标。
 * @param {number} id 主题 ID
 * @return {string} emoji 字符
 */
function getThemeIcon(id) {
  const iconMap = { 1: '🍎', 2: '🚗' }
  return iconMap[id] || '📚'
}
</script>

<template>
  <div class="home">
    <!-- 顶部欢迎 header: 渐变背景 + 吉祥物 + 飘浮装饰 -->
    <header class="welcome-header">
      <span class="decor cloud c1">☁️</span>
      <span class="decor star c2">⭐</span>
      <span class="decor star c3">✨</span>
      <div class="header-content">
        <div class="greeting">
          <h1>嗨，小朋友！</h1>
          <p>今天一起学英语吧 🎈</p>
        </div>
        <img :src="mascotWelcome" alt="小老鼠 Mimi" class="mascot" />
      </div>
    </header>

    <!-- 主题选择区 -->
    <section class="theme-section">
      <h2 class="section-title">选择主题</h2>

      <!-- 加载中 -->
      <div v-if="isLoading" class="state-tip">
        <div class="loading-dot"></div>
        <p>加载中...</p>
      </div>

      <!-- 加载失败 -->
      <div v-else-if="errorMsg" class="state-tip error">
        <p>{{ errorMsg }}</p>
      </div>

      <!-- 主题卡片网格 -->
      <div v-else class="theme-grid">
        <div
          v-for="theme in themes"
          :key="theme.id"
          class="theme-card"
          :class="{ locked: theme.isLocked }"
          @click="!theme.isLocked && router.push(`/theme/${theme.id}`)"
        >
          <div class="card-icon">{{ getThemeIcon(theme.id) }}</div>
          <h3 class="card-title">{{ theme.name }}</h3>
          <p class="card-desc" v-if="theme.isLocked">🔒 待解锁</p>
          <p class="card-desc" v-else>点击进入 →</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 首页容器: 暖底 + 上下内边距 */
.home {
  padding: var(--space-4);
  min-height: 100vh;
}

/* ===== 欢迎 header ===== */
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

/* 吉祥物图片 */
.mascot {
  width: 120px;
  height: 120px;
  object-fit: contain;
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

/* 飘浮装饰元素 */
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

/* ===== 主题选择区 ===== */
.section-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

/* 主题卡片网格 */
.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-4);
}

/* 主题卡片: 白底 + 大圆角 + 柔和阴影 */
.theme-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  text-align: center;
  cursor: pointer;
  box-shadow: var(--shadow-card);
  transition: transform var(--duration-fast) var(--ease-bounce),
              box-shadow var(--duration-fast) var(--ease-smooth);
}

.theme-card:not(.locked):hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

.theme-card.locked {
  opacity: 0.6;
  cursor: not-allowed;
}

.card-icon {
  font-size: 3.5rem;
  margin-bottom: var(--space-3);
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

/* ===== 响应式: 手机端缩小吉祥物 ===== */
@media (max-width: 480px) {
  .mascot { width: 96px; height: 96px; }
  .greeting h1 { font-size: var(--text-lg); }
}
</style>
