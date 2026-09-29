<!--
  ModernGamesPage.vue - 新版游戏页
  用途: 强调先学习再解锁，只重点展示 3 个推荐游戏并提示更多内容。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { useRouter } from 'vue-router'
import BackBar from '../../BackBar.vue'
import GameAccessGate from '../../games/GameAccessGate.vue'
import AppButton from '../../AppButton.vue'
import { useGameAccess } from '../../../composables/useGameAccess'

const router = useRouter()
const accessState = useGameAccess()

const recommendedGames = [
  { name: '童话拼图', route: '/games/picture-puzzle', image: `${import.meta.env.BASE_URL}images/games/woodland-puzzle.png`, desc: '先看完整图，再试着把森林里的小伙伴拼回原位。' },
  { name: '几何转转拼图', route: '/games/shape-puzzle', image: `${import.meta.env.BASE_URL}vendor/scrollzz/images/demo.png`, desc: '滑动图块，找回正确顺序，观察图案慢慢完整。' },
  { name: '动物翻翻乐', route: '/games/memory-match', icon: '🐱', desc: '翻开卡片，记住位置，看看你能不能一口气配对成功。' }
]

const hiddenGamesCount = 6

function openGame(route) {
  router.push(route)
}
</script>

<template>
  <!-- 游戏页固定用成功色作为品牌强调色，呼应"解锁奖励"主题 -->
  <main
    class="modern-games modern-page-shell modern-page-shell--top-spaced"
    :style="{ '--brand-accent': 'var(--color-success)' }"
  >
    <BackBar title="游戏专区" @back="router.push('/')" />

    <section class="modern-brand-card modern-brand-hero games-hero">
      <p class="modern-brand-kicker">奖励解锁</p>
      <h1 class="modern-brand-title">先学习，再把小游戏一个个点亮</h1>
      <p class="modern-brand-desc">今天认真学习 5 分钟，就能解锁推荐游戏。先完成主线，奖励会自己打开。</p>
    </section>

    <GameAccessGate
      :access="accessState.access.value"
      :is-loading="accessState.isLoading.value"
      :error-msg="accessState.errorMsg.value"
      :progress-percent="accessState.progressPercent.value"
      @retry="accessState.refreshAccess"
      @learn="router.push('/')"
    >
      <!-- 已解锁后的主动作横幅：左文案右按钮，一步开玩 -->
      <section class="modern-brand-card modern-brand-hero--split games-reward">
        <div class="modern-brand-hero-copy">
          <p class="modern-brand-kicker">已解锁</p>
          <h2 class="modern-brand-title">今天的推荐游戏已经准备好啦</h2>
          <p class="modern-brand-desc">先从三款精选开始，剩下的小游戏会在后面继续等你。</p>
        </div>
        <div class="modern-brand-hero-side">
          <AppButton size="lg" variant="success" @click="openGame(recommendedGames[0].route)">马上开玩</AppButton>
        </div>
      </section>

      <section class="modern-brand-grid modern-brand-grid--three games-grid" aria-label="推荐游戏">
        <button
          v-for="game in recommendedGames"
          :key="game.route"
          type="button"
          class="modern-brand-card modern-brand-media-card"
          @click="openGame(game.route)"
        >
          <img
            v-if="game.image"
            :src="game.image"
            :alt="game.name"
            class="modern-brand-media-cover"
            loading="lazy"
          />
          <span v-else class="modern-brand-media-icon" aria-hidden="true">{{ game.icon }}</span>
          <h3>{{ game.name }}</h3>
          <p class="modern-brand-note">{{ game.desc }}</p>
          <strong class="modern-brand-tile-cta">进入游戏 →</strong>
        </button>
      </section>

      <section class="modern-brand-card modern-brand-state games-more" aria-label="更多小游戏">
        <h3 class="modern-brand-section-title">后面还有 {{ hiddenGamesCount }} 个小游戏</h3>
        <p class="modern-brand-note">新版先把推荐内容收得更清楚，后续可以继续往下拓展。</p>
      </section>
    </GameAccessGate>
  </main>
</template>

<style scoped>
@import './modern-brand.css';

/* 游戏页只保留间距，卡片/网格/封面视觉全部来自品牌类 */
.modern-games {
  min-height: 100vh;
}

.games-hero,
.games-reward,
.games-grid {
  margin-bottom: var(--space-5);
}
</style>
