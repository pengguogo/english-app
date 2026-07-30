import test from 'node:test'
import assert from 'node:assert/strict'
import { normalizeRouterBase } from '../src/utils/routerBase.js'

test('should_keepAppBase_when_baseUrlIsAppPath', () => {
  assert.equal(normalizeRouterBase('/app/'), '/app/')
})

test('should_fallbackToRoot_when_baseUrlIsEmpty', () => {
  assert.equal(normalizeRouterBase(''), '/')
})

