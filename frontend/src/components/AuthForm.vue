<!--
  AuthForm.vue - 登录/注册共用的家长邮箱密码表单
  用途: 常规表单(给家长用),提交时 emit submit({ email, password })。
-->
<script setup>
import { reactive } from 'vue'
import AppButton from './AppButton.vue'

const props = defineProps({
  submitText: { type: String, required: true },
  submitting: { type: Boolean, default: false },
  error: { type: String, default: '' },
  newPassword: { type: Boolean, default: false }
})
const emit = defineEmits(['submit'])
const form = reactive({ email: '', password: '' })

function submit() {
  if (!form.email || !form.password || props.submitting) return
  emit('submit', { email: form.email.trim(), password: form.password })
}
</script>

<template>
  <form class="auth-form" @submit.prevent="submit">
    <label for="auth-email">家长邮箱</label>
    <input id="auth-email" v-model="form.email" type="email" autocomplete="email" required />
    <label for="auth-password">密码</label>
    <input id="auth-password" v-model="form.password" type="password" required
      :autocomplete="newPassword ? 'new-password' : 'current-password'" :minlength="newPassword ? 8 : undefined" maxlength="72" />
    <p v-if="newPassword" class="auth-hint">8–72 位。这个密码也用于进入家长区域和解锁游戏。</p>
    <p v-if="error" class="auth-error" role="alert">{{ error }}</p>
    <AppButton block size="lg" :disabled="submitting" @click="submit">{{ submitting ? '请稍候...' : submitText }}</AppButton>
    <!-- 隐藏的 submit 按钮让回车键能提交表单 -->
    <button type="submit" hidden />
  </form>
</template>

<style scoped>
.auth-form { display: grid; gap: var(--space-3); padding: var(--space-5); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); }
.auth-form label { font-weight: var(--font-medium); color: var(--text-primary); }
.auth-form input { min-height: var(--touch-target); padding: var(--space-3); color: var(--text-primary); background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-sm); font: inherit; }
.auth-form input:focus { outline: 2px solid var(--color-primary); outline-offset: 2px; }
.auth-hint { color: var(--text-tertiary); font-size: var(--text-sm); }
.auth-error { color: var(--color-warning); font-size: var(--text-sm); }
</style>
