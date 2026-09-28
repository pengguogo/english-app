<script setup>
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import GameAccessGate from '../components/games/GameAccessGate.vue'
import { useGameAccess } from '../composables/useGameAccess'

const router = useRouter()
const accessState = useGameAccess()
const games = [
  { name: '童话拼图', route: '/games/picture-puzzle', image: `${import.meta.env.BASE_URL}images/games/woodland-puzzle.png`, desc: '手机精选 · 精美森林插画，4 / 9 / 16 块拼图' },
  { name: '几何转转拼图', route: '/games/shape-puzzle', image: `${import.meta.env.BASE_URL}vendor/scrollzz/images/demo.png`, desc: '手机精选 · 滑动图块，拼出星星和几何图案' },
  { name: '动物翻翻乐', route: '/games/memory-match', icon: '🐱', desc: '适合 6 岁 · 翻开卡片，找到相同动物' },
  { name: '图案接龙', route: '/games/pattern-play', icon: '🧩', desc: '适合 6 岁 · 找到规律，选出下一个图案' },
  { name: '亲子井字棋', route: '/games/tic-tac-toe', icon: '⭕', desc: '亲子双人 · 轮流点击，三个连成一线' },
  { name: '五子棋', route: '/games/gomoku', icon: '⚫', desc: '双人对弈，先连成五子获胜' },
  { name: '飞行棋', route: '/games/flight-chess', icon: '✈️', desc: '掷骰前进，先到终点获胜' },
  { name: '冒险棋', route: '/games/adventure-chess', icon: '🗺️', desc: '穿越丛林，寻找宝藏城堡' },
  { name: '九路围棋', route: '/games/go', icon: '⚪', desc: '围地提子，学习传统棋艺' }
]
</script>

<template>
  <main class="games-page">
    <BackBar title="游戏专区" @back="router.push('/')" />
    <section class="hero">
      <span>🎮</span>
      <div><h1>学习后的小奖励</h1><p>每天认真学习 5 分钟，即可解锁当天的全部游戏。</p></div>
    </section>
    <GameAccessGate
      :access="accessState.access.value" :is-loading="accessState.isLoading.value"
      :error-msg="accessState.errorMsg.value" :progress-percent="accessState.progressPercent.value"
      @retry="accessState.refreshAccess" @learn="router.push('/')"
    >
      <div class="success-tip">✅ 今日学习任务已完成，尽情玩吧！</div>
      <section class="game-grid">
        <button v-for="game in games" :key="game.route" class="game-card" type="button" @click="router.push(game.route)">
          <img v-if="game.image" class="game-cover" :src="game.image" :alt="game.name" loading="lazy" />
          <span v-else class="game-icon">{{ game.icon }}</span>
          <h2>{{ game.name }}</h2><p>{{ game.desc }}</p><strong>开始游戏 →</strong>
        </button>
      </section>
    </GameAccessGate>
  </main>
</template>

<style scoped>
.games-page { min-height: 100dvh; padding: var(--space-4); background: var(--gradient-warm); }
.hero { display: flex; align-items: center; gap: var(--space-4); padding: var(--space-6); margin-bottom: var(--space-5); background: var(--gradient-primary); color: var(--text-on-primary); border-radius: var(--radius-lg); }
.hero > span { font-size: 54px; }
.hero h1 { margin-bottom: var(--space-2); font-size: var(--text-lg); }
.hero p { color: var(--text-on-primary-muted); }
.success-tip { margin-bottom: var(--space-4); padding: var(--space-3); text-align: center; color: var(--color-success-hover); background: var(--color-success-bg); border-radius: var(--radius-md); }
.game-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: var(--space-4); }
.game-card { padding: var(--space-6); text-align: left; background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); transition: transform var(--duration-fast) var(--ease-bounce); }
.game-card:hover { transform: translateY(-4px); }
.game-cover { width: 100%; height: 180px; object-fit: cover; border-radius: var(--radius-md); margin-bottom: var(--space-3); }
.game-icon { display: block; margin-bottom: var(--space-3); font-size: 48px; }
.game-card h2 { color: var(--text-primary); margin-bottom: var(--space-2); }
.game-card p { color: var(--text-secondary); margin-bottom: var(--space-4); }
.game-card strong { color: var(--color-primary); }
@media (max-width: 560px) { .game-grid { grid-template-columns: 1fr; } }
</style>
