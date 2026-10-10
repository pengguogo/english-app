<script setup>
import { useRouter } from 'vue-router'
import AppButton from './AppButton.vue'
const router = useRouter()
</script>

<template>
  <section class="character-entry">
    <img :src="'/images/hanzi-pilot/hanzi-shan.jpg'" alt="认字小课堂的山景配图" />
    <div>
      <h2>认字小课堂</h2>
      <p>按统编一年级上册选字，听一听、找一找，学过的字再复习。</p>
      <AppButton @click="router.push('/character-review')">认字与每日复习</AppButton>
    </div>
  </section>
</template>

<style scoped>
.character-entry { display: flex; align-items: center; gap: var(--space-4); padding: var(--space-5); margin-bottom: var(--space-6); border-radius: var(--radius-lg); background: var(--bg-card); color: var(--text-primary); }
img { width: 100px; height: 100px; object-fit: contain; border-radius: var(--radius-md); }
p { margin: var(--space-3) 0; color: var(--text-secondary); }
@media (max-width: 480px) { .character-entry { flex-direction: column; text-align: center; } }
</style>
