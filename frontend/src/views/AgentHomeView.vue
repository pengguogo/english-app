<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppButton from '../components/AppButton.vue'
import AgentComposer from '../components/agent/AgentComposer.vue'
import GuidedStep from '../components/agent/GuidedStep.vue'
import CompanionMouse from '../components/companion/CompanionMouse.vue'
import CompanionPet from '../components/companion/CompanionPet.vue'
import CompanionExplainCard from '../components/companion/CompanionExplainCard.vue'
import CompanionCandidateCard from '../components/companion/CompanionCandidateCard.vue'
import { useAuthStore } from '../stores/auth'
import { useCompanion } from '../composables/useCompanion'
import { useGuidedLearning } from '../composables/useGuidedLearning'

const trainCover = '/images/story/railway-logistics-06.jpg'
const router = useRouter()
const route = useRoute()
const petPreview = computed(() => route.query['pet-preview'] === '1')
const auth = useAuthStore()
const showGuide = ref(true)
const guide = useGuidedLearning()
const { screen, loading, pending, error, audioError, playing, reaction } = guide
const companion = useCompanion({ auth, router, onIntent: handleIntent })
const { result, quiz, phase, mouseState, canReplay, topics } = companion
const awaitingSpeech = computed(() => ['openingMic', 'recognizing', 'thinking'].includes(phase.value))
const voiceStatus = computed(() => ({
  openingMic: '正在打开麦克风…',
  recording: '豆豆在听你说…',
  recognizing: '豆豆在听清楚…',
  thinking: '豆豆在想…',
  speaking: '豆豆在说…'
})[phase.value])
const inputLocked = computed(() => awaitingSpeech.value || phase.value === 'recording')
const portraitState = computed(() => {
  if (inputLocked.value || chatError.value || !showGuide.value) return mouseState.value
  if (reaction.value) return reaction.value
  if (pending.value) return 'thinking'
  if (playing.value) return 'speaking'
  return 'idle'
})
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
  if (!/^(豆豆)?(我想|我想要|想|我要|可以|我们|一起|请)?(看|听|学|了解|去看|去学|找|讲|探索|开始)?(一下|一看|一听|关于)?(小?火车)(的?探险|的?故事)?(吗|吧|呀|啦)?$/.test(normalized)) {
    showGuide.value = false
    return false
  }
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
  void companion.submitText(text)
}

function startRecording() {
  guide.stop()
  void companion.startRecording()
}

function openBook() {
  const book = screen.value?.book
  if (!book) return
  guide.stop()
  router.push({ name: 'picturebook', params: { unitId: book.unitId },
    query: {
      lesson: book.lessonId, guide: screen.value.revision,
      'pet-preview': petPreview.value ? '1' : undefined
    } })
}

onMounted(async () => {
  void companion.openPanel()
  await guide.load()
  if (route.query.from === 'picturebook' && screen.value?.step === 'RECAP') {
    guide.react('wave')
    void router.replace({ query: petPreview.value ? { 'pet-preview': '1' } : {} })
  }
})
onBeforeUnmount(companion.closePanel)
</script>

<template>
  <main class="agent-home">
    <header class="top">
      <div class="wordmark"><CompanionMouse still :size="38" /><span>豆豆学习舱</span></div>
      <nav aria-label="学习导航">
        <RouterLink to="/library">学习目录</RouterLink>
        <RouterLink :to="parentRoute">家长</RouterLink>
      </nav>
    </header>

    <section class="greeting">
      <div class="greeting-copy">
        <h1>{{ screen && showGuide ? '和豆豆一起学' : '你想学什么？' }}</h1>
        <p v-if="!screen || !showGuide">我是领航员豆豆。选个任务，或者直接跟我说。</p>
      </div>
      <div class="pilot-window">
        <CompanionPet :state="portraitState" :preview="petPreview" />
      </div>
    </section>

    <section v-if="showGuide && loading" class="status" role="status">豆豆在找上次的学习进度…</section>
    <div v-if="error && showGuide" class="status" role="alert">
      <p>{{ error }}</p><AppButton v-if="!screen" variant="ghost" @click="guide.load()">重试</AppButton>
    </div>

    <GuidedStep v-if="screen && showGuide" :screen="screen" :pending="pending || inputLocked" :playing="playing"
      :audio-error="audioError" @act="guide.act" @listen="playing ? guide.stop() : guide.listen()" @book="openBook" />

    <section v-else-if="!loading && !result && !awaitingSpeech && !error" class="feature">
      <div class="task-window">
        <img :src="trainCover" alt="蓝色火车带着苹果准备出发" />
      </div>
      <div class="feature-copy">
        <h2>火车探险</h2>
        <p>看图找车头，再陪苹果坐一站小火车。</p>
        <AppButton size="lg" :disabled="pending || inputLocked" @click="chooseTrain">
          {{ screen ? '继续探险' : '开始探险' }}
        </AppButton>
      </div>
    </section>

    <section v-if="!showGuide" class="reply" aria-label="豆豆的回答">
      <template v-if="result && !inputLocked && !chatError">
        <p class="reply-speech" role="status">{{ result.speech }}</p>
        <AppButton v-if="canReplay" variant="ghost" @click="companion.replay()">听豆豆说</AppButton>
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
          :key="topic.name" variant="ghost" :disabled="pending || inputLocked" @click="send(topic.name)">
          {{ topic.name }}
        </AppButton>
      </div>
      <p v-if="chatError" class="status" role="alert">{{ chatError.message }}</p>
      <p v-else-if="voiceStatus" class="status" role="status">{{ voiceStatus }}</p>
      <AgentComposer :phase="phase" :retry="chatError?.kind === 'retry'"
        :prompt="showGuide && screen?.options.length ? '告诉豆豆你的答案' : '告诉豆豆你想学什么'"
        :disabled="loading || pending || awaitingSpeech" @send="send" @record="startRecording" @stop="companion.stopRecording" />
    </div>
  </main>
</template>

<style scoped src="./agentCabin.css"></style>
