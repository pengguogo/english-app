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
    <AppButton
      v-if="task"
      size="lg"
      block
      class="today-btn"
      @click="router.push({ path: task.path, query: task.query })"
    >
      <span class="today-icon" aria-hidden="true">▶</span>
      <span class="today-text">
        <span class="today-label">开始今日任务</span>
        <span class="today-title">{{ task.title }}</span>
      </span>
    </AppButton>
    <p v-else-if="!error" role="status">正在准备今日任务...</p>
    <template v-if="error && !task">
      <p role="alert">{{ error }}</p>
      <AppButton size="lg" @click="load">重试</AppButton>
    </template>
  </section>
</template>

<style scoped>
.today-task { display: grid; gap: var(--space-3); margin-bottom: var(--space-5); }
.today-task p { color: var(--text-secondary); text-align: center; }
.today-btn { min-height: 96px; justify-content: flex-start; gap: var(--space-4); padding: var(--space-4) var(--space-6); white-space: normal; text-align: left; }
.today-icon { display: inline-flex; align-items: center; justify-content: center; flex-shrink: 0; width: 56px; height: 56px; border-radius: var(--radius-pill); background: var(--brand-surface-soft); color: var(--color-primary); font-size: var(--text-lg); }
.today-text { display: grid; gap: var(--space-1); min-width: 0; }
.today-label { font-size: var(--text-xl); font-weight: var(--font-bold); }
.today-title { font-size: var(--text-sm); opacity: 0.9; overflow-wrap: anywhere; }
</style>
