<!--
  ThemeView.vue - 主题学习页:按单元分组展示课时与学习进度
  用途: 场景 banner + 单元分组课时列表。
  修改: 2026-07-25 直接移除学习地图，保留更直接的单元列表入口。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUnitsByTheme } from '../api/unit'
import { getLessonsByUnit } from '../api/lesson'
import { getUnitProgress } from '../api/progress'
import { getThemeConfig } from '../config/themeConfig'
import { getUnitImage } from '../config/unitImages'
import { useSafeBack } from '../composables/useSafeBack'
import BackBar from '../components/BackBar.vue'
import StarBar from '../components/StarBar.vue'
import AppButton from '../components/AppButton.vue'
// 汪汪队角色图片(仅主题 3 使用)
import pawPatrolHero from '../assets/paw-patrol/rescue-team-hero.jpg'
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
const { safeBack } = useSafeBack()
const units = ref([])
const isLoading = ref(true)
const errorMsg = ref('')
const themeId = computed(() => Number(route.params.themeId))
const subjectId = computed(() => Number(route.query.subjectId || 0))
const subjectName = computed(() => String(route.query.subjectName || ''))
const themeName = computed(() => String(route.query.themeName || ''))

// 当前主题视觉配置:按路由 themeId 动态读取,避免硬编码某一主题内容
const themeVisual = computed(() => getThemeConfig(themeId.value, themeName.value))

onMounted(loadUnits)

async function loadUnits() {
  const themeId = route.params.themeId
  isLoading.value = true
  errorMsg.value = ''
  try {
    const unitList = await getUnitsByTheme(themeId)
    units.value = await Promise.all(unitList.map(async (unit) => {
      const [lessons, progressList] = await Promise.all([
        getLessonsByUnit(unit.id),
        getUnitProgress(unit.id).catch(() => [])
      ])
      const progressMap = new Map(progressList.map(progress => [progress.lessonId, progress]))
      return {
        ...unit,
        lessons: lessons.map(lesson => ({
          ...lesson,
          progress: progressMap.get(lesson.id)
        }))
      }
    }))
  } catch (e) {
    errorMsg.value = '加载失败,请返回重试'
    console.error('加载单元失败:', e)
  } finally {
    isLoading.value = false
  }
}

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
  const fallback = subjectId.value ? `/subject/${subjectId.value}` : '/'
  safeBack(fallback)
}

function openLesson(unit, lesson) {
  const query = {
    unitId: String(unit.id),
    themeId: String(themeId.value),
    themeName: themeVisual.value.title
  }
  if (subjectId.value) query.subjectId = String(subjectId.value)
  if (subjectName.value) query.subjectName = subjectName.value
  router.push({
    path: `/lesson/${lesson.id}`,
    query
  })
}

function getTypeText(type) {
  const labels = {
    WORD: '认知',
    SENTENCE: '句型',
    READING: '阅读',
    QUIZ: '问答',
    CALCULATE: '计算',
    PHONICS: '拼读',
    DIALOGUE: '对话'
  }
  return labels[type] || '课程'
}
</script>

<template>
  <div class="theme-view">
    <BackBar :title="themeVisual.title" @back="goBack" />

    <!-- 场景 banner -->
    <div
      class="scene-banner"
      :class="{ 'paw-patrol-banner': themeId === 3 }"
    >
      <img
        v-if="themeId === 3"
        :src="pawPatrolHero"
        alt=""
        class="banner-art"
      />
      <div class="banner-content">
        <span v-if="themeId === 3" class="banner-kicker">RESCUE ENGLISH</span>
        <h1>{{ themeVisual.emoji }} {{ themeVisual.title }}</h1>
        <p>{{ themeVisual.description }}</p>
        <span v-if="themeId === 3" class="banner-slogan">Learn · Play · Grow!</span>
      </div>
    </div>

    <!-- 汪汪队角色介绍(仅主题 3 显示) -->
    <section v-if="Number(route.params.themeId) === 3" class="characters-section">
      <div class="section-heading">
        <div>
          <span class="section-eyebrow">MEET THE TEAM</span>
          <h2 class="section-title">🐾 汪汪队成员</h2>
        </div>
        <span class="section-hint">认识伙伴，勇敢开口</span>
      </div>
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
    <div v-if="isLoading" class="state-tip" role="status" aria-live="polite">
      <div class="loading-dot"></div>
      <p>加载中...</p>
    </div>

    <!-- 加载失败 -->
    <div v-else-if="errorMsg" class="state-tip error" role="alert">
      <p>{{ errorMsg }}</p>
      <AppButton variant="ghost" @click="loadUnits">重新加载</AppButton>
    </div>

    <div v-else-if="units.length === 0" class="state-tip">
      <p>这个主题还没有学习内容</p>
      <AppButton variant="ghost" @click="goBack">选择其他主题</AppButton>
    </div>

    <template v-else>
      <section class="unit-list-section">
        <h2 class="section-title">开始学习</h2>
        <div class="unit-list">
          <section
            v-for="(unit, index) in units"
            :key="unit.id"
            class="unit-card"
          >
            <!-- 左侧彩色竖条 -->
            <div class="unit-stripe" :style="{ background: getSceneConfig(index).color }"></div>
            <div class="unit-body">
              <div class="unit-header">
                <img
                  v-if="getUnitImage(unit.id)"
                  :src="getUnitImage(unit.id)"
                  :alt="unit.name"
                  class="unit-image"
                />
                <div v-else class="unit-icon">{{ getSceneConfig(index).icon }}</div>
                <div class="unit-info">
                  <h3>{{ unit.name }}</h3>
                  <p class="unit-progress-text">
                    已完成 {{ unit.completedLessons }} / {{ unit.totalLessons }} 课
                  </p>
                </div>
                <span v-if="unit.completedLessons === unit.totalLessons" class="done-icon">✓</span>
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
              <div class="lesson-list">
                <button
                  v-for="(lesson, lessonIndex) in unit.lessons"
                  :key="lesson.id"
                  class="lesson-card"
                  @click="openLesson(unit, lesson)"
                >
                  <span class="lesson-order">{{ lessonIndex + 1 }}</span>
                  <span class="lesson-info">
                    <strong>{{ lesson.name }}</strong>
                    <small>{{ getTypeText(lesson.type) }}</small>
                  </span>
                  <StarBar
                    v-if="lesson.progress?.status === 'COMPLETED'"
                    :stars="lesson.progress.stars || 0"
                    size="sm"
                  />
                  <span v-else class="lesson-action">
                    {{ lesson.progress?.status === 'IN_PROGRESS' ? '继续' : '开始' }} →
                  </span>
                </button>
              </div>
            </div>
          </section>
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

.banner-art {
  display: none;
}

.banner-content h1 {
  color: var(--text-on-primary);
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
}

.banner-content p {
  color: var(--text-on-primary-muted);
  font-size: var(--text-base);
}

.paw-patrol-banner {
  min-height: 300px;
  display: flex;
  align-items: center;
  padding: clamp(var(--space-5), 5vw, 56px);
  background: var(--paw-banner-fallback);
  isolation: isolate;
}

.paw-patrol-banner::after {
  content: '';
  position: absolute;
  inset: 0;
  z-index: -1;
  background: var(--paw-banner-overlay);
}

.paw-patrol-banner .banner-art {
  display: block;
  position: absolute;
  inset: 0;
  z-index: -2;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.paw-patrol-banner .banner-content {
  width: min(46%, 440px);
  text-shadow: var(--shadow-text);
}

.paw-patrol-banner .banner-content h1 {
  font-size: clamp(var(--text-xl), 4vw, 46px);
  line-height: 1.12;
}

.banner-kicker,
.section-eyebrow {
  color: var(--color-accent);
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  letter-spacing: 0.14em;
}

.banner-slogan {
  display: inline-flex;
  margin-top: var(--space-4);
  padding: var(--space-2) var(--space-4);
  color: var(--text-on-primary);
  background: var(--paw-badge-bg);
  border: 1px solid var(--paw-badge-border);
  border-radius: var(--radius-pill);
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  box-shadow: var(--shadow-soft);
}

/* ===== 主题地图 ===== */
/* ===== 汪汪队角色介绍 ===== */
.characters-section {
  margin-bottom: var(--space-6);
}

.section-heading {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
}

.section-heading .section-title {
  margin: var(--space-1) 0 0;
}

.section-hint {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

.characters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(118px, 1fr));
  gap: var(--space-3);
}

.character-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  text-align: center;
  border: 1px solid var(--border-light);
  transition: transform var(--duration-fast) var(--ease-bounce);
}

.character-card:hover {
  transform: translateY(-4px) scale(1.03);
}

.character-img {
  width: 100%;
  aspect-ratio: 4 / 5;
  object-fit: cover;
  object-position: center 22%;
  display: block;
}

.character-info {
  position: relative;
  padding: var(--space-3) var(--space-2);
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.character-info::before {
  content: '';
  position: absolute;
  top: 0;
  left: var(--space-3);
  right: var(--space-3);
  height: 3px;
  border-radius: var(--radius-pill);
  background: var(--color-primary);
}

.character-card:nth-child(3n + 2) .character-info::before {
  background: var(--color-warning);
}

.character-card:nth-child(3n) .character-info::before {
  background: var(--color-accent);
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

.unit-image {
  width: 64px;
  height: 64px;
  flex-shrink: 0;
  object-fit: cover;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-soft);
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

.lesson-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  margin-top: var(--space-4);
}

.lesson-card {
  width: 100%;
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border-radius: var(--radius-md);
  background: var(--bg-muted);
  color: var(--text-primary);
  text-align: left;
  transition: transform var(--duration-fast) var(--ease-bounce),
              background var(--duration-fast) var(--ease-smooth);
}

.lesson-card:hover {
  transform: translateX(4px);
  background: var(--border-muted);
}

.lesson-order {
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  flex-shrink: 0;
  border-radius: var(--radius-pill);
  background: var(--color-primary);
  color: var(--text-on-primary);
  font-weight: var(--font-bold);
}

.lesson-info {
  min-width: 0;
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: var(--space-1);
}

.lesson-info strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.lesson-info small {
  color: var(--text-tertiary);
}

.lesson-action {
  flex-shrink: 0;
  color: var(--color-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
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

.state-tip p + .app-btn {
  margin-top: var(--space-3);
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

@media (max-width: 640px) {
  .paw-patrol-banner {
    min-height: 360px;
    align-items: flex-start;
  }

  .paw-patrol-banner .banner-art {
    object-position: 60% center;
  }

  .paw-patrol-banner::after {
    background: var(--paw-banner-overlay-mobile);
  }

  .paw-patrol-banner .banner-content {
    width: 78%;
  }

  .section-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: var(--space-1);
  }

  .characters-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

</style>
