<!--
  RegisterView.vue - 家庭账号注册页
  用途: 家长用邮箱密码注册;成功后进入选孩子/建孩子页。
  注册后后端自动登录。
-->
<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AuthForm from '../components/AuthForm.vue'
import { register } from '../api/auth'
import { useAuthStore } from '../stores/auth'
import { nextStepAfterAuth } from '../router/authFlow'
import { authErrorMessage } from '../utils/authError'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const submitting = ref(false)
const error = ref('')

async function onSubmit(data) {
  submitting.value = true
  error.value = ''
  try {
    await register(data)
    await auth.refresh()
    router.replace(nextStepAfterAuth(auth, route.query.redirect))
  } catch (e) {
    error.value = authErrorMessage(e, '注册失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <BackBar title="注册家庭账号" @back="router.push('/')" />
    <p class="auth-intro">由家长注册。一个家庭账号可以添加多个孩子，各自记录学习进度。</p>
    <AuthForm submit-text="注册" new-password :submitting="submitting" :error="error" @submit="onSubmit" />
    <p class="auth-switch">已有账号？<RouterLink :to="{ path: '/login', query: route.query }">去登录</RouterLink></p>
  </main>
</template>

<style scoped>
.auth-page { max-width: 480px; margin: 0 auto; padding: var(--space-4); display: grid; gap: var(--space-4); }
.auth-intro, .auth-switch { color: var(--text-secondary); }
.auth-switch a { color: var(--color-primary); font-weight: var(--font-medium); }
</style>
