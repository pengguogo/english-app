<!--
  RecordButton.vue - 录音按钮组件
  用途:点击开始录音,再次点击停止录音并通过 recorded 事件抛出 WAV 音频 Blob,
       面向儿童英语跟读学习场景,配合后端百度语音评测使用。
  作者:english-app
  创建日期:2026-07-20
-->
<script setup>
import { ref } from 'vue'
import { useRecorder } from '../composables/useRecorder'

// emit 事件:
// - recorded: 录音完成后抛出 WAV Blob(16kHz / 16bit / mono)
const emit = defineEmits(['recorded'])

// 复用录音组合式函数,获取录音状态与启停方法
const { isRecording, start, stop } = useRecorder()

// 错误提示信息(权限拒绝、编码失败等场景)
const errorMessage = ref('')

/**
 * 按钮点击处理:切换录音状态。
 * 未录音时启动录音;录音中时停止并向父组件抛出录音结果。
 */
async function handleClick() {
  errorMessage.value = ''
  if (!isRecording.value) {
    try {
      await start()
    } catch (e) {
      // 用户拒绝麦克风权限或设备不可用时,给出儿童可理解的友好提示
      errorMessage.value = '请允许麦克风权限才能跟读'
      console.error('启动录音失败:', e)
    }
  } else {
    // stop 编码失败时返回 null,提示用户重试而非静默丢失
    const wavBlob = await stop()
    if (wavBlob) {
      emit('recorded', wavBlob)
    } else {
      errorMessage.value = '录音处理失败,请重试'
    }
  }
}
</script>

<template>
  <div class="record-wrap">
    <button
      class="record-btn"
      :class="{ recording: isRecording }"
      @click="handleClick"
    >
      <span v-if="!isRecording">🎤 跟读</span>
      <span v-else>⏹ 停止</span>
    </button>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<style scoped>
/* 面向儿童的橙色圆角按钮,录音中变红并脉冲呼吸,直观反馈录音状态 */
.record-btn {
  padding: 12px 24px;
  font-size: 1rem;
  border: none;
  border-radius: 24px;
  background: #ff9800;
  color: white;
  cursor: pointer;
  transition: background 0.2s;
}
.record-btn:hover {
  background: #f57c00;
}
.record-btn.recording {
  background: #f44336;
  animation: pulse 1s infinite;
}
.error {
  color: #f44336;
  font-size: 0.85rem;
  margin-top: 8px;
}
@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}
</style>
