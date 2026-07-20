<!--
  AudioButton.vue - 发音播放按钮组件
  用途:点击后调用 useTts 播放传入文本的英文发音,面向儿童的英语学习场景。
  作者:english-app
  创建日期:2026-07-20
-->
<script setup>
import { useTts } from '../composables/useTts'

// 组件 props:
// - text: 需要播放发音的英文文本(必填)
const props = defineProps({
  text: { type: String, required: true }
})

// 复用 TTS 组合式函数,获取 loading 状态与播放方法
const { isLoading, play } = useTts()
</script>

<template>
  <button
    class="audio-btn"
    :disabled="isLoading"
    @click="play(props.text)"
  >
    <span v-if="!isLoading">🔊 听发音</span>
    <span v-else>加载中...</span>
  </button>
</template>

<style scoped>
/* 面向儿童的绿色圆角按钮,尺寸适中便于点击 */
.audio-btn {
  padding: 12px 24px;
  font-size: 1rem;
  border: none;
  border-radius: 24px;
  background: #4CAF50;
  color: white;
  cursor: pointer;
  transition: background 0.2s;
}
.audio-btn:hover { background: #43A047; }
.audio-btn:disabled { background: #9E9E9E; cursor: not-allowed; }
</style>
