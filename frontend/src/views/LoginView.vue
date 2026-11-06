<!--
  LoginView.vue - 家长登录页
  用途: 邮箱密码登录;成功后按会话状态去同意页 / 选孩子页 / 原目标页。
-->
<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AuthForm from '../components/AuthForm.vue'
import { login } from '../api/auth'
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
    await login(data)
    await auth.refresh()
    router.replace(nextStepAfterAuth(auth, route.query.redirect))
  } catch (e) {
    error.value = authErrorMessage(e, '登录失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <BackBar title="家长登录" @back="router.push('/')" />
    <p class="auth-intro">登录后可以保存孩子的学习进度和错题。不登录也可以先浏览课程。</p>
    <AuthForm submit-text="登录" :submitting="submitting" :error="error" @submit="onSubmit" />
    <p class="auth-switch">还没有账号？<RouterLink :to="{ path: '/register', query: route.query }">注册家庭账号</RouterLink></p>
  </main>
</template>

<style scoped>
.auth-page { max-width: 480px; margin: 0 auto; padding: var(--space-4); display: grid; gap: var(--space-4); }
.auth-intro, .auth-switch { color: var(--text-secondary); }
.auth-switch a { color: var(--color-primary); font-weight: var(--font-medium); }
</style>
