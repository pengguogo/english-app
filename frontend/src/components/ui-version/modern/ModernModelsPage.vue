<!--
  ModernModelsPage.vue - 新版模型页
  用途: 将 21 个模型入口收束为 3 个大类，并用代表模型驱动真实 ModelViewer。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../../BackBar.vue'
import AppButton from '../../AppButton.vue'
import ModelViewer from '../../models/ModelViewer.vue'
import { modelCategories, modelHeadline } from './modelsModernData'

const router = useRouter()
const activeCategoryId = ref(modelCategories[0].id)

const currentCategory = computed(
  () => modelCategories.find((item) => item.id === activeCategoryId.value) || modelCategories[0]
)
const currentModel = computed(() => currentCategory.value.model)
const currentHeadline = computed(() => modelHeadline[currentModel.value])

function selectCategory(categoryId) {
  activeCategoryId.value = categoryId
}
</script>

<template>
  <!-- 页面级强调色跟随当前选中大类，hero 与提示随选择联动 -->
  <main
    class="modern-models modern-page-shell modern-page-shell--top-spaced"
    :style="{ '--brand-accent': currentCategory.accent }"
  >
    <BackBar title="交通工具馆" @back="router.push('/')" />

    <section class="modern-brand-card modern-brand-hero models-hero">
      <p class="modern-brand-kicker">{{ currentHeadline.eyebrow }}</p>
      <h1 class="modern-brand-title">
        {{ currentHeadline.title }} <span class="models-english">{{ currentHeadline.english }}</span>
      </h1>
      <p class="modern-brand-desc">{{ currentHeadline.description }}</p>
      <div class="modern-brand-actions">
        <AppButton size="lg" @click="selectCategory(currentCategory.id)">
          查看 {{ currentCategory.title }} 代表模型
        </AppButton>
      </div>
    </section>

    <section class="modern-brand-grid modern-brand-grid--three models-category" aria-label="模型大类">
      <!-- 每个大类磁贴注入自己的强调色，选中态用品牌描边突出 -->
      <button
        v-for="category in modelCategories"
        :key="category.id"
        type="button"
        class="modern-brand-card modern-brand-tile"
        :class="{ 'modern-brand-tile--active': activeCategoryId === category.id }"
        :style="{ '--brand-accent': category.accent }"
        @click="selectCategory(category.id)"
      >
        <span class="modern-brand-tile-icon">{{ category.icon }}</span>
        <p class="modern-brand-kicker">{{ category.subtitle }}</p>
        <h2>{{ category.title }}</h2>
        <p class="modern-brand-note">{{ category.description }}</p>
      </button>
    </section>

    <ModelViewer :key="currentModel" :model="currentModel" />

    <section class="modern-brand-card modern-brand-panel models-note">
      <h3 class="modern-brand-section-title">这个大类还包括</h3>
      <p class="modern-brand-note">{{ currentCategory.examples.join(' · ') }}</p>
    </section>
  </main>
</template>

<style scoped>
@import './modern-brand.css';

/* 模型页只保留间距与英文副标题样式，视觉全部来自品牌类 */
.modern-models {
  min-height: 100vh;
}

.models-hero,
.models-category {
  margin-bottom: var(--space-5);
}

/* 英文副标题弱化为次级说明文字，不与中文主标题抢视觉 */
.models-english {
  display: inline-block;
  margin-left: var(--space-2);
  color: var(--text-secondary);
  font-size: var(--text-base);
  font-weight: var(--font-normal);
}

.models-note {
  margin-top: var(--space-5);
}
</style>
