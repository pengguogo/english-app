<!--
  ParentVerifyModal.vue - 家长二次验证弹窗
  用途: 由 http 拦截器在“家长验证失效”403 时打开;输入家长登录密码,验证成功后原请求自动重试。
-->
<script setup>
import { ref, watch } from 'vue'
import AppModal from './AppModal.vue'
import AppButton from './AppButton.vue'
import { verifyParent } from '../api/auth'
import { parentVerifyOpen, finishParentVerify } from '../composables/useParentVerify'
import { authErrorMessage } from '../utils/authError'

const password = ref('')
const error = ref('')
const submitting = ref(false)

watch(parentVerifyOpen, (open) => {
  if (open) { password.value = ''; error.value = '' }
})

async function submit() {
  if (!password.value || submitting.value) return
  submitting.value = true
  error.value = ''
  try {
    await verifyParent(password.value)
    finishParentVerify(true)
  } catch (e) {
    error.value = authErrorMessage(e, '验证失败，请重试')
  } finally {
    submitting.value = false
    password.value = ''
  }
}
</script>

<template>
  <AppModal :open="parentVerifyOpen" title="家长验证" @close="finishParentVerify(false)">
    <form class="pv-form" @submit.prevent="submit">
      <p class="pv-tip">这里是家长区域，请输入家长账号密码。验证后 10 分钟内有效。</p>
      <label for="pv-password">家长密码</label>
      <input id="pv-password" v-model="password" type="password" autocomplete="current-password" required />
      <p v-if="error" class="pv-error" role="alert">{{ error }}</p>
      <AppButton block :disabled="submitting || !password" @click="submit">{{ submitting ? '验证中...' : '确认' }}</AppButton>
    </form>
  </AppModal>
</template>

<style scoped>
.pv-form { display: grid; gap: var(--space-3); }
.pv-tip { color: var(--text-secondary); font-size: var(--text-sm); }
.pv-form input { min-height: var(--touch-target); padding: var(--space-3); color: var(--text-primary); background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-sm); font: inherit; }
.pv-error { color: var(--color-warning); font-size: var(--text-sm); }
</style>
