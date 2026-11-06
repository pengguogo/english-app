import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { computed, ref } from 'vue'

const source = readFileSync(new URL('../src/composables/useCompanion.js', import.meta.url), 'utf8')
  .replace(/^import .*\n/gm, '').replace(/^export /gm, '')

const httpError = (status, code) => Object.assign(new Error('x'), { response: { status, data: { code } } })
const tick = () => new Promise((r) => setImmediate(r))

/** 可控的 Promise,用来模拟“响应还没回来” */
function deferred() {
  let resolve, reject
  const promise = new Promise((res, rej) => { resolve = res; reject = rej })
  return { promise, resolve, reject }
}

function setup({ auth = { loggedIn: true, hasChild: true, consented: true }, api = {}, recorder = {}, tts = {}, env = {} } = {}) {
  const calls = { push: [], play: [], speak: [], abort: 0, recStop: 0, stopActive: 0, ttsStop: 0 }
  const timers = []
  const sandbox = {
    ref, computed, console,
    askGuide: api.askGuide || (async () => ({ type: 'RECOMMEND', speech: '去学水果吧', candidates: [], topics: [] })),
    fetchTopics: api.fetchTopics || (async () => [{ themeId: 1, name: '水果' }]),
    recognizeSpeech: api.recognizeSpeech || (async () => ({ text: '我想学水果' })),
    useRecorder: () => ({
      isRecording: ref(false),
      start: recorder.start || (async () => {}),
      stop: async () => { calls.recStop++; return recorder.stop ? recorder.stop() : { size: 10 } }
    }),
    useTts: () => ({
      play: async (t) => { calls.play.push(t) },
      playAndWait: tts.playAndWait || (async (t) => { calls.speak.push(t) }),
      stop() { calls.ttsStop++; tts.onStop?.() }
    }),
    stopActiveTts: () => { calls.stopActive++ },
    AbortController: class { constructor() { this.signal = {} } abort() { calls.abort++ } },
    setTimeout: (fn, ms) => { timers.push({ fn, ms }); return timers.length },
    clearTimeout: (id) => { if (timers[id - 1]) timers[id - 1].fn = null },
    isSecureContext: env.isSecureContext ?? true,
    navigator: env.navigator ?? { mediaDevices: { getUserMedia() {} } }
  }
  const api2 = runInNewContext(`${source}\n({ useCompanion, companionError })`, sandbox)
  const router = { push: (r) => calls.push.push(r) }
  const c = api2.useCompanion({ auth, router })
  const fire = (ms) => timers.filter((t) => t.ms === ms && t.fn).forEach((t) => { const f = t.fn; t.fn = null; f() })
  return { c, calls, fire, companionError: api2.companionError }
}

test('打字检索：思考中 → 播报中 → 等待选择，点了「去学」才跳转', async () => {
  const pending = deferred()
  const { c, calls } = setup({ api: { askGuide: () => pending.promise } })
  const run = c.submitText('  我想学水果  ')
  assert.equal(c.phase.value, 'thinking')
  pending.resolve({ type: 'RECOMMEND', speech: '去学水果吧', candidates: [{ title: '水果', route: '/lesson/12?themeId=3' }] })
  await run
  assert.deepEqual(calls.speak, ['去学水果吧'])
  assert.equal(c.phase.value, 'choosing')
  assert.equal(calls.push.length, 0)
  c.go(c.result.value.candidates[0])
  assert.deepEqual(calls.push, ['/lesson/12?themeId=3'])
  assert.equal(c.open.value, false)
})

test('录音 → 识别 → 检索，15 秒自动停止', async () => {
  const { c, calls, fire } = setup()
  await c.startRecording()
  assert.equal(c.phase.value, 'recording')
  assert.equal(c.mouseState.value, 'listening')
  fire(15000)
  await tick(); await tick(); await tick()
  assert.equal(calls.recStop, 1)
  assert.equal(c.phase.value, 'choosing')
  assert.deepEqual(calls.speak, ['去学水果吧'])
})

test('超过 2 秒显示思考动画并播“让我想一想”', async () => {
  const pending = deferred()
  const { c, calls, fire } = setup({ api: { askGuide: () => pending.promise } })
  const run = c.submitText('恐龙')
  assert.equal(c.slow.value, false)
  fire(2000)
  assert.equal(c.slow.value, true)
  assert.equal(c.mouseState.value, 'thinking')
  assert.deepEqual(calls.play, ['让我想一想'])
  pending.resolve({ type: 'NO_MATCH', speech: '换一个吧', candidates: [], topics: [{ name: '动物' }] })
  await run
  assert.equal(c.slow.value, false)
  assert.equal(c.result.value.topics[0].name, '动物')
})

test('关闭面板后丢弃过期响应，不播报、不改状态', async () => {
  const pending = deferred()
  const { c, calls } = setup({ api: { askGuide: () => pending.promise } })
  await c.openPanel()
  const run = c.submitText('水果')
  c.closePanel()
  assert.equal(calls.abort >= 1, true)
  pending.resolve({ type: 'RECOMMEND', speech: '旧的', candidates: [] })
  await run
  assert.equal(c.phase.value, 'idle')
  assert.equal(c.result.value, null)
  assert.deepEqual(calls.speak, [])
})

test('新一轮覆盖旧一轮：先发的慢响应被丢弃', async () => {
  const first = deferred()
  let n = 0
  const { c, calls } = setup({ api: { askGuide: () => (++n === 1 ? first.promise : Promise.resolve({ type: 'RECOMMEND', speech: '新的', candidates: [] })) } })
  const a = c.submitText('一')
  await c.submitText('二')
  first.resolve({ type: 'RECOMMEND', speech: '旧的', candidates: [] })
  await a
  assert.deepEqual(calls.speak, ['新的'])
})

test('错误码映射：429 / 4033 / 5031 / 4001', () => {
  const { companionError } = setup()
  assert.equal(companionError(httpError(429, 4292)).message, '今天聊够啦，明天再来')
  assert.equal(companionError(httpError(429, 4293)).message, '今天聊够啦，明天再来')
  assert.match(companionError(httpError(429, 4291)).message, /一分钟/)
  assert.equal(companionError(httpError(403, 4033)).kind, 'consent')
  assert.equal(companionError(httpError(503, 5031)).message, '没听清，再说一次吧')
  assert.equal(companionError(httpError(400, 4001)).message, '没听清，再说一次吧')
  assert.equal(companionError(new Error('Network Error')).kind, 'network')
})

test('识别 429 进入异常并提示明天再来', async () => {
  const { c } = setup({ api: { recognizeSpeech: async () => { throw httpError(429, 4292) } } })
  await c.startRecording()
  await c.stopRecording()
  assert.equal(c.phase.value, 'error')
  assert.equal(c.error.value.message, '今天聊够啦，明天再来')
})

test('识别 403/4033 进入安全提示状态', async () => {
  const { c } = setup({ api: { recognizeSpeech: async () => { throw httpError(403, 4033) } } })
  await c.startRecording()
  await c.stopRecording()
  assert.equal(c.error.value.kind, 'consent')
  assert.equal(c.mouseState.value, 'safety')
})

test('识别为空文本提示重说', async () => {
  const { c } = setup({ api: { recognizeSpeech: async () => ({ text: '  ' }) } })
  await c.startRecording()
  await c.stopRecording()
  assert.equal(c.error.value.message, '没听清，再说一次吧')
})

test('游客不能录音，未同意提示家长开启', async () => {
  const guest = setup({ auth: { loggedIn: false, hasChild: false, consented: false } })
  assert.equal(guest.c.micMode.value, 'hidden')
  const noConsent = setup({ auth: { loggedIn: true, hasChild: true, consented: false } })
  assert.equal(noConsent.c.micMode.value, 'consent')
  await noConsent.c.startRecording()
  assert.equal(noConsent.c.error.value.message, '请家长在家长中心开启学习伙伴')
  assert.equal(noConsent.calls.recStop, 0)
})

test('麦克风被拒或非 HTTPS：给出说明，打字仍可用', async () => {
  const denied = setup({ recorder: { start: async () => { throw Object.assign(new Error(), { name: 'NotAllowedError' }) } } })
  await denied.c.startRecording()
  assert.equal(denied.c.error.value.kind, 'mic')
  assert.match(denied.c.error.value.message, /麦克风权限/)
  await denied.c.submitText('水果')
  assert.equal(denied.c.phase.value, 'choosing')

  const insecure = setup({ env: { isSecureContext: false }, recorder: { start: async () => { throw new TypeError() } } })
  await insecure.c.startRecording()
  assert.match(insecure.c.error.value.message, /HTTPS/)
})

test('自动播放被拦截时显示播放按钮，点了补播', async () => {
  let blocked = true
  const spoken = []
  const { c } = setup({ tts: { playAndWait: async (t) => { if (blocked) throw Object.assign(new Error(), { name: 'NotAllowedError' }); spoken.push(t) } } })
  await c.submitText('水果')
  assert.equal(c.needsPlayButton.value, true)
  assert.equal(c.phase.value, 'choosing')
  blocked = false
  await c.replay()
  assert.equal(c.needsPlayButton.value, false)
  assert.deepEqual(spoken, ['去学水果吧'])
})

test('录音授权期间松手：授权完成后立即停止', async () => {
  const start = deferred()
  const { c, calls } = setup({ recorder: { start: () => start.promise } })
  const run = c.startRecording()
  await c.stopRecording()
  start.resolve()
  await run
  assert.equal(calls.recStop, 1)
  assert.equal(c.phase.value, 'choosing')
})

test('录音中切页取消：停止录音并回到待机', async () => {
  const { c, calls } = setup()
  await c.startRecording()
  c.cancel()
  assert.equal(calls.recStop, 1)
  assert.equal(c.phase.value, 'idle')
})

test('文本超过 100 字截断，空文本不请求', async () => {
  const bodies = []
  const { c } = setup({ api: { askGuide: async (b) => { bodies.push(b); return { type: 'NO_MATCH', speech: '', candidates: [] } } } })
  await c.submitText('   ')
  await c.submitText('啊'.repeat(150))
  assert.equal(bodies.length, 1)
  assert.equal(bodies[0].text.length, 100)
})

test('播报中关闭面板：只停 companion 自己的音频，phase 回到 idle', async () => {
  let finish
  const tts = { playAndWait: () => new Promise((r) => { finish = r }) }
  tts.onStop = () => finish?.()
  const { c, calls } = setup({ tts })
  await c.openPanel()
  const run = c.submitText('水果')
  await tick()
  assert.equal(c.phase.value, 'speaking')
  const before = { ttsStop: calls.ttsStop, stopActive: calls.stopActive }
  c.closePanel()
  await run
  assert.equal(calls.ttsStop, before.ttsStop + 1)
  assert.equal(calls.stopActive, before.stopActive)
  assert.equal(c.phase.value, 'idle')
  assert.equal(c.open.value, false)
})

test('点主题直接跳服务端给的主题页 route，不再检索', async () => {
  let asked = 0
  const { c, calls } = setup({ api: { askGuide: async () => { asked++; return {} } } })
  await c.openPanel()
  c.go({ themeId: 3, name: '恐龙世界', route: '/theme/3?subjectId=1' })
  assert.deepEqual(calls.push, ['/theme/3?subjectId=1'])
  assert.equal(asked, 0)
  assert.equal(c.open.value, false)
  c.go({ themeId: 4, name: '没有 route' })
  assert.equal(calls.push.length, 1)
})
