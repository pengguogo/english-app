import test from 'node:test'
import assert from 'node:assert/strict'
import { createPicturebookPlayback } from '../src/utils/picturebookPlayback.js'

test('带读按中文再英文顺序播放', async () => {
  const played = []
  const playback = createPicturebookPlayback({
    playAndWait: async (text, language) => played.push(`${language}:${text}`),
    stopTts() {},
    onState() {},
    device: {},
  })

  assert.equal(await playback.speakSequence([
    { text: '看，小海龟。', language: 'zh' },
    { text: 'A little turtle.', language: 'en' },
  ]), true)
  assert.deepEqual(played, ['zh:看，小海龟。', 'en:A little turtle.'])
})

test('停止后不再播放序列中的下一句', async () => {
  const played = []
  let release
  const playback = createPicturebookPlayback({
    playAndWait: (text) => { played.push(text); return new Promise(resolve => { release = resolve }) },
    stopTts() {},
    onState() {},
    device: {},
  })

  const task = playback.speakSequence([{ text: '第一句' }, { text: '第二句' }])
  playback.stop()
  release()
  assert.equal(await task, false)
  assert.deepEqual(played, ['第一句'])
})

test('服务语音失败时等待设备语音结束后再播下一句', async () => {
  const spoken = []
  const device = {
    SpeechSynthesisUtterance: class { constructor(text) { this.text = text } },
    speechSynthesis: {
      speak(utterance) { spoken.push(utterance.text); queueMicrotask(() => utterance.onend()) },
      cancel() {},
    },
  }
  const playback = createPicturebookPlayback({
    playAndWait: async () => { throw new Error('服务不可用') },
    stopTts() {},
    onState() {},
    device,
  })

  assert.equal(await playback.speakSequence([{ text: '你好', language: 'zh' }, { text: 'Hello', language: 'en' }]), true)
  assert.deepEqual(spoken, ['你好', 'Hello'])
})
