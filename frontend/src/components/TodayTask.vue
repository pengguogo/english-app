<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppButton from './AppButton.vue'
import { getTodayTask } from '../api/learning'
import { chooseTodayTask } from '../utils/todayTask'
const router = useRouter()
const task = ref(null)
const error = ref('')
async function load() {
  error.value = ''
  try {
    const suggested = await getTodayTask()
    task.value = suggested
    try { task.value = chooseTodayTask(localStorage, suggested) } catch { /* 本机存储被禁用时仍显示服务端任务。 */ }
  } catch {
    try { task.value = chooseTodayTask(localStorage, null) } catch { /* 存储不可用。 */ }
    error.value = '今日任务暂时无法加载，可以重试或直接选择学科。'
  }
}
onMounted(load)
</script>

<template>
  <section class="today-task" aria-label="今日学习任务">
    <p class="task-label">今天先做什么</p>
    <template v-if="task">
      <h2>{{ task.title }}</h2>
      <p>{{ task.reason }}</p>
      <AppButton @click="router.push({ path: task.path, query: task.query })">开始今日任务</AppButton>
    </template>
    <p v-else-if="!error" role="status">正在准备今日任务...</p>
    <template v-if="error"><p role="alert">{{ error }}</p><AppButton variant="ghost" @click="load">重试</AppButton></template>
    <AppButton variant="ghost" @click="router.push('/parent-center')">家长中心 · 学习周报与游戏密码</AppButton>
  </section>
</template>

<style scoped>
.today-task { display: grid; gap: var(--space-3); padding: var(--space-5); margin-bottom: var(--space-4); border-radius: var(--radius-lg); background: var(--bg-card); box-shadow: var(--shadow-card); }
.task-label { color: var(--color-primary); font-weight: var(--font-bold); }
.today-task p { color: var(--text-secondary); }
</style>
