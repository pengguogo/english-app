<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import GameAccessGate from '../components/games/GameAccessGate.vue'
import { useGameAccess } from '../composables/useGameAccess'
import { SIZE, KOMI, newGame, play, pass, toggleDead, score } from '../utils/goGame.js'

const router = useRouter()
const accessState = useGameAccess()
const game = ref(newGame())
const message = ref('')
const result = computed(() => score(game.value))
const status = computed(() => game.value.phase === 'play' ? `${game.value.current === 'black' ? '黑方' : '白方'}落子${game.value.passes ? ' · 对方已停一手' : ''}` : game.value.phase === 'mark' ? '双方确认死子' : '对局结束')
function select(index) {
  if (game.value.phase === 'mark') { game.value = toggleDead(game.value, index); message.value = ''; return }
  if (game.value.phase !== 'play') return
  const next = play(game.value, index); game.value = next.game; message.value = next.error
}
function confirmScore() {
  if (game.value.confirmations === 0) game.value = { ...game.value, confirmations: 1 }
  else game.value = { ...game.value, phase: 'finished' }
}
function resume() { game.value = { ...game.value, phase: 'play', passes: 0, dead: [], confirmations: 0 }; message.value = '' }
function undo() { if (game.value.history.length) { game.value = game.value.history.at(-1); message.value = '' } }
function restart() { if (window.confirm('确定重新开始？当前棋局将丢失。')) { game.value = newGame(); message.value = '' } }
</script>

<template>
  <main class="game-page">
    <BackBar title="九路围棋" @back="router.push('/games')" />
    <GameAccessGate :access="accessState.access.value" :is-loading="accessState.isLoading.value" :error-msg="accessState.errorMsg.value" :progress-percent="accessState.progressPercent.value" @retry="accessState.refreshAccess" @learn="router.push('/')">
      <section class="game-panel">
        <div class="heading"><h1>双人围棋</h1><strong aria-live="polite">{{ status }}</strong></div>
        <p class="intro">同一台设备轮流下棋 · 九路棋盘 · 白方贴 {{ KOMI }} 点</p>
        <div class="board" role="grid" aria-label="九路围棋棋盘">
          <button v-for="(_, index) in game.board" :key="index" class="cell" type="button" role="gridcell" :aria-label="`第 ${Math.floor(index / SIZE) + 1} 行第 ${index % SIZE + 1} 列，${game.board[index] === 'black' ? '黑子' : game.board[index] === 'white' ? '白子' : '空'}${game.dead.includes(index) ? '，标记为死子' : ''}`" @click="select(index)">
            <span v-if="game.board[index]" class="stone" :class="game.board[index]"><span v-if="game.dead.includes(index)" class="dead">×</span></span>
          </button>
        </div>
        <p v-if="message" class="message" role="status">{{ message }}</p>
        <div class="actions">
          <template v-if="game.phase === 'play'"><AppButton @click="game = pass(game); message = ''">停一手</AppButton><AppButton variant="ghost" :disabled="!game.history.length" @click="undo">悔棋</AppButton></template>
          <template v-if="game.phase === 'mark'"><AppButton @click="confirmScore">{{ game.confirmations ? '白方确认并计分' : '黑方确认死子' }}</AppButton><AppButton variant="ghost" @click="resume">有争议，继续下棋</AppButton></template>
          <AppButton variant="ghost" @click="restart">重新开始</AppButton>
        </div>
        <div v-if="game.phase === 'mark'" class="notice" aria-live="polite">点击棋子可整块标记或取消死子。{{ game.confirmations ? '黑方已确认，请白方检查后确认；修改标记会重新确认。' : '请先由双方商量，再由黑方确认。' }}</div>
        <div v-if="game.phase === 'finished'" class="result" aria-live="polite">
          <h2>{{ result.black > result.white ? '黑方获胜' : result.white > result.black ? '白方获胜' : '平局' }}<template v-if="result.black !== result.white"> · 领先 {{ Math.abs(result.black - result.white) }} 点</template></h2>
          <p>黑方 {{ result.black }} 点 = 活子 {{ result.blackStones }} + 围空 {{ result.blackTerritory }}</p>
          <p>白方 {{ result.white }} 点 = 活子 {{ result.whiteStones }} + 围空 {{ result.whiteTerritory }} + 贴 {{ KOMI }}</p>
          <p>双方相邻的中立空点：{{ result.neutral }}。死子已移除后计分。</p>
        </div>
        <details class="rules"><summary>围棋规则与计分说明</summary>
          <ol><li>黑先白后，轮流在交叉点落一子；棋子不移动。棋盘边缘和角落也可以落子。</li><li>棋子上下左右相邻的空点称为“气”，相连的同色棋子共享气。对方棋块失去最后一口气时，整块棋子被提走。</li><li>不能下在没有气且无法提子的地方；本局禁止落子造成此前出现过的棋盘局面，防止打劫循环。停一手不受此限制。</li><li>不想落子时可停一手；双方连续停手后，点击棋子标记死子，再分别确认。修改标记会清除确认；有争议可继续下棋。</li><li>死子是双方同意无法存活的棋块。计分先移除死子，按面积计分：活子数量加只与己方棋子相邻的空点；黑先行，白方加 7.5 点。双方相邻的空点不计入任何一方。点数多者获胜。</li></ol>
          <p>如果不确定死活或双活，请继续下棋并由双方达成一致；系统不会自动判断死活。</p>
        </details>
      </section>
    </GameAccessGate>
  </main>
</template>

<style scoped>
.game-page { min-height: 100dvh; padding: var(--space-4); background: var(--gradient-warm); }
.game-panel { max-width: 760px; margin: auto; padding: var(--space-4); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); color: var(--text-primary); }
.heading { display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: var(--space-2); }
.heading h1 { margin: 0; font-size: var(--text-xl); }
.intro, .message { margin: var(--space-2) 0; color: var(--text-secondary); }
.board { display: grid; grid-template-columns: repeat(9, 1fr); width: min(100%, 590px); aspect-ratio: 1; margin: var(--space-3) auto; padding: var(--space-3); background: var(--game-board); border: 3px solid var(--game-board-edge); border-radius: var(--radius-sm); }
.cell { position: relative; min-width: 0; padding: 0; border: 0; cursor: pointer; background-color: var(--color-transparent); background-image: linear-gradient(var(--game-grid), var(--game-grid)), linear-gradient(90deg, var(--game-grid), var(--game-grid)); background-size: 100% 1px, 1px 100%; background-position: center; background-repeat: no-repeat; }
.cell:focus-visible { outline: 3px solid var(--color-primary); z-index: 1; }
.stone { position: absolute; inset: 8%; display: grid; place-items: center; border-radius: var(--radius-pill); box-shadow: var(--shadow-soft); }
.stone.black { background: var(--game-stone-black); color: var(--game-stone-white); }
.stone.white { background: var(--game-stone-white); border: 1px solid var(--border-light); color: var(--game-stone-black); }
.dead { font-size: var(--text-xl); font-weight: bold; line-height: 1; }
.actions { display: flex; flex-wrap: wrap; justify-content: center; gap: var(--space-2); }
.notice, .result, .rules { margin-top: var(--space-4); padding: var(--space-3); background: var(--bg-muted); border-radius: var(--radius-md); line-height: 1.7; }
.result h2, .result p { margin: var(--space-1) 0; }
.rules summary { cursor: pointer; font-weight: bold; }
.rules li { margin-top: var(--space-2); }
@media (max-width: 600px) { .game-page { padding: var(--space-2); } .game-panel { padding: var(--space-3); } }
</style>
