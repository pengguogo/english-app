<!--
  LegacyHomePage.vue - 老版首页
  用途: 首页大图宫格：今日任务大按钮 + 学科与功能入口，家长入口收进右上角小锁。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import TodayTask from '../../TodayTask.vue'
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSubjects } from '../../../api/subject'
import MimiMascot from '../../MimiMascot.vue'
import AppButton from '../../AppButton.vue'
import { buildHomeTiles } from '../../../utils/homeTiles'

const router = useRouter()
const subjects = ref([])
const isLoading = ref(true)
const errorMsg = ref('')
// 学科加载失败时仍展示固定入口
const tiles = computed(() => buildHomeTiles(subjects.value))

onMounted(loadSubjects)

async function loadSubjects() {
  isLoading.value = true
  errorMsg.value = ''
  try {
    subjects.value = await getSubjects()
  } catch (error) {
    errorMsg.value = '学科加载失败，请重试'
    console.error('加载学科失败:', error)
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="home">
    <header class="welcome-header">
      <span class="decor cloud c1" aria-hidden="true">☁️</span>
      <span class="decor star c2" aria-hidden="true">⭐</span>
      <div class="header-content">
        <h1>Mimi 启蒙乐园</h1>
        <MimiMascot variant="welcome" size="md" />
      </div>
      <button
        type="button"
        class="parent-lock"
        aria-label="家长中心"
        title="家长中心"
        @click="router.push('/parent-center')"
      >🔒</button>
    </header>

    <TodayTask />

    <nav class="tile-grid" aria-label="学习入口">
      <RouterLink v-for="tile in tiles" :key="tile.key" :to="tile.to" class="tile">
        <img :src="tile.image" alt="" class="tile-image" loading="lazy" decoding="async" />
        <span class="tile-label">{{ tile.label }}</span>
      </RouterLink>
    </nav>

    <p v-if="isLoading" class="state-tip" role="status">正在加载学科…</p>
    <div v-else-if="errorMsg" class="state-tip error" role="alert">
      <p>{{ errorMsg }}</p>
      <AppButton size="lg" @click="loadSubjects">重试</AppButton>
    </div>
  </div>
</template>

<style scoped>
.home {
  max-width: var(--content-max);
  margin: 0 auto;
  padding: var(--space-4);
  min-height: 100vh;
}

.welcome-header {
  position: relative;
  background: var(--gradient-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4) var(--space-5);
  padding-right: calc(var(--touch-target) + var(--space-5));
  margin-bottom: var(--space-4);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.header-content {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  position: relative;
  z-index: 1;
}

.header-content h1 {
  color: var(--text-on-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
}

/* 家长入口：右上角小锁 */
.parent-lock {
  position: absolute;
  top: var(--space-2);
  right: var(--space-2);
  z-index: 2;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: var(--touch-target);
  height: var(--touch-target);
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--brand-surface-soft);
  font-size: var(--text-base);
  cursor: pointer;
}

.parent-lock:focus-visible,
.tile:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px var(--focus-ring);
}

.decor {
  position: absolute;
  z-index: 0;
  font-size: 20px;
}

.decor.c1 { top: var(--space-2); right: 40%; }
.decor.c2 { bottom: var(--space-2); left: 45%; }

@media (prefers-reduced-motion: no-preference) {
  .decor { animation: float 5s ease-in-out infinite; }
  .tile { transition: transform var(--duration-fast) var(--ease-bounce), box-shadow var(--duration-fast) var(--ease-smooth); }
  .tile:hover { transform: translateY(-4px); box-shadow: var(--shadow-hover); }
  .tile:active { transform: scale(0.97); }
}

@keyframes float {
  50% { transform: translateY(-6px); }
}

/* 大图宫格：手机两列，桌面三到四列 */
.tile-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--space-3);
}

.tile {
  display: flex;
  flex-direction: column;
  min-height: 96px;
  overflow: hidden;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  color: var(--text-primary);
  text-decoration: none;
}

.tile-image {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  background: var(--bg-muted);
}

.tile-label {
  padding: var(--space-2) var(--space-3);
  text-align: center;
  font-size: var(--text-base);
  font-weight: var(--font-bold);
}

@media (min-width: 720px) {
  .tile-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); gap: var(--space-4); }
}

@media (min-width: 1024px) {
  .tile-grid { grid-template-columns: repeat(4, minmax(0, 1fr)); }
}

.state-tip {
  text-align: center;
  padding: var(--space-6);
  color: var(--text-tertiary);
}

.state-tip.error { color: var(--color-warning); }
.state-tip.error p { margin-bottom: var(--space-3); }
</style>
