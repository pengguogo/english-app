<!--
  CompanionPanel.vue - 全站常驻学习伙伴(右下角小老鼠入口)
  默认安静:不自动弹出、不自动出声;点小老鼠展开面板。
  面板:按住说话(登录+同意才可用) / 打字 → 候选卡 → 点「去学」才跳转;点主题直接跳主题页。
  切页、关闭、卸载时停录音和播报并丢弃过期响应(useCompanion.cancel)。
-->
<script setup>
import { ref, watch, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppButton from '../AppButton.vue'
import CompanionMouse from './CompanionMouse.vue'
import CompanionCandidateCard from './CompanionCandidateCard.vue'
import { useAuthStore } from '../../stores/auth'
import { useCompanion, MAX_TEXT } from '../../composables/useCompanion'

const auth = useAuthStore()
const route = useRoute()
const c = useCompanion({ auth, router: useRouter() })
const { open, phase, result, topics, error, needsPlayButton, micMode, mouseState } = c
const draft = ref('')

const STATUS = { recording: '我在听…', recognizing: '听到啦…', thinking: '我找找…', speaking: '', choosing: '' }

async function toggle() {
  if (open.value) return c.closePanel()
  auth.ensureLoaded()
  await c.openPanel()
}

function send() {
  const text = draft.value
  draft.value = ''
  c.submitText(text)
}

// 切页:停录音和播报、丢弃在途响应
watch(() => route.fullPath, () => c.cancel())
onBeforeUnmount(() => c.cancel())
</script>

<template>
  <div class="companion">
    <section v-if="open" class="panel" role="dialog" aria-label="学习伙伴">
      <header class="panel-head">
        <CompanionMouse :state="mouseState" :size="64" />
        <p class="bubble" aria-live="polite">
          <template v-if="error">{{ error.message }}</template>
          <template v-else-if="STATUS[phase]">{{ STATUS[phase] }}</template>
          <template v-else-if="result?.speech">{{ result.speech }}</template>
          <template v-else>想学什么？</template>
        </p>
        <AppButton variant="ghost" aria-label="关闭学习伙伴" @click="c.closePanel()">✕</AppButton>
      </header>

      <AppButton v-if="needsPlayButton" variant="primary" size="lg" block @click="c.replay()">
        ▶ 听一听
      </AppButton>

      <div v-if="result?.candidates?.length" class="cands">
        <CompanionCandidateCard v-for="cand in result.candidates" :key="cand.lessonId ?? cand.route"
          :candidate="cand" @go="c.go" />
      </div>

      <div v-if="!result?.candidates?.length || result?.type === 'NO_MATCH'" class="topics">
        <button v-for="t in (result?.topics?.length ? result.topics : topics)" :key="t.themeId ?? t.name"
          type="button" class="topic" :disabled="phase === 'recording'" @click="c.go(t)">
          <img v-if="t.image" :src="t.image" alt="" loading="lazy" />
          <span>{{ t.name }}</span>
        </button>
      </div>

      <AppButton v-if="micMode !== 'hidden'" class="mic" size="lg" block
        :variant="phase === 'recording' ? 'warning' : 'primary'"
        :aria-pressed="phase === 'recording'"
        @pointerdown.prevent="c.startRecording()"
        @pointerup="c.stopRecording()" @pointercancel="c.stopRecording()" @pointerleave="c.stopRecording()"
        @contextmenu.prevent>
        🎤 {{ phase === 'recording' ? '松开就好' : '按住说话' }}
      </AppButton>

      <form class="typing" @submit.prevent="send">
        <input v-model="draft" class="typing-input" :maxlength="MAX_TEXT" placeholder="打字也行" aria-label="想学什么" />
        <AppButton variant="success" :disabled="!draft.trim()" @click="send">找</AppButton>
      </form>
    </section>

    <button type="button" class="entry" :aria-expanded="open" aria-label="学习伙伴" @click="toggle">
      <CompanionMouse :state="open ? mouseState : 'idle'" :size="56" />
    </button>
  </div>
</template>

<style scoped>
.companion {
  position: fixed;
  right: max(var(--space-4), env(safe-area-inset-right));
  bottom: max(var(--space-4), env(safe-area-inset-bottom));
  z-index: 900;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: var(--space-3);
  pointer-events: none;
}
.companion > * { pointer-events: auto; }
.entry {
  border-radius: 50%;
  background: var(--bg-card);
  box-shadow: var(--shadow-hover);
  padding: var(--space-1);
  line-height: 0;
}
.entry:focus-visible { outline: 3px solid var(--focus-ring); }
.panel {
  width: min(380px, calc(100vw - 2 * var(--space-4)));
  max-height: calc(100dvh - 120px);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  padding: var(--space-4);
  background: var(--bg-page);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-hover);
}
.panel-head { display: flex; align-items: center; gap: var(--space-2); }
.bubble {
  flex: 1;
  margin: 0;
  padding: var(--space-2) var(--space-3);
  background: var(--bg-card);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}
.cands { display: flex; flex-direction: column; gap: var(--space-2); }
.topics { display: grid; grid-template-columns: repeat(2, 1fr); gap: var(--space-2); }
.topic {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  min-height: 64px;
  padding: var(--space-2);
  background: var(--bg-card);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card);
  font-size: var(--text-base);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}
.topic img { width: 48px; height: 48px; object-fit: cover; border-radius: var(--radius-sm); }
.mic { touch-action: none; user-select: none; -webkit-user-select: none; }
.typing { display: flex; gap: var(--space-2); }
.typing-input {
  flex: 1;
  min-width: 0;
  min-height: var(--touch-target);
  padding: 0 var(--space-3);
  border: 2px solid var(--border-light);
  border-radius: var(--radius-pill);
  background: var(--bg-card);
  font-size: var(--text-base);
  color: var(--text-primary);
}
@media (prefers-reduced-motion: no-preference) {
  .panel { animation: panel-in var(--duration-normal) var(--ease-smooth); }
  @keyframes panel-in { from { opacity: 0; transform: translateY(12px); } }
}
</style>
