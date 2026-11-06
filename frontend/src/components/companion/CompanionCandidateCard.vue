<!--
  CompanionCandidateCard.vue - 学习伙伴候选课程卡:配图 + 课程名 + 「去学」
  点「去学」才 emit go,由面板负责跳转。
-->
<script setup>
import AppButton from '../AppButton.vue'

defineProps({ candidate: { type: Object, required: true } })
defineEmits(['go'])
</script>

<template>
  <div class="cand">
    <img v-if="candidate.image" class="cand-img" :src="candidate.image" alt="" loading="lazy" />
    <div v-else class="cand-img cand-img--empty" aria-hidden="true" />
    <div class="cand-body">
      <div class="cand-title">{{ candidate.title }}</div>
      <div v-if="candidate.themeName" class="cand-theme">{{ candidate.themeName }}</div>
    </div>
    <AppButton variant="success" size="lg" :aria-label="`去学${candidate.title}`" @click="$emit('go', candidate)">
      去学
    </AppButton>
  </div>
</template>

<style scoped>
.cand {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}
.cand-img {
  width: 72px;
  height: 72px;
  flex: none;
  object-fit: cover;
  border-radius: var(--radius-md);
  background: var(--bg-muted);
}
.cand-body { flex: 1; min-width: 0; }
.cand-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1.25;
  /* 绘本标题带书名(如“小海龟回家 · 看图听故事”),414 宽下单行放不下,最多两行 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-all;
}
.cand-theme { font-size: var(--text-sm); color: var(--text-secondary); }
</style>
