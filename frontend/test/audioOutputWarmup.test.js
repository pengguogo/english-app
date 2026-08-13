import test from 'node:test'
import assert from 'node:assert/strict'
import {
  AUDIO_IDLE_THRESHOLD_MS,
  markAudioOutputActive,
  shouldWarmAudioOutput
} from '../src/utils/audioOutputWarmup.js'

test('长时间未播放时需要预热音频输出', () => {
  markAudioOutputActive(1000)
  assert.equal(shouldWarmAudioOutput(1000 + AUDIO_IDLE_THRESHOLD_MS + 1), true)
})

test('连续播放时不重复预热音频输出', () => {
  markAudioOutputActive(2000)
  assert.equal(shouldWarmAudioOutput(2000 + AUDIO_IDLE_THRESHOLD_MS), false)
})
