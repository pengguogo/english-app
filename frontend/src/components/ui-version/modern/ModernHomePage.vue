<!--
  ModernHomePage.vue - 新版首页
  用途: 复用 modernHomeData 入口配置，统一到 modern-brand 品牌视觉体系。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { useRouter } from 'vue-router'
import AppButton from '../../AppButton.vue'
import { featuredRoutes, supportRoutes } from './modernHomeData'

const router = useRouter()

function openRoute(route) {
  router.push(route)
}
</script>

<template>
  <!-- 首页为应用根路由，没有返回目标：仅用顶部留白为右上角 UI 切换开关让位 -->
  <main class="modern-home modern-page-shell modern-page-shell--top-spaced">
    <!-- 今日主任务卡：一步主动作指向英语学科乐园 -->
    <section class="modern-brand-card modern-brand-hero modern-brand-hero--split home-hero">
      <div class="modern-brand-hero-copy">
        <p class="modern-brand-kicker">今天先做什么</p>
        <h1 class="modern-brand-title">先去英语学科乐园，完成今天的第一张小车票</h1>
        <p class="modern-brand-desc">
          先学一点，再去绘本、游戏和交通工具馆继续探索。今天的路线已经帮你排好了。
        </p>
        <div class="modern-brand-actions">
          <AppButton size="lg" @click="openRoute('/subject/1')">进入学科乐园</AppButton>
          <button type="button" class="modern-brand-ghost-button" @click="openRoute('/learned')">查看学习记录</button>
        </div>
      </div>

      <div class="modern-brand-hero-side">
        <div class="modern-brand-sticker" aria-hidden="true">
          <span>🎫</span>
          <strong>出发喽</strong>
        </div>
        <p class="modern-brand-caption">跟着路线走，不怕迷路。</p>
      </div>
    </section>

    <!-- 主入口：四个磁贴，各自注入入口强调色 -->
    <section class="home-section" aria-labelledby="home-featured-title">
      <div class="modern-brand-section-head">
        <div>
          <p class="modern-brand-kicker">主入口</p>
          <h2 id="home-featured-title" class="modern-brand-section-title">今天的四个主要入口</h2>
        </div>
        <p class="modern-brand-section-note">学一点、读一点、玩一点、看一点，刚刚好。</p>
      </div>

      <div class="modern-brand-grid modern-brand-grid--two">
        <button
          v-for="entry in featuredRoutes"
          :key="entry.route"
          type="button"
          class="modern-brand-card modern-brand-tile"
          :style="{ '--brand-accent': entry.accent }"
          @click="openRoute(entry.route)"
        >
          <span class="modern-brand-tile-icon">{{ entry.icon }}</span>
          <p class="modern-brand-kicker">{{ entry.kicker }}</p>
          <h3>{{ entry.title }}</h3>
          <p class="modern-brand-note">{{ entry.description }}</p>
          <strong class="modern-brand-tile-cta">现在出发</strong>
        </button>
      </div>
    </section>

    <!-- 次级入口：学习补给站，视觉弱化保持一步主动作 -->
    <section class="home-section" aria-labelledby="home-support-title">
      <div class="modern-brand-section-head">
        <div>
          <p class="modern-brand-kicker">学习补给站</p>
          <h2 id="home-support-title" class="modern-brand-section-title">两张随手可用的小卡片</h2>
        </div>
      </div>

      <div class="modern-brand-grid modern-brand-grid--two">
        <button
          v-for="entry in supportRoutes"
          :key="entry.route"
          type="button"
          class="modern-brand-card modern-brand-mini-card"
          :style="{ '--brand-accent': entry.accent }"
          @click="openRoute(entry.route)"
        >
          <span class="modern-brand-mini-dot"></span>
          <div>
            <h3>{{ entry.title }}</h3>
            <p class="modern-brand-note">{{ entry.description }}</p>
          </div>
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
@import './modern-brand.css';

/* 首页只保留少量间距规则，视觉全部收敛到 modern-brand 品牌类 */
.modern-home {
  min-height: 100vh;
}

.home-hero {
  margin-bottom: var(--space-6);
}

.home-section {
  padding-bottom: var(--space-6);
}
</style>
