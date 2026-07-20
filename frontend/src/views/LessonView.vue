<!-- LessonView.vue - 课时学习页(占位,Task 20 将实现完整功能) -->
<script setup>
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
</script>

<template>
  <div class="lesson-placeholder">
    <button class="back-btn" @click="router.back()">← 返回</button>
    <h2>课时 {{ route.params.lessonId }}</h2>
    <p>课时学习功能开发中...</p>
  </div>
</template>

<style scoped>
.back-btn { background: none; border: none; font-size: 1rem; color: #667eea; cursor: pointer; margin-bottom: 12px; }
h2 { margin-bottom: 8px; }
p { color: #888; }
</style>
