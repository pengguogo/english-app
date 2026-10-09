<script setup>
import { ref } from 'vue'
import AppButton from '../AppButton.vue'
import { unlockGames } from '../../api/games'

defineProps({
  access: { type: Object, default: null },
  isLoading: { type: Boolean, default: false },
  errorMsg: { type: String, default: '' },
  progressPercent: { type: Number, default: 0 }
})
const emit = defineEmits(['retry', 'learn'])
const showPassword = ref(false)
const password = ref('')
const passwordError = ref('')
const isUnlocking = ref(false)

async function submitPassword() {
  if (password.value.length !== 6 || isUnlocking.value) return
  isUnlocking.value = true
  passwordError.value = ''
  try {
    const result = await unlockGames(password.value)
    if (!result.success) {
      passwordError.value = result.message
      password.value = ''
      return
    }
    emit('retry')
  } catch (error) {
    passwordError.value = '解锁失败，请稍后重试'
    console.error('密码解锁游戏失败:', error)
  } finally {
    isUnlocking.value = false
  }
}
</script>

<template>
  <div v-if="isLoading" class="gate state" role="status">正在检查今日学习时长…</div>
  <div v-else-if="errorMsg" class="gate state error" role="alert">
    <p>{{ errorMsg }}</p>
    <AppButton variant="ghost" @click="$emit('retry')">重新检查</AppButton>
  </div>
  <div v-else-if="access && !access.unlocked" class="gate locked">
    <span class="lock-icon" aria-hidden="true">🔒</span>
    <h2>再累计 {{ Math.ceil(access.remainingSeconds / 60) }} 分钟有效学习可解锁</h2>
    <p>每课复习全部答对后结算时长。今日已结算 {{ Math.floor(access.studiedSeconds / 60) }} 分 {{ access.studiedSeconds % 60 }} 秒</p>
    <div class="progress" role="progressbar" :aria-valuenow="progressPercent" aria-valuemin="0" aria-valuemax="100">
      <span :style="{ width: `${progressPercent}%` }"></span>
    </div>
    <AppButton size="lg" @click="$emit('learn')">去学习</AppButton>
    <button v-if="!showPassword" type="button" class="parent-link" @click="showPassword = true">家长密码解锁</button>
    <form v-else class="password-form" @submit.prevent="submitPassword">
      <label for="game-password">输入 6 位家长密码</label>
      <div class="password-row">
        <input id="game-password" v-model="password" type="password" inputmode="numeric" maxlength="6" pattern="[0-9]{6}" autocomplete="off" placeholder="••••••" />
        <AppButton
          :disabled="password.length !== 6 || isUnlocking"
          @click="submitPassword"
        >{{ isUnlocking ? '验证中…' : '解锁' }}</AppButton>
      </div>
      <p v-if="passwordError" class="password-error" role="alert">{{ passwordError }}</p>
    </form>
  </div>
  <slot v-else />
</template>

<style scoped>
.gate { text-align: center; padding: var(--space-8); }
.state { color: var(--text-tertiary); }
.error { color: var(--color-warning); }
.error p { margin-bottom: var(--space-3); }
.locked { background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.lock-icon { display: block; font-size: 48px; margin-bottom: var(--space-3); }
.locked h2 { color: var(--text-primary); margin-bottom: var(--space-2); }
.locked p { color: var(--text-secondary); margin-bottom: var(--space-4); }
.progress { height: 14px; overflow: hidden; margin-bottom: var(--space-5); background: var(--bg-muted); border-radius: var(--radius-pill); }
.progress span { display: block; height: 100%; background: var(--gradient-success); border-radius: inherit; transition: width var(--duration-normal) var(--ease-smooth); }
.parent-link { display: block; margin: var(--space-4) auto 0; padding: var(--space-2); color: var(--color-primary); text-decoration: underline; }
.password-form { max-width: 360px; margin: var(--space-4) auto 0; padding-top: var(--space-4); border-top: 1px solid var(--border-light); text-align: left; }
.password-form label { display: block; margin-bottom: var(--space-2); color: var(--text-secondary); font-size: var(--text-sm); }
.password-row { display: flex; gap: var(--space-2); }
.password-row input { min-width: 0; flex: 1; padding: var(--space-3); color: var(--text-primary); background: var(--bg-card); border: 2px solid var(--border-light); border-radius: var(--radius-md); font-size: var(--text-lg); letter-spacing: 0.3em; }
.password-row input:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--focus-ring); outline: none; }
.password-error { margin-top: var(--space-2); color: var(--color-warning) !important; font-size: var(--text-sm); }
</style>
