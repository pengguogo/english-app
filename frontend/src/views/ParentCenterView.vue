<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import WeeklyReport from '../components/parent/WeeklyReport.vue'
import { useParentCenter } from '../composables/useParentCenter'
const router = useRouter()
const { unlocked, loading, error, report, load } = useParentCenter()
// 家长验证失效时 http 拦截器会弹出家长密码框
onMounted(load)
</script>

<template>
  <main class="parent-center">
    <BackBar title="家长中心" @back="router.push('/')" />
    <p v-if="error" role="alert">{{ error }}</p>
    <section v-if="!unlocked" class="access-form">
      <h2>验证家长身份</h2>
      <p>进入家长中心需要再次输入家长密码，10 分钟内有效。</p>
      <AppButton :disabled="loading" @click="load">{{ loading ? '验证中...' : '验证并进入' }}</AppButton>
    </section>
    <template v-else>
      <!-- 原首页的家长类入口统一收在这里 -->
      <nav class="parent-links" aria-label="家长工具">
        <AppButton variant="ghost" @click="router.push('/child-profile')">记录身高体重</AppButton>
        <AppButton variant="ghost" @click="router.push('/wrong-answers')">错题集</AppButton>
        <AppButton variant="ghost" @click="router.push('/learned')">学习记录</AppButton>
        <AppButton variant="ghost" @click="router.push('/agent-records')">学习伙伴记录</AppButton>
        <AppButton variant="ghost" @click="router.push('/consent')">学习伙伴说明与同意</AppButton>
      </nav>
      <AppButton variant="ghost" :disabled="loading" @click="load">刷新周报</AppButton>
      <WeeklyReport v-if="report" :report="report" />
    </template>
  </main>
</template>

<style scoped>
.parent-center { max-width: var(--content-max); margin: auto; padding: var(--space-4); display: grid; gap: var(--space-4); }
.access-form { display: grid; gap: var(--space-3); padding: var(--space-5); background: var(--bg-card); border-radius: var(--radius-lg); }
.parent-links { display: flex; flex-wrap: wrap; gap: var(--space-3); }
</style>
