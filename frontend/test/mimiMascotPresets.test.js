import test from 'node:test'
import assert from 'node:assert/strict'
import { getMimiPreset } from '../src/utils/mimiMascotPresets.js'

test('should_returnFloat_when_variantIsWelcome', () => {
  assert.equal(getMimiPreset('welcome').motion, 'float')
})

test('should_returnBreathe_when_variantIsCompanion', () => {
  assert.equal(getMimiPreset('companion').motion, 'breathe')
})

test('should_returnCelebrate_when_variantIsCelebrate', () => {
  assert.equal(getMimiPreset('celebrate').motion, 'celebrate')
})

test('should_fallbackToBreathe_when_variantIsUnknown', () => {
  assert.equal(getMimiPreset('unknown').motion, 'breathe')
})

