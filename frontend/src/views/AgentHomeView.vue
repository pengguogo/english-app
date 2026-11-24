<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppButton from '../components/AppButton.vue'
import AgentComposer from '../components/agent/AgentComposer.vue'
import GuidedStep from '../components/agent/GuidedStep.vue'
import CompanionMouse from '../components/companion/CompanionMouse.vue'
import CompanionExplainCard from '../components/companion/CompanionExplainCard.vue'
import CompanionCandidateCard from '../components/companion/CompanionCandidateCard.vue'
import { useAuthStore } from '../stores/auth'
import { useCompanion } from '../composables/useCompanion'
import { useGuidedLearning } from '../composables/useGuidedLearning'

const trainCover = '/images/story/railway-logistics-06.jpg'
const router = useRouter()
const auth = useAuthStore()
const showGuide = ref(true)
const guide = useGuidedLearning()
const { screen, loading, pending, error, audioError, playing } = guide
const companion = useCompanion({ auth, router, onIntent: handleIntent })
const { result, quiz, phase, micMode, mouseState, needsPlayButton, topics } = companion
const awaitingSpeech = computed(() => ['recognizing', 'thinking'].includes(phase.value))
const chatError = companion.error
const parentRoute = computed(() => auth.hasChild ? '/parent-center' : auth.loggedIn ? '/choose-child' : '/login')

function handleIntent(text) {
  const normalized = text.trim().replace(/[？?。！!，,\s]/g, '')
  const answer = normalized.replace(/^(豆豆)?(我选|我觉得是|我猜是|是)/, '').replace(/[呀啦吧]$/, '')
  const option = screen.value?.options.find(choice => choice.text === answer)
  if (option) {
    showGuide.value = true
    void guide.act('ANSWER', option.id)
    return true
  }
  if (!/^(豆豆)?(我想|我想要|想|我要|可以|我们|一起|请)?(看|听|学|了解|去看|去学|找|讲|探索|开始)?(一下|一看|一听|关于)?(小?火车)(的?探险|的?故事)?(吗|吧|呀|啦)?$/.test(normalized)) return false
  startTrain()
  return true
}

function startTrain() {
  showGuide.value = true
  if (screen.value && screen.value.step !== 'DONE') {
    void guide.listen()
    return
  }
  void guide.act('START')
}

function chooseTrain() {
  companion.closePanel()
  startTrain()
}

function send(text) {
  guide.stop()
  showGuide.value = false
  void companion.submitText(text)
}

function startRecording() {
  guide.stop()
  showGuide.value = false
  void companion.startRecording()
}

function openBook() {
  const book = screen.value?.book
  if (!book) return
  guide.stop()
  router.push({ name: 'picturebook', params: { unitId: book.unitId },
    query: { lesson: book.lessonId, guide: screen.value.revision } })
}

onMounted(() => { void guide.load(); void companion.openPanel() })
onBeforeUnmount(companion.closePanel)
</script>

<template>
  <main class="agent-home">
    <header class="top">
      <div class="wordmark"><CompanionMouse :state="mouseState" :size="42" /><span>豆豆</span></div>
      <nav aria-label="学习导航">
        <RouterLink to="/library">学习目录</RouterLink>
        <RouterLink :to="parentRoute">家长</RouterLink>
      </nav>
    </header>

    <section class="greeting">
      <CompanionMouse class="portrait" :state="screen?.step === 'DONE' ? 'cheer' : mouseState" :size="88" />
      <div>
        <h1>{{ screen && showGuide ? '和豆豆一起学' : '你想学什么？' }}</h1>
        <p v-if="!screen || !showGuide">我是豆豆。告诉我你想学什么，或选一个主题一起出发。</p>
      </div>
    </section>

    <section v-if="showGuide && loading" class="status" role="status">豆豆在找上次的学习进度…</section>
    <div v-if="error && showGuide" class="status" role="alert">
      <p>{{ error }}</p><AppButton v-if="!screen" variant="ghost" @click="guide.load()">重试</AppButton>
    </div>

    <GuidedStep v-if="screen && showGuide" :screen="screen" :pending="pending" :playing="playing"
      :audio-error="audioError" @act="guide.act" @listen="playing ? guide.stop() : guide.listen()" @book="openBook" />

    <section v-else-if="!loading && !result && !awaitingSpeech && !error" class="feature">
      <img :src="trainCover" alt="蓝色火车带着苹果准备出发" />
      <div class="feature-copy">
        <h2>火车探险</h2>
        <p>看图找车头，再陪苹果坐一站小火车。</p>
        <AppButton size="lg" :disabled="pending || phase === 'recording'" @click="chooseTrain">
          {{ screen ? '继续探险' : '出发吧' }}
        </AppButton>
      </div>
    </section>

    <section v-if="!showGuide" class="reply" aria-label="豆豆的回答">
      <p v-if="chatError" role="alert">{{ chatError.message }}</p>
      <p v-else-if="phase === 'recording'" role="status">豆豆在听你说…</p>
      <p v-else-if="awaitingSpeech" role="status">{{ phase === 'recognizing' ? '豆豆在听清楚…' : '豆豆在想…' }}</p>
      <template v-else-if="result">
        <p class="reply-speech" role="status">{{ result.speech }}</p>
        <AppButton v-if="needsPlayButton" variant="ghost" @click="companion.replay()">听豆豆说</AppButton>
        <CompanionCandidateCard v-for="candidate in result.candidates" :key="candidate.lessonId ?? candidate.route"
          :candidate="candidate" @go="companion.go" />
        <CompanionExplainCard v-if="result.explain" :explain="result.explain" :quiz="result.quiz"
          :state="quiz" :learn-more="result.learnMore" @answer="companion.answer" @go="companion.go" />
        <AppButton variant="ghost" @click="chooseTrain">{{ screen ? '回到火车探险' : '试试火车探险' }}</AppButton>
      </template>
    </section>

    <div class="prompt-area">
      <div v-if="!screen && topics.length && !awaitingSpeech" class="topics" aria-label="想学的主题">
        <AppButton v-for="topic in topics.filter(t => !t.name.includes('火车')).slice(0, 3)"
          :key="topic.name" variant="ghost" :disabled="pending || phase === 'recording'" @click="send(topic.name)">
          {{ topic.name }}
        </AppButton>
      </div>
      <AgentComposer :phase="phase" :mic="micMode !== 'hidden'"
        :prompt="showGuide && screen?.options.length ? '告诉豆豆你的答案' : '告诉豆豆你想学什么'"
        :disabled="loading || pending || awaitingSpeech" @send="send" @record="startRecording" @stop="companion.stopRecording" />
    </div>
  </main>
</template>

<style scoped>
.agent-home { min-height: 100dvh; max-width: 1080px; margin: auto; padding: 0 clamp(16px, 6vw, 72px) var(--space-6); }
.top { display: flex; justify-content: space-between; align-items: center; min-height: 68px; gap: var(--space-2); border-bottom: 1px solid var(--border-light); }
.wordmark { display: flex; align-items: center; gap: var(--space-2); font-size: var(--text-lg); font-weight: var(--font-bold); }
.top nav { display: flex; gap: clamp(12px, 4vw, 28px); }
.top a { display: inline-flex; align-items: center; min-height: var(--touch-target); color: var(--text-primary); font-size: var(--text-sm); text-decoration: underline; text-underline-offset: .24em; }
.greeting { display: flex; align-items: center; gap: var(--space-4); padding: clamp(22px, 6vw, 52px) 0 var(--space-5); }
.greeting h1 { font-size: clamp(30px, 5vw, 54px); line-height: 1.15; letter-spacing: -.04em; }
.greeting p, .feature p { margin-top: var(--space-2); color: var(--text-secondary); line-height: 1.65; }
.feature { display: grid; grid-template-columns: 1.2fr 1fr; overflow: hidden; border-radius: var(--radius-lg); background: var(--bg-card); box-shadow: var(--shadow-card); }
.feature > img { width: 100%; height: 100%; min-height: 225px; object-fit: cover; }
.feature-copy { align-self: center; padding: clamp(16px, 4vw, 36px); }
.feature-copy h2 { font-size: clamp(24px, 4vw, 38px); line-height: 1.2; }
.feature-copy .app-btn { margin-top: var(--space-4); }
.topics { display: flex; flex-wrap: wrap; gap: var(--space-1); }
.topics :deep(.app-btn) { background: var(--bg-card); padding-inline: var(--space-3); }
.prompt-area, .reply { display: grid; gap: var(--space-3); }
.prompt-area { margin-top: var(--space-6); }
.reply { margin-top: var(--space-3); }
.reply-speech, .status { padding: var(--space-3) var(--space-4); border-radius: var(--radius-md); background: var(--bg-card); }
@media (max-width: 620px) {
  .feature { grid-template-columns: 1fr; }
  .feature > img { height: auto; aspect-ratio: 1.9; min-height: 0; }
  .feature-copy .app-btn { width: 100%; }
  .greeting .portrait { width: 64px !important; height: 64px !important; }
}
</style>
