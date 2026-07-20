<!--
  RecordButton.vue - 录音按钮组件
  用途: 点击开始录音,再次点击停止录音并通过 recorded 事件抛出 WAV 音频 Blob,
       面向儿童英语跟读学习场景,配合后端百度语音评测使用。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
import { ref } from 'vue'
import { useRecorder } from '../composables/useRecorder'

/**
 * 组件 emits:
 * @event recorded - 录音完成后抛出 WAV Blob(16kHz / 16bit / mono)
 */
const emit = defineEmits(['recorded'])

/* 复用录音组合式函数,获取录音状态与启停方法 */
const { isRecording, start, stop } = useRecorder()

/* 错误提示信息(权限拒绝、编码失败等场景) */
const errorMessage = ref('')

/**
 * 按钮点击处理: 切换录音状态。
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
      <!-- 麦克风 SVG 图标(未录音) -->
      <svg v-if="!isRecording" class="icon-mic" width="20" height="20" viewBox="0 0 24 24" fill="none">
        <rect x="9" y="2" width="6" height="12" rx="3" fill="currentColor"/>
        <path d="M5 11C5 15 8 17 12 17C16 17 19 15 19 11" stroke="currentColor"
              stroke-width="2" stroke-linecap="round" fill="none"/>
        <line x1="12" y1="17" x2="12" y2="21" stroke="currentColor"
              stroke-width="2" stroke-linecap="round"/>
      </svg>
      <!-- 停止 SVG 图标(录音中) -->
      <svg v-else class="icon-stop" width="20" height="20" viewBox="0 0 24 24" fill="none">
        <rect x="6" y="6" width="12" height="12" rx="3" fill="currentColor"/>
      </svg>
      <span>{{ isRecording ? '停止' : '跟读' }}</span>
      <!-- 录音波形动画 -->
      <div v-if="isRecording" class="waveform">
        <span></span><span></span><span></span>
      </div>
    </button>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<style scoped>
/* 面向儿童的橙色胶囊按钮 */
.record-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
  background: var(--color-orange);
  color: white;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
  transition: transform var(--duration-fast) var(--ease-bounce),
              background var(--duration-fast) var(--ease-smooth);
}

.record-btn:not(:disabled):hover {
  background: var(--color-orange-hover);
}

.record-btn:not(:disabled):active {
  transform: scale(0.96);
}

/* 录音中: 变红 + 呼吸动效 */
.record-btn.recording {
  background: var(--color-warning);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

@media (prefers-reduced-motion: no-preference) {
  .record-btn.recording {
    animation: pulse 1s infinite;
  }
}

/* 波形动画条 */
.waveform {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  margin-left: var(--space-1);
}
.waveform span {
  display: block;
  width: 3px;
  height: 16px;
  background: white;
  border-radius: var(--radius-pill);
}

@media (prefers-reduced-motion: no-preference) {
  .waveform span:nth-child(1) { animation: wave 0.6s ease-in-out infinite; }
  .waveform span:nth-child(2) { animation: wave 0.6s ease-in-out infinite 0.2s; }
  .waveform span:nth-child(3) { animation: wave 0.6s ease-in-out infinite 0.4s; }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

@keyframes wave {
  0%, 100% { height: 8px; }
  50% { height: 20px; }
}

.error {
  color: var(--color-warning);
  font-size: var(--text-sm);
  margin-top: var(--space-2);
  text-align: center;
}
</style>
