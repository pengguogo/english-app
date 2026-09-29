<!--
  ModernSubjectPage.vue - 新版学科页
  用途: 复用真实主题数据与 /theme/:id 路由，统一到 modern-brand 品牌视觉体系。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getThemesBySubject } from '../../../api/subject'
import { getThemeConfig } from '../../../config/themeConfig'
import { useSafeBack } from '../../../composables/useSafeBack'
import AppButton from '../../AppButton.vue'
import BackBar from '../../BackBar.vue'
import { getSubjectColor, getSubjectLabel } from './subjectViewShared'

const router = useRouter()
const route = useRoute()
const { safeBack } = useSafeBack()
const themes = ref([])
const isLoading = ref(true)
const errorMsg = ref('')
const subjectId = Number(route.params.subjectId)
const subjectName = getSubjectLabel(subjectId)
const subjectColor = getSubjectColor(subjectId)

const featuredTheme = computed(() => themes.value[0] || null)
const secondaryThemes = computed(() => themes.value.slice(1))

onMounted(loadThemes)

async function loadThemes() {
  isLoading.value = true
  errorMsg.value = ''

  try {
    themes.value = await getThemesBySubject(subjectId)
  } catch (error) {
    errorMsg.value = '主题加载失败，请稍后重试。'
    console.error('加载主题失败:', error)
  } finally {
    isLoading.value = false
  }
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

function getThemeEmoji(theme) {
  return getThemeConfig(theme.id, theme.name).emoji
}

function goBack() {
  safeBack('/')
}
</script>

<template>
  <!-- 学科强调色统一注入 --brand-accent，供品牌类局部取色 -->
  <main
    class="modern-subject modern-page-shell modern-page-shell--top-spaced"
    :style="{ '--brand-accent': subjectColor }"
  >
    <BackBar :title="`${subjectName}学科乐园`" @back="goBack" />

    <section class="modern-brand-card modern-brand-hero subject-hero">
      <p class="modern-brand-kicker">学科乐园</p>
      <h1 class="modern-brand-title">{{ subjectName }}乐园</h1>
      <p class="modern-brand-desc">先点最上面的大卡片，学完再看下面的。</p>
      <div v-if="!isLoading && !errorMsg" class="modern-brand-badges">
        <span class="modern-brand-badge modern-brand-badge--solid">{{ themes.length }} 个主题</span>
        <span class="modern-brand-badge">{{ subjectName }}学科</span>
      </div>
    </section>

    <section v-if="isLoading" class="modern-brand-card modern-brand-state" role="status">正在整理主题地图…</section>
    <section v-else-if="errorMsg" class="modern-brand-card modern-brand-state" role="alert">
      <p>{{ errorMsg }}</p>
      <AppButton variant="ghost" @click="loadThemes">重新加载</AppButton>
    </section>
    <section v-else-if="themes.length === 0" class="modern-brand-card modern-brand-state">这个学科暂时还没有主题。</section>

    <template v-else>
      <!-- 优先推荐主题：整页唯一主动作，右侧贴纸只做装饰不参与点击 -->
      <section class="modern-brand-card modern-brand-hero--split subject-featured">
        <div class="modern-brand-hero-copy">
          <p class="modern-brand-kicker">从这里开始</p>
          <h2 class="modern-brand-title">{{ featuredTheme.name }}</h2>
          <p class="modern-brand-desc">从这个主题开始学，一站一站往前走。</p>
          <div class="modern-brand-actions">
            <AppButton size="lg" @click="openTheme(featuredTheme)">开始学习</AppButton>
          </div>
        </div>

        <div class="modern-brand-hero-side">
          <div class="modern-brand-sticker subject-sticker" aria-hidden="true">
            <span>{{ getThemeEmoji(featuredTheme) }}</span>
          </div>
        </div>
      </section>

      <section class="subject-section" aria-labelledby="subject-secondary-title">
        <div class="modern-brand-section-head">
          <div>
            <p class="modern-brand-kicker">继续探索</p>
            <h2 id="subject-secondary-title" class="modern-brand-section-title">其他主题</h2>
          </div>
          <p class="modern-brand-section-note">点一个喜欢的，进去看看。</p>
        </div>

        <div class="modern-brand-grid">
          <button
            v-for="theme in secondaryThemes"
            :key="theme.id"
            type="button"
            class="modern-brand-card modern-brand-row-card"
            @click="openTheme(theme)"
          >
            <span class="modern-brand-media-icon subject-theme-icon">{{ getThemeEmoji(theme) }}</span>
            <div>
              <h3>{{ theme.name }}</h3>
              <p class="modern-brand-note">点这里进去 →</p>
            </div>
          </button>
        </div>
      </section>
    </template>
  </main>
</template>

<style scoped>
@import './modern-brand.css';

/* 学科页仅保留间距与个别尺寸微调，视觉全部来自品牌类 */
.modern-subject {
  min-height: 100vh;
}

.subject-hero {
  margin-bottom: var(--space-5);
}

.subject-featured {
  margin-bottom: var(--space-6);
}

.subject-section {
  padding-bottom: var(--space-8);
}

/* 推荐主题贴纸已改为纯装饰：保持尺寸撑起 hero 右侧视觉，不再有按钮态 */
.subject-sticker {
  min-width: 120px;
  min-height: 120px;
}

/* 行卡图标略缩小，与列表密度更协调 */
.subject-theme-icon {
  width: 64px;
  height: 64px;
  font-size: 34px;
}

@media (prefers-reduced-motion: no-preference) {
  /* 装饰贴纸保留轻微呼吸感，呼应儿童界面的活力但不参与交互 */
  .subject-sticker {
    animation: subject-sticker-float 3s var(--ease-bounce) infinite alternate;
  }
}

@keyframes subject-sticker-float {
  from { transform: translateY(-4px); }
  to { transform: translateY(4px); }
}
</style>
