<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppButton from './AppButton.vue'
const saved = ref(null)
const router = useRouter()
onMounted(() => {
  try {
    const value = JSON.parse(localStorage.getItem('lastLesson') || 'null')
    if (Number.isInteger(value?.id) && typeof value.name === 'string') saved.value = value
  } catch { /* 无有效草稿时不显示入口。 */ }
})
</script>

<template>
  <section v-if="saved" class="continue-lesson">
    <p>上次学到：{{ saved.name }}</p>
    <AppButton @click="router.push({ path: `/lesson/${saved.id}`, query: saved.query })">继续上次学习</AppButton>
  </section>
</template>

<style scoped>
.continue-lesson { padding: var(--space-4); display: grid; gap: var(--space-3); }
</style>
