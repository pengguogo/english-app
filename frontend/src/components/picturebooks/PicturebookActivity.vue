<script setup>
import { computed, onMounted, ref } from 'vue'
import AppButton from '../AppButton.vue'
import PicturebookOptions from './PicturebookOptions.vue'
import { usePicturebookAudio } from '../../composables/usePicturebookAudio'
const props = defineProps({ item: { type: Object, required: true }, quiz: Boolean, interactive: Boolean, autoRead: Boolean, last: Boolean, first: Boolean, saving: Boolean })
const emit = defineEmits(['next', 'prev', 'answer'])
const { speak, speakSequence, stop, speaking, audioError } = usePicturebookAudio()
const translated = ref(false)
const ready = ref(false)
const tried = ref(false)
const feedback = ref('')
const selected = ref(-1)
const imageFailed = ref(false)
const action = computed(() => props.item.trainAction)
const canContinue = computed(() => ready.value || (!props.quiz && !action.value))
const english = computed(() => props.quiz ? props.item.audioText : props.item.content)
const narration = computed(() => props.quiz
  ? [{ text: props.item.question, language: 'zh' }, { text: english.value, language: 'en' }]
  : [{ text: props.item.translation, language: 'zh' }, { text: english.value, language: 'en' }])
function choose(index) {
  if (ready.value) return
  stop()
  selected.value = index
  if (index === props.item.answer) {
    ready.value = true
    feedback.value = '货物装对啦！小火车可以前进了。'
    emit('answer', tried.value ? 70 : 100)
    speak('答对啦！真会观察。', 'zh')
  } else {
    tried.value = true
    feedback.value = props.item.hint || '再听一次，换一节车厢试试。'
    speak(feedback.value, 'zh')
  }
}
function trainAction(value) {
  stop()
  if (action.value === 'signal' && value !== 'go') {
    feedback.value = 'Stop 是停下。站长刚刚说 Go，再试试“发车”！'
    speak(feedback.value, 'zh')
    return
  }
  ready.value = true
  feedback.value = action.value === 'ticket' ? '检票成功！这趟列车的小司机就是你。' : action.value === 'signal' ? '发车！下一站，故事里的小溪。' : '词语装车成功！谢谢小站长的广播。'
  speak(feedback.value, 'zh')
}
function navigate(event) { stop(); emit(event) }
function listenPage() { speaking.value ? stop() : speakSequence(narration.value) }
function listenOption(text) { speak(text, /[\u3400-\u9fff]/.test(text) ? 'zh' : 'en') }
onMounted(() => { if (props.autoRead) listenPage() })
</script>

<template>
  <section class="activity">
    <button class="page-picture" :class="{ landscape: action === 'ticket' || action === 'signal' }" type="button" aria-label="点图片听这一页" @click="listenPage">
      <img v-if="!imageFailed" :src="item.image" :alt="quiz ? '本题场景图' : item.title" @error="imageFailed = true" />
      <p v-else role="status">图片暂时没到站，可以听广播或继续阅读。</p>
      <span class="picture-speaker" aria-hidden="true">{{ speaking ? '⏹' : '🔊' }}</span>
    </button>
    <div class="page-content">
      <span class="activity-label">{{ quiz ? '站长挑战 · 听一听，选一选' : action ? '小司机任务' : '原绘本 · 看图听故事' }}</span>
      <h2>{{ quiz ? item.question : item.title }}</h2>
      <p v-if="!quiz" class="english" lang="en">{{ item.content }}</p>
      <div class="audio-actions">
        <AppButton size="lg" variant="success" :aria-label="speaking ? '停止声音' : '播放这一页'" @click="listenPage"><span aria-hidden="true">{{ speaking ? '⏹' : '🔊' }}</span>{{ speaking ? '停一下' : '听故事' }}</AppButton>
        <AppButton v-if="!quiz" variant="ghost" :aria-expanded="translated" @click="translated = !translated"><span aria-hidden="true">👪</span>{{ translated ? '收起家长文字' : '给家长看' }}</AppButton>
        <AppButton v-if="item.announcement" variant="ghost" @click="speak(item.announcement, 'zh')"><span aria-hidden="true">📣</span>听提示</AppButton>
      </div>
      <p v-if="translated && !quiz" class="translation">{{ item.translation }}</p>
      <p v-if="audioError" role="status" class="notice">{{ audioError }}</p>

      <PicturebookOptions v-if="quiz" :options="item.options" :ready="ready" :saving="saving" :selected="selected" :answer="item.answer" @choose="choose" @listen="listenOption" />
      <div v-else-if="action" class="train-controls">
        <template v-if="action === 'ticket'">
          <p>站长：“请小司机出示车票。”</p>
          <AppButton class="ticket-button" variant="warning" :disabled="ready" @click="trainAction('ticket')">{{ ready ? '已检票 ✓' : '点击车票 · TICKET' }}</AppButton>
        </template>
        <template v-else-if="action === 'signal'">
          <p>听一听 Go，选择对应的驾驶按钮。</p>
          <div class="audio-actions"><AppButton variant="warning" :disabled="ready" @click="trainAction('stop')">停车 · STOP</AppButton><AppButton variant="success" :disabled="ready" @click="trainAction('go')">发车 · GO</AppButton></div>
        </template>
        <template v-else>
          <p>听一遍，再试着说。也可以请家长带着一起读。</p>
          <AppButton variant="warning" :disabled="ready" @click="trainAction('speak')">{{ ready ? '词语已装车 ✓' : '我说好了，装进车厢' }}</AppButton>
        </template>
      </div>
      <aside v-if="interactive && item.prompt && !quiz" class="station-prompt">
        <strong>停靠一下 · 轮到你回答</strong><p>{{ item.prompt }}</p>
        <AppButton variant="ghost" @click="speak(item.prompt, 'zh')"><span aria-hidden="true">🔊</span>听问题</AppButton>
        <span>说给家长听，也可以带着猜想继续看。</span>
      </aside>
      <p v-if="feedback" class="feedback" role="status">{{ feedback }}</p>
      <AppButton v-if="quiz && tried && !ready" variant="ghost" @click="speak(item.hint, 'zh')">听中文提示</AppButton>
      <nav class="page-navigation" aria-label="绘本翻页">
        <AppButton size="lg" variant="ghost" :disabled="first || saving" aria-label="上一页" @click="navigate('prev')"><span class="nav-icon" aria-hidden="true">←</span>上一页</AppButton>
        <AppButton size="lg" :disabled="!canContinue || saving" :aria-label="last ? '读完并盖章' : '下一页'" @click="navigate('next')">{{ saving ? '保存中…' : last ? '完成 ✓' : '下一页' }}<span v-if="!last" class="nav-icon" aria-hidden="true">→</span></AppButton>
      </nav>
    </div>
  </section>
</template>

<style scoped>
.activity { display: grid; grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); gap: var(--space-6); align-items: start; }
.page-picture { position: relative; width: 100%; padding: var(--space-2); background: var(--bg-card); border-radius: var(--radius-md); border: 2px solid var(--border-light); cursor: pointer; }
.page-picture img { display: block; width: 100%; max-height: 640px; object-fit: contain; border-radius: var(--radius-sm); }
.picture-speaker { position: absolute; right: var(--space-4); bottom: var(--space-4); display: grid; place-items: center; width: 58px; height: 58px; border-radius: var(--radius-pill); background: var(--bg-card); box-shadow: var(--shadow-card); font-size: var(--text-lg); }
.page-content { padding: var(--space-4) 0; min-width: 0; }
.activity-label { font-size: var(--text-xs); color: var(--text-secondary); letter-spacing: .05em; }
h2 { font-size: var(--text-lg); line-height: 1.4; margin: var(--space-3) 0; color: var(--text-primary); }
.english { font-size: clamp(22px, 3vw, 30px); line-height: 1.65; color: var(--text-primary); margin-bottom: var(--space-5); }
.audio-actions { display: flex; flex-wrap: wrap; gap: var(--space-2); }
.translation, .notice { color: var(--text-secondary); margin-top: var(--space-3); line-height: 1.7; }
.train-controls, .station-prompt { padding: var(--space-4); margin-top: var(--space-5); border-radius: var(--radius-md); background: var(--gradient-warm); }
.train-controls p, .station-prompt p { color: var(--text-primary); line-height: 1.7; margin-bottom: var(--space-3); }
.station-prompt span { display: block; font-size: var(--text-sm); color: var(--text-secondary); margin-top: var(--space-2); }
.ticket-button { border: 2px dashed var(--text-primary); }
.feedback { padding: var(--space-3); background: var(--bg-muted); color: var(--text-primary); border-radius: var(--radius-sm); line-height: 1.6; margin-top: var(--space-4); }
.page-navigation { display: flex; justify-content: space-between; gap: var(--space-2); margin-top: var(--space-6); }
.nav-icon { font-size: var(--text-lg); }
@media(max-width: 700px) { .activity { grid-template-columns: 1fr; gap: var(--space-2); } .page-picture img { max-height: 48vh; } .landscape img { max-height: 220px; } .page-content { padding: var(--space-2) 0; } }
</style>
