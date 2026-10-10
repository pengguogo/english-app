<script setup>
import AppButton from '../AppButton.vue'
import AudioButton from '../AudioButton.vue'
import RecordButton from '../RecordButton.vue'
import { useCharacterPractice } from '../../composables/useCharacterPractice'

const props = defineProps({
  currentItem: { type: Object, required: true },
  currentIndex: { type: Number, required: true },
  lessonId: { type: Number, required: true },
  items: { type: Array, required: true },
  totalItems: { type: Number, default: 4 },
  isLastItem: Boolean,
  reviewOnly: Boolean
})
const emit = defineEmits(['next', 'answered', 'recorded'])
const { phase, choices, hint, answered, heard, saved, saving, error, feedback, showPinyin,
  title, isLearn, isImage, isContext, needsAudio, audioText, revealHint, choose, advance, save } = useCharacterPractice(props, emit)
</script>

<template>
  <section class="character-lesson" aria-label="汉字认读练习">
    <p>第 {{ currentIndex + 1 }} 项 · {{ reviewOnly ? '每日复习' : `第 ${phase + 1} / 5 步` }}</p>
    <h2>{{ title }}</h2>
    <template v-if="isLearn">
      <img :src="currentItem.image" alt="汉字含义配图" />
      <div class="big-character">{{ currentItem.word }}</div>
      <AppButton variant="ghost" @click="showPinyin = !showPinyin">{{ showPinyin ? '收起拼音' : '看看拼音' }}</AppButton>
      <p v-if="showPinyin">{{ currentItem.phonetic }}</p>
      <p>{{ currentItem.exampleWord }} · {{ currentItem.exampleSentence }}</p>
      <AudioButton :text="currentItem.exampleSentence" lan="zh" />
      <RecordButton @recorded="blob => emit('recorded', blob)" />
      <p>跟读可以练发音；下面的找字练习才会记录认字结果。</p>
    </template>
    <div v-if="isImage" class="big-character">{{ currentItem.word }}</div>
    <AudioButton :text="audioText" lan="zh" @played="heard = true" />
    <p v-if="needsAudio && !heard">先点喇叭听题，再找字。</p>
    <div v-if="!isLearn && !isContext" class="choices">
      <AppButton v-for="(choice, index) in choices" :key="choice.word" variant="ghost"
        :aria-label="isImage ? `图片选项 ${index + 1}` : choice.word"
        :disabled="answered || saving || (needsAudio && !heard)" @click="choose(choice.word)">
        <img v-if="isImage" :src="choice.image" :alt="`图片选项 ${index + 1}`" />
        <span v-else class="choice-character">{{ choice.word }}</span>
      </AppButton>
    </div>
    <template v-if="isContext">
      <p>点句子里的目标字，也可以先听题。</p>
      <div class="sentence">
        <template v-for="(char, index) in [...currentItem.exampleSentence]" :key="index">
          <AppButton v-if="/\p{Script=Han}/u.test(char)" variant="ghost"
            :disabled="answered || saving" @click="choose(char)">{{ char }}</AppButton>
          <span v-else>{{ char }}</span>
        </template>
      </div>
    </template>
    <AppButton v-if="!isLearn && !answered" variant="ghost" @click="revealHint">看看提示</AppButton>
    <div v-if="hint" class="hint">
      <img :src="currentItem.image" alt="提示配图" />
      <span class="big-character">{{ currentItem.word }}</span>
      <p>{{ currentItem.exampleWord }}</p>
    </div>
    <p aria-live="polite">{{ feedback }}</p>
    <p v-if="error" role="alert">{{ error }}</p>
    <div class="actions">
      <AppButton :disabled="saving || (!isLearn && !answered && !saved)" @click="advance">
        {{ saving ? '保存中…' : saved ? (isLastItem ? '完成认读' : '下一个字') : isLearn ? '开始找字' : error ? '重试保存' : '继续' }}
      </AppButton>
      <AppButton v-if="!saved" variant="ghost" :disabled="saving" @click="save(true)">记为待巩固</AppButton>
    </div>
  </section>
</template>

<style scoped>
.character-lesson { display: grid; gap: var(--space-4); text-align: center; max-width: 560px; margin: auto; color: var(--text-primary); }
.character-lesson > img, .hint > img { width: 180px; height: 180px; object-fit: contain; margin: auto; border-radius: var(--radius-lg); }
.big-character { font-size: 5rem; line-height: 1.3; font-weight: var(--font-bold); }
.choices { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: var(--space-2); }
.choices :deep(.app-btn) { min-width: 0; padding: var(--space-2); }
.choices img { width: 100%; max-width: 130px; aspect-ratio: 1; object-fit: contain; }
.choice-character { font-size: 3rem; }
.sentence, .actions { display: flex; flex-wrap: wrap; justify-content: center; gap: var(--space-2); }
.sentence :deep(.app-btn) { font-size: var(--text-xl); }
.hint { padding: var(--space-3); background: var(--bg-muted); border-radius: var(--radius-lg); }
</style>
