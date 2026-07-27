<!--
  ThemeView.vue - 单元列表页:展示某主题下的所有单元及学习进度
  用途: 场景 banner + 单元卡片列表。
  修改: 2026-07-25 直接移除学习地图，保留更直接的单元列表入口。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUnitsByTheme } from '../api/unit'
import { getThemeConfig } from '../config/themeConfig'
import BackBar from '../components/BackBar.vue'
// 汪汪队角色图片(仅主题 3 使用)
import ryderImg from '../assets/paw-patrol/ryder.jpg'
import chaseImg from '../assets/paw-patrol/chase.jpg'
import marshallImg from '../assets/paw-patrol/marshall.jpg'
import skyeImg from '../assets/paw-patrol/skye.jpg'
import rubbleImg from '../assets/paw-patrol/rubble.jpg'
import zumaImg from '../assets/paw-patrol/zuma.jpg'
import rockyImg from '../assets/paw-patrol/rocky.jpg'

// 汪汪队角色列表:图片 + 名字 + 职责描述
const pawPatrolCharacters = [
  { img: ryderImg, name: 'Ryder', cn: '莱德', role: '队长' },
  { img: chaseImg, name: 'Chase', cn: '阿奇', role: '警察狗' },
  { img: marshallImg, name: 'Marshall', cn: '毛毛', role: '消防狗' },
  { img: skyeImg, name: 'Skye', cn: '天天', role: '飞行狗' },
  { img: rubbleImg, name: 'Rubble', cn: '小砾', role: '工程狗' },
  { img: zumaImg, name: 'Zuma', cn: '路马', role: '水上救生狗' },
  { img: rockyImg, name: 'Rocky', cn: '灰灰', role: '回收狗' }
]

const route = useRoute()
const router = useRouter()
const units = ref([])
const isLoading = ref(true)
const errorMsg = ref('')
const themeId = computed(() => Number(route.params.themeId))
const subjectId = computed(() => Number(route.query.subjectId || 0))
const subjectName = computed(() => String(route.query.subjectName || ''))
const themeName = computed(() => String(route.query.themeName || ''))

// 当前主题视觉配置:按路由 themeId 动态读取,避免硬编码某一主题内容
const themeVisual = computed(() => getThemeConfig(themeId.value, themeName.value))

onMounted(async () => {
  const themeId = route.params.themeId
  try {
    units.value = await getUnitsByTheme(themeId)
  } catch (e) {
    errorMsg.value = '加载失败,请返回重试'
    console.error('加载单元失败:', e)
  } finally {
    isLoading.value = false
  }
})

/**
 * 根据单元索引返回对应的场景配置(emoji + 主色调)。
 * 场景来自当前主题配置,不同主题(水果/交通工具)展示不同图标,
 * 超出配置数量时回退到第一个场景,保证渲染安全。
 * @param {number} index 单元在列表中的索引
 * @return {Object} 场景配置 { icon, color, label }
 */
function getSceneConfig(index) {
  const scenes = themeVisual.value.scenes
  return scenes[index % scenes.length] || scenes[0]
}

function goBack() {
  if (subjectId.value) {
    router.push(`/subject/${subjectId.value}`)
    return
  }
  router.push('/')
}

function openUnit(unit) {
  const query = {
    themeId: String(themeId.value),
    themeName: themeVisual.value.title
  }
  if (subjectId.value) query.subjectId = String(subjectId.value)
  if (subjectName.value) query.subjectName = subjectName.value
  router.push({
    path: `/unit/${unit.id}`,
    query
  })
}
</script>

<template>
  <div class="theme-view">
    <BackBar :title="themeVisual.title" @back="goBack" />

    <!-- 场景 banner -->
    <div class="scene-banner">
      <div class="banner-content">
        <h1>{{ themeVisual.emoji }} {{ themeVisual.title }}</h1>
        <p>{{ themeVisual.description }}</p>
      </div>
    </div>

    <!-- 汪汪队角色介绍(仅主题 3 显示) -->
    <section v-if="Number(route.params.themeId) === 3" class="characters-section">
      <h2 class="section-title">🐶 汪汪队成员</h2>
      <div class="characters-grid">
        <div
          v-for="char in pawPatrolCharacters"
          :key="char.name"
          class="character-card"
        >
          <img :src="char.img" :alt="char.cn" class="character-img" />
          <div class="character-info">
            <span class="character-name">{{ char.name }}</span>
            <span class="character-cn">{{ char.cn }}</span>
            <span class="character-role">{{ char.role }}</span>
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

    <template v-else>
      <section class="unit-list-section">
        <h2 class="section-title">选择单元</h2>
        <div class="unit-list">
          <div
            v-for="(unit, index) in units"
            :key="unit.id"
            class="unit-card"
            @click="openUnit(unit)"
          >
            <!-- 左侧彩色竖条 -->
            <div class="unit-stripe" :style="{ background: getSceneConfig(index).color }"></div>
            <div class="unit-body">
              <div class="unit-header">
                <div class="unit-icon">{{ getSceneConfig(index).icon }}</div>
                <div class="unit-info">
                  <h3>{{ unit.name }}</h3>
                  <p class="unit-progress-text">
                    已完成 {{ unit.completedLessons }} / {{ unit.totalLessons }} 课
                  </p>
                </div>
                <div class="unit-status">
                  <span v-if="unit.completedLessons === unit.totalLessons" class="done-icon">✓</span>
                </div>
              </div>
              <!-- 进度条 -->
              <div class="progress-bar">
                <div
                  class="progress-fill"
                  :style="{
                    width: (unit.totalLessons ? (unit.completedLessons / unit.totalLessons * 100) : 0) + '%',
                    background: getSceneConfig(index).color
                  }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<style scoped>
/* 页面容器 */
.theme-view {
  padding: var(--space-4);
  min-height: 100vh;
}

/* ===== 场景 banner ===== */
.scene-banner {
  background: var(--gradient-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
  position: relative;
  overflow: hidden;
}

.banner-content h1 {
  color: white;
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
}

.banner-content p {
  color: rgba(255, 255, 255, 0.9);
  font-size: var(--text-base);
}

/* ===== 主题地图 ===== */
/* ===== 汪汪队角色介绍 ===== */
.characters-section {
  margin-bottom: var(--space-6);
}

.characters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: var(--space-3);
}

.character-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  text-align: center;
  transition: transform var(--duration-fast) var(--ease-bounce);
}

.character-card:hover {
  transform: translateY(-4px) scale(1.03);
}

.character-img {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  display: block;
}

.character-info {
  padding: var(--space-2);
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.character-name {
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.character-cn {
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.character-role {
  font-size: 10px;
  color: var(--text-tertiary);
}

.section-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

/* ===== 单元列表 ===== */
.unit-list-section {
  margin-bottom: var(--space-6);
}

.unit-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.unit-card {
  display: flex;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: transform var(--duration-fast) var(--ease-bounce),
              box-shadow var(--duration-fast) var(--ease-smooth);
}

.unit-card:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-hover);
}

/* 左侧彩色竖条 */
.unit-stripe {
  width: 6px;
  flex-shrink: 0;
}

.unit-body {
  flex: 1;
  padding: var(--space-4);
}

.unit-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-3);
}

.unit-icon {
  font-size: 2rem;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-muted);
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

.unit-info {
  flex: 1;
}

.unit-info h3 {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.unit-progress-text {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.unit-status {
  font-size: 1.5rem;
}

.done-icon {
  color: var(--color-success);
  font-weight: var(--font-bold);
}

/* 进度条 */
.progress-bar {
  height: 8px;
  background: var(--bg-muted);
  border-radius: var(--radius-pill);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: var(--radius-pill);
  transition: width var(--duration-slow) var(--ease-smooth);
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
