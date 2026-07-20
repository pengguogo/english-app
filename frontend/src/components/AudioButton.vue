<!--
  AudioButton.vue - 发音播放按钮组件
  用途: 点击后先播放中文翻译,再播放英文单词/句子。面向儿童的英语学习场景。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
import { useTts } from '../composables/useTts'

/**
 * 组件 props:
 * @param {String} text - 需要播放发音的英文文本(必填)
 * @param {String} translation - 中文翻译文本(可选),传入时先播放中文再播放英文
 */
const props = defineProps({
  text: { type: String, required: true },
  translation: { type: String, default: '' }
})

/* 复用 TTS 组合式函数,获取 loading 状态与播放方法 */
const { isLoading, playAndWait } = useTts()

/**
 * 点击播放:先播放中文翻译(如果有),播完后再播放英文。
 * 两个音频顺序播放,播放期间按钮显示"播放中..."并禁用。
 */
async function handlePlay() {
  if (isLoading.value) return
  try {
    // 如果有中文翻译,先播放中文
    if (props.translation) {
      await playAndWait(props.translation, 'zh')
    }
    // 再播放英文
    await playAndWait(props.text, 'en')
  } catch (e) {
    console.error('TTS 播放失败:', e)
    alert('发音加载失败,请重试')
  }
}
</script>

<template>
  <button
    class="audio-btn"
    :class="{ loading: isLoading }"
    :disabled="isLoading"
    @click="handlePlay"
  >
    <!-- 扬声器 SVG 图标 -->
    <svg class="icon-speaker" width="20" height="20" viewBox="0 0 24 24" fill="none">
      <path d="M11 5L6 9H2V15H6L11 19V5Z" fill="currentColor"/>
      <path d="M15.5 8.5C16.5 9.5 17 11 17 12C17 13 16.5 14.5 15.5 15.5"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" fill="none"/>
      <path d="M18 6C19.5 7.5 20.5 9.5 20.5 12C20.5 14.5 19.5 16.5 18 18"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" fill="none"
            class="wave wave-1"/>
    </svg>
    <span>{{ isLoading ? '播放中...' : '听发音' }}</span>
  </button>
</template>

<style scoped>
/* 面向儿童的成功色胶囊按钮 */
.audio-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
  background: var(--color-success);
  color: white;
  box-shadow: 0 4px 12px rgba(76, 217, 100, 0.3);
  transition: transform var(--duration-fast) var(--ease-bounce),
              filter var(--duration-fast) var(--ease-smooth);
}

.audio-btn:not(:disabled):hover {
  filter: brightness(1.05);
}

.audio-btn:not(:disabled):active {
  transform: scale(0.96);
}

.audio-btn:disabled {
  background: var(--text-tertiary);
  cursor: not-allowed;
  opacity: 0.6;
  box-shadow: none;
}

/* 声波动画: 播放时波纹脉冲扩散 */
@media (prefers-reduced-motion: no-preference) {
  .audio-btn:not(.loading) .wave {
    animation: wavePulse 1.5s ease-in-out infinite;
  }
  .audio-btn:not(.loading) .wave-1 {
    animation-delay: 0.2s;
  }
}

@keyframes wavePulse {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}
</style>
