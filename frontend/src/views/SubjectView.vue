<!--
  SubjectView.vue - 学科主题列表页
  用途: 展示某学科下所有学习主题，点击进入主题详情。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description 学科主题列表页，根据路由参数 subjectId 加载该学科下的主题列表。
  复用原有的主题卡片样式和 themeConfig 配置。
 */
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getThemesBySubject } from '../api/subject'
import { getThemeConfig } from '../config/themeConfig'
import BackBar from '../components/BackBar.vue'

const router = useRouter()
const route = useRoute()
const themes = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

// 学科主题色映射（用于页面顶部 banner 配色）
const subjectColors = {
  1: 'var(--subject-english)',
  2: 'var(--subject-chinese)',
  3: 'var(--subject-math)',
  4: 'var(--subject-extracurricular)'
}

// 学科名称映射
const subjectNames = {
  1: '英语',
  2: '语文',
  3: '数学',
  4: '课外'
}

const subjectId = Number(route.params.subjectId)
const subjectColor = subjectColors[subjectId] || 'var(--color-primary)'
const subjectName = subjectNames[subjectId] || '学习'

onMounted(async () => {
  try {
    themes.value = await getThemesBySubject(subjectId)
  } catch (e) {
    errorMsg.value = '加载失败，请刷新重试'
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
  return getThemeConfig(id).emoji
}
</script>

<template>
  <div class="subject-page">
    <BackBar :title="subjectName + '学习'" />

    <!-- 主题选择区 -->
    <section class="theme-section">
      <!-- 加载中 -->
      <div v-if="isLoading" class="state-tip">
        <div class="loading-dot"></div>
        <p>加载中...</p>
      </div>

      <!-- 加载失败 -->
      <div v-else-if="errorMsg" class="state-tip error">
        <p>{{ errorMsg }}</p>
      </div>

      <!-- 空列表 -->
      <div v-else-if="themes.length === 0" class="state-tip">
        <p>暂无主题，敬请期待！</p>
      </div>

      <!-- 主题卡片网格 -->
      <div v-else class="theme-grid">
        <div
          v-for="theme in themes"
          :key="theme.id"
          class="theme-card"
          :class="{ locked: theme.isLocked }"
          :style="{ '--card-accent': subjectColor }"
          @click="!theme.isLocked && router.push(`/theme/${theme.id}`)"
        >
          <div class="card-icon">{{ getThemeIcon(theme.id) }}</div>
          <h3 class="card-title">{{ theme.name }}</h3>
          <p class="card-desc" v-if="theme.isLocked">待解锁</p>
          <p class="card-desc" v-else>点击进入</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.subject-page {
  min-height: 100vh;
  padding-bottom: var(--space-8);
}

/* 主题选择区 */
.theme-section {
  padding: var(--space-4);
}

/* 主题卡片网格 */
.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-4);
}

/* 主题卡片 */
.theme-card {
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
</style>
