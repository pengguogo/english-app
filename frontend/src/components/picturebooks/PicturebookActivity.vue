<script setup>
import { computed, ref } from 'vue'
import AppButton from '../AppButton.vue'
import { usePicturebookAudio } from '../../composables/usePicturebookAudio'
const props = defineProps({ item: { type: Object, required: true }, quiz: Boolean, interactive: Boolean, last: Boolean, first: Boolean, saving: Boolean })
const emit = defineEmits(['next', 'prev', 'answer'])
const { speak, stop, speaking, audioError } = usePicturebookAudio()
const translated = ref(false)
const ready = ref(false)
const tried = ref(false)
const feedback = ref('')
const selected = ref(-1)
const imageFailed = ref(false)
const failedOptions = ref({})
const action = computed(() => props.item.trainAction)
const canContinue = computed(() => ready.value || (!props.quiz && !action.value))
const english = computed(() => props.quiz ? props.item.audioText : props.item.content)
function choose(index) {
  if (ready.value) return
  stop()
  selected.value = index
  if (index === props.item.answer) {
    ready.value = true
    feedback.value = '货物装对啦！小火车可以前进了。'
    emit('answer', tried.value ? 70 : 100)
  } else {
    tried.value = true
    feedback.value = props.item.hint || '再听一次，换一节车厢试试。'
  }
}
function trainAction(value) {
  stop()
  if (action.value === 'signal' && value !== 'go') {
    feedback.value = 'Stop 是停下。站长刚刚说 Go，再试试“发车”！'
    return
  }
  ready.value = true
  feedback.value = action.value === 'ticket' ? '检票成功！这趟列车的小司机就是你。' : action.value === 'signal' ? '发车！下一站，故事里的小溪。' : '词语装车成功！谢谢小站长的广播。'
}
function navigate(event) { stop(); emit(event) }
</script>

<template>
  <section class="activity">
    <div class="page-picture" :class="{ landscape: action === 'ticket' || action === 'signal' }">
      <img v-if="!imageFailed" :src="item.image" :alt="quiz ? '本题场景图' : item.title" @error="imageFailed = true" />
      <p v-else role="status">图片暂时没到站，可以听广播或继续阅读。</p>
    </div>
    <div class="page-content">
      <span class="activity-label">{{ quiz ? '站长挑战 · 听一听，选一选' : action ? '小司机任务' : '原绘本 · 看图听故事' }}</span>
      <h2>{{ quiz ? item.question : item.title }}</h2>
      <p v-if="!quiz" class="english" lang="en">{{ item.content }}</p>
      <div class="audio-actions">
        <AppButton variant="success" @click="speaking ? stop() : speak(english)">{{ speaking ? '停止广播' : quiz ? '听英文问题' : '听英文 / 再听一次' }}</AppButton>
        <AppButton v-if="!quiz" variant="ghost" :aria-expanded="translated" @click="translated = !translated">{{ translated ? '收起中文' : '看看中文' }}</AppButton>
        <AppButton v-if="item.announcement" variant="ghost" @click="speak(item.announcement, 'zh')">听到站广播</AppButton>
      </div>
      <p v-if="translated && !quiz" class="translation">{{ item.translation }}</p>
      <p v-if="audioError" role="status" class="notice">{{ audioError }}</p>

      <div v-if="quiz" class="options">
        <AppButton v-for="(option, position) in item.options" :key="position" class="answer-option" :variant="ready && position === item.answer ? 'success' : 'ghost'" :disabled="ready || saving" :aria-pressed="selected === position" @click="choose(position)">
          <img v-if="option.image && !failedOptions[position]" :src="option.image" :alt="option.text" @error="failedOptions[position] = true" />
          <span v-if="typeof option === 'string' || !option.image || failedOptions[position]">{{ typeof option === 'string' ? option : option.text }}</span>
          <span v-else class="option-label">{{ position + 1 }} 号车厢</span>
        </AppButton>
      </div>
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
        <AppButton variant="ghost" @click="speak(item.prompt, 'zh')">听站长提问</AppButton>
        <span>说给家长听，也可以带着猜想继续看。</span>
      </aside>
      <p v-if="feedback" class="feedback" role="status">{{ feedback }}</p>
      <AppButton v-if="quiz && tried && !ready" variant="ghost" @click="speak(item.hint, 'zh')">听中文提示</AppButton>
      <nav class="page-navigation" aria-label="绘本翻页">
        <AppButton variant="ghost" :disabled="first || saving" @click="navigate('prev')">上一站</AppButton>
        <AppButton :disabled="!canContinue || saving" @click="navigate('next')">{{ saving ? '正在保存车票…' : last ? '到站盖章' : '前进一站 →' }}</AppButton>
      </nav>
    </div>
  </section>
</template>

<style scoped>
.activity { display: grid; grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); gap: var(--space-6); align-items: start; }
.page-picture { padding: var(--space-2); background: var(--bg-card); border-radius: var(--radius-md); border: 1px solid var(--border-light); }
.page-picture img { display: block; width: 100%; max-height: 640px; object-fit: contain; border-radius: var(--radius-sm); }
.page-content { padding: var(--space-4) 0; min-width: 0; }
.activity-label { font-size: var(--text-xs); color: var(--text-secondary); letter-spacing: .05em; }
h2 { font-size: var(--text-lg); line-height: 1.4; margin: var(--space-3) 0; color: var(--text-primary); }
.english { font-size: clamp(22px, 3vw, 30px); line-height: 1.65; color: var(--text-primary); margin-bottom: var(--space-5); }
.audio-actions { display: flex; flex-wrap: wrap; gap: var(--space-2); }
.translation, .notice { color: var(--text-secondary); margin-top: var(--space-3); line-height: 1.7; }
.options { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-3); margin-top: var(--space-5); }
.options .answer-option { flex-direction: column; border: 2px solid var(--border-light); border-radius: var(--radius-md); padding: var(--space-2); white-space: normal; min-height: 72px; }
.answer-option img { width: 100%; max-height: 190px; object-fit: contain; }
.option-label { font-size: var(--text-sm); }
.train-controls, .station-prompt { padding: var(--space-4); margin-top: var(--space-5); border-radius: var(--radius-md); background: var(--gradient-warm); }
.train-controls p, .station-prompt p { color: var(--text-primary); line-height: 1.7; margin-bottom: var(--space-3); }
.station-prompt span { display: block; font-size: var(--text-sm); color: var(--text-secondary); margin-top: var(--space-2); }
.ticket-button { border: 2px dashed var(--text-primary); }
.feedback { padding: var(--space-3); background: var(--bg-muted); color: var(--text-primary); border-radius: var(--radius-sm); line-height: 1.6; margin-top: var(--space-4); }
.page-navigation { display: flex; justify-content: space-between; gap: var(--space-2); margin-top: var(--space-6); }
@media(max-width: 700px) { .activity { grid-template-columns: 1fr; gap: var(--space-2); } .page-picture img { max-height: 48vh; } .landscape img { max-height: 220px; } .page-content { padding: var(--space-2) 0; } }
</style>
