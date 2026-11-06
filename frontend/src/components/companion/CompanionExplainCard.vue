<!--
  CompanionExplainCard.vue - 学习伙伴讲解卡:标题 + 配图 + 正文 + 点选题 + 「去学完整课程」
  source=GENERATED 时角落显示「豆豆想的」,和课程原文区分。
  选项点击 emit answer,由面板调 /agent/answer;答对后所有选项锁定。
-->
<script setup>
import AppButton from '../AppButton.vue'

defineProps({
  explain: { type: Object, required: true },
  quiz: { type: Object, default: null },
  // useCompanion.quiz:{wrong: string[], solved, pending, expired, picked, feedback}
  state: { type: Object, default: null },
  learnMore: { type: Object, default: null }
})
defineEmits(['answer', 'go'])
</script>

<template>
  <article class="explain">
    <span v-if="explain.source === 'GENERATED'" class="tag">豆豆想的</span>
    <h3 class="title">{{ explain.title }}</h3>
    <img v-if="explain.image" class="img" :src="explain.image" alt="" loading="lazy" />
    <p class="text">{{ explain.text }}</p>

    <div v-if="quiz" class="quiz">
      <p class="question">{{ quiz.question }}</p>
      <AppButton v-for="opt in quiz.options" :key="opt.id" size="lg" block
        :variant="state?.picked === opt.id ? 'success' : state?.wrong.includes(opt.id) ? 'warning' : 'primary'"
        :disabled="state?.picked !== opt.id && (state?.solved || state?.pending || state?.expired || state?.wrong.includes(opt.id))"
        :aria-disabled="state?.solved ? 'true' : undefined"
        @click="$emit('answer', opt)">
        {{ opt.text }}
      </AppButton>
      <p v-if="state?.feedback" class="feedback" :class="{ ok: state.solved }" aria-live="polite">{{ state.feedback }}</p>
    </div>

    <AppButton v-if="learnMore" variant="success" size="lg" block @click="$emit('go', learnMore)">
      去学完整课程
    </AppButton>
  </article>
</template>

<style scoped>
.explain {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  padding: var(--space-3);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}
.tag {
  position: absolute;
  top: var(--space-2);
  right: var(--space-2);
  padding: 2px var(--space-2);
  border-radius: var(--radius-pill);
  background: var(--color-primary-bg);
  color: var(--color-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
}
.title {
  margin: 0;
  padding-right: 5em;
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}
.img { width: 100%; max-height: 180px; object-fit: contain; border-radius: var(--radius-md); background: var(--bg-muted); }
.text { margin: 0; font-size: var(--text-base); line-height: 1.6; color: var(--text-primary); }
.quiz { display: flex; flex-direction: column; gap: var(--space-2); }
.question { margin: var(--space-2) 0 0; font-size: var(--text-base); font-weight: var(--font-bold); color: var(--text-primary); }
.feedback { margin: 0; padding: var(--space-2); border-radius: var(--radius-md); background: var(--color-warning-bg); color: var(--text-primary); }
.feedback.ok { background: var(--color-success-bg); }
</style>
