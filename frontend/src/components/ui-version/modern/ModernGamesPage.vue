<!--
  ModernGamesPage.vue - 新版游戏页
  用途: 强调先学习再解锁；游戏列表常显（锁定时灰显），孩子始终能看到有哪些游戏。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { computed } from 'vue'
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

// 是否已解锁：未加载完成或出错时按未解锁展示，避免点了没反应
const isUnlocked = computed(() => accessState.access.value?.unlocked === true)

function openGame(route) {
  if (!isUnlocked.value) return
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
      <p class="modern-brand-kicker">奖励小屋</p>
      <h1 class="modern-brand-title">认真学习，就能打开小游戏</h1>
      <p class="modern-brand-desc">先去学一会儿英语，学完游戏就会一个个亮起来。</p>
    </section>

    <!-- 门禁只负责状态提示：锁定时给进度与"去学习"按钮，解锁后给开玩横幅 -->
    <GameAccessGate
      :access="accessState.access.value"
      :is-loading="accessState.isLoading.value"
      :error-msg="accessState.errorMsg.value"
      :progress-percent="accessState.progressPercent.value"
      @retry="accessState.refreshAccess"
      @learn="router.push('/')"
    >
      <section class="modern-brand-card modern-brand-hero--split games-reward">
        <div class="modern-brand-hero-copy">
          <p class="modern-brand-kicker">已解锁</p>
          <h2 class="modern-brand-title">游戏全部打开啦，选一个开玩</h2>
          <p class="modern-brand-desc">先从下面三个开始，都很好玩。</p>
        </div>
        <div class="modern-brand-hero-side">
          <AppButton size="lg" variant="success" @click="openGame(recommendedGames[0].route)">马上开玩</AppButton>
        </div>
      </section>
    </GameAccessGate>

    <!-- 游戏列表放在门禁外常显：锁定时灰显禁用，孩子仍能看见有什么可玩 -->
    <section
      class="modern-brand-grid modern-brand-grid--three games-grid"
      :class="{ 'games-grid--locked': !isUnlocked }"
      aria-label="推荐游戏"
    >
      <button
        v-for="game in recommendedGames"
        :key="game.route"
        type="button"
        class="modern-brand-card modern-brand-media-card"
        :disabled="!isUnlocked"
        :aria-label="isUnlocked ? `${game.name}，进入游戏` : `${game.name}，还没解锁`"
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
        <strong class="modern-brand-tile-cta">
          {{ isUnlocked ? '进入游戏 →' : '🔒 学完今天的课就解锁' }}
        </strong>
      </button>
    </section>

    <section class="modern-brand-card modern-brand-state games-more" aria-label="更多小游戏">
      <h3 class="modern-brand-section-title">还有 {{ hiddenGamesCount }} 个游戏在排队</h3>
      <p class="modern-brand-note">每天多学一会儿，它们也会慢慢亮起来。</p>
    </section>
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

/* 锁定态：整组游戏灰显降饱和，禁用点击，保留"看得见"的期待感 */
.games-grid--locked .modern-brand-media-card {
  opacity: 0.55;
  filter: grayscale(0.85);
  cursor: not-allowed;
}
</style>
