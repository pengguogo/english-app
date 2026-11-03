<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import AppButton from '../AppButton.vue'
import { unlockGames } from '../../api/games'

defineProps({
  access: { type: Object, default: null },
  isLoading: { type: Boolean, default: false },
  errorMsg: { type: String, default: '' },
  progressPercent: { type: Number, default: 0 }
})
const router = useRouter()
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
    <!-- 家长入口：小锁，展开后才显示密码解锁与设置 -->
    <button
      type="button"
      class="parent-lock"
      :aria-expanded="showPassword"
      aria-label="家长解锁"
      title="家长解锁"
      @click="showPassword = !showPassword"
    >🔒</button>
    <span class="lock-icon" aria-hidden="true">🎮</span>
    <div class="progress" role="progressbar" :aria-valuenow="progressPercent" aria-valuemin="0" aria-valuemax="100" aria-label="今日学习进度">
      <span :style="{ width: `${progressPercent}%` }"></span>
    </div>
    <p class="remain">还差 {{ Math.ceil(access.remainingSeconds / 60) }} 分钟</p>
    <AppButton size="lg" class="learn-btn" @click="$emit('learn')">去学习</AppButton>
    <div v-if="showPassword" class="parent-panel">
      <form class="password-form" @submit.prevent="submitPassword">
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
      <AppButton variant="ghost" @click="router.push('/parent-center')">设置或修改游戏密码</AppButton>
    </div>
  </div>
  <slot v-else />
</template>

<style scoped>
.gate { text-align: center; padding: var(--space-8); }
.state { color: var(--text-tertiary); }
.error { color: var(--color-warning); }
.error p { margin-bottom: var(--space-3); }
.locked { position: relative; background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.parent-lock { position: absolute; top: var(--space-2); right: var(--space-2); display: inline-flex; align-items: center; justify-content: center; width: var(--touch-target); height: var(--touch-target); border: 0; border-radius: var(--radius-pill); background: var(--bg-muted); font-size: var(--text-base); cursor: pointer; }
.parent-lock:focus-visible { outline: none; box-shadow: 0 0 0 3px var(--focus-ring); }
.remain { color: var(--text-secondary); margin-bottom: var(--space-5); font-size: var(--text-lg); font-weight: var(--font-bold); white-space: nowrap; }
.learn-btn { min-width: 200px; }
.parent-panel { display: grid; justify-items: center; gap: var(--space-3); margin-top: var(--space-5); }
.lock-icon { display: block; font-size: 48px; margin-bottom: var(--space-3); }
.progress { height: 20px; overflow: hidden; margin-bottom: var(--space-3); background: var(--bg-muted); border-radius: var(--radius-pill); }
.progress span { display: block; height: 100%; background: var(--gradient-success); border-radius: inherit; transition: width var(--duration-normal) var(--ease-smooth); }
.password-form { width: 100%; min-width: 0; max-width: 360px; padding-top: var(--space-4); border-top: 1px solid var(--border-light); text-align: left; }
.password-form label { display: block; margin-bottom: var(--space-2); color: var(--text-secondary); font-size: var(--text-sm); }
.password-row { display: flex; gap: var(--space-2); }
.password-row input { width: 0; min-width: 0; flex: 1; padding: var(--space-3); color: var(--text-primary); background: var(--bg-card); border: 2px solid var(--border-light); border-radius: var(--radius-md); font-size: var(--text-lg); letter-spacing: 0.3em; }
.password-row input:focus { border-color: var(--color-primary); box-shadow: 0 0 0 3px var(--focus-ring); outline: none; }
.password-error { margin-top: var(--space-2); color: var(--color-warning) !important; font-size: var(--text-sm); }
</style>
