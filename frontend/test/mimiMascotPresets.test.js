import test from 'node:test'
import assert from 'node:assert/strict'
import { getMimiPreset } from '../src/utils/mimiMascotPresets.js'

test('should_returnLivelyMotions_when_variantIsWelcome', () => {
  const preset = getMimiPreset('welcome')
  assert.equal(preset.motion, 'float')
  assert.equal(preset.secondaryMotion, 'sway')
})

test('should_returnGentleMotions_when_variantIsCompanion', () => {
  const preset = getMimiPreset('companion')
  assert.equal(preset.motion, 'breathe')
  assert.equal(preset.secondaryMotion, 'nod')
})

test('should_returnMostCheerfulMotions_when_variantIsCelebrate', () => {
  const preset = getMimiPreset('celebrate')
  assert.equal(preset.motion, 'bounce')
  assert.equal(preset.secondaryMotion, 'wiggle')
  assert.equal(preset.auraMotion, 'pulse')
  assert.equal(preset.motionStyle, 'celebrate-excited')
})

test('should_returnMinimalMotion_when_variantIsAvatar', () => {
  const preset = getMimiPreset('avatar')
  assert.equal(preset.motion, 'breathe')
  assert.equal(preset.secondaryMotion, 'none')
})

test('should_returnLightFloat_when_variantIsEmpty', () => {
  const preset = getMimiPreset('empty')
  assert.equal(preset.motion, 'float')
  assert.equal(preset.secondaryMotion, 'sway')
})

test('should_fallbackToBreathe_when_variantIsUnknown', () => {
  const preset = getMimiPreset('unknown')
  assert.equal(preset.motion, 'breathe')
  assert.equal(preset.secondaryMotion, 'nod')
})
