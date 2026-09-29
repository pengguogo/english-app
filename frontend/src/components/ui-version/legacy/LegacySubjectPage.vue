<!--
  LegacySubjectPage.vue - 老版学科页
  用途: 保留原学科主题列表展示与真实跳转逻辑，作为老版 UI 展示。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getThemesBySubject } from '../../../api/subject'
import { getThemeConfig } from '../../../config/themeConfig'
import { useSafeBack } from '../../../composables/useSafeBack'
import BackBar from '../../BackBar.vue'
import AppButton from '../../AppButton.vue'

const router = useRouter()
const route = useRoute()
const { safeBack } = useSafeBack()
const themes = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

const subjectColors = {
  1: 'var(--subject-english)',
  2: 'var(--subject-chinese)',
  3: 'var(--subject-math)',
  4: 'var(--subject-extracurricular)'
}

const subjectNames = {
  1: '英语',
  2: '语文',
  3: '数学',
  4: '课外'
}

const subjectId = Number(route.params.subjectId)
const subjectColor = subjectColors[subjectId] || 'var(--color-primary)'
const subjectName = subjectNames[subjectId] || '学习'

onMounted(loadThemes)

async function loadThemes() {
  isLoading.value = true
  errorMsg.value = ''
  try {
    themes.value = await getThemesBySubject(subjectId)
  } catch (error) {
    errorMsg.value = '加载失败，请刷新重试'
    console.error('加载主题失败:', error)
  } finally {
    isLoading.value = false
  }
}

function getThemeIcon(theme) {
  return getThemeConfig(theme.id, theme.name).emoji
}

function goBack() {
  safeBack('/')
}

function openTheme(theme) {
  router.push({
    path: `/theme/${theme.id}`,
    query: {
      subjectId: String(subjectId),
      subjectName,
      themeName: theme.name
    }
  })
}
</script>

<template>
  <div class="subject-page">
    <BackBar :title="subjectName + '学习'" @back="goBack" />

    <section class="theme-section">
      <div v-if="isLoading" class="state-tip" role="status" aria-live="polite">
        <div class="loading-dot"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="errorMsg" class="state-tip error" role="alert">
        <p>{{ errorMsg }}</p>
        <AppButton variant="ghost" @click="loadThemes">重新加载</AppButton>
      </div>

      <div v-else-if="themes.length === 0" class="state-tip">
        <p>暂无主题，敬请期待！</p>
      </div>

      <div v-else class="theme-grid">
        <button
          v-for="theme in themes"
          :key="theme.id"
          type="button"
          class="theme-card"
          :style="{ '--card-accent': subjectColor }"
          @click="openTheme(theme)"
        >
          <div class="card-icon">{{ getThemeIcon(theme) }}</div>
          <h3 class="card-title">{{ theme.name }}</h3>
          <p class="card-desc">点击进入</p>
        </button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.subject-page {
  min-height: 100vh;
  padding-bottom: var(--space-8);
}

.theme-section {
  padding: var(--space-4);
}

.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-4);
}

.theme-card {
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

.theme-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

.theme-card:active {
  transform: scale(0.98);
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
</style>
