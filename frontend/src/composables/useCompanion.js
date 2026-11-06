/**
 * @file useCompanion.js - 学习伙伴面板状态机
 * @description 管理“按住说话 / 打字 / 点主题 → 识别 → 检索 → 播报 → 等孩子选”的流程。
 *  phase: idle 待机 | recording 录音中 | recognizing 识别中 | thinking 检索中
 *         | speaking 播报中 | choosing 等待选择 | error 异常
 *  每次发起流程分配一个序号 seq;cancel() 让 seq 失效并 abort 请求,过期响应一律丢弃。
 *  只有孩子点了「去学」或主题入口才 router.push(服务端给的 route),前端不拼。
 */
import { computed, ref } from 'vue'
import { askGuide, fetchTopics, recognizeSpeech } from '../api/agent'
import { useRecorder } from './useRecorder'
import { stopActiveTts, useTts } from './useTts'

export const MAX_RECORD_MS = 15000
export const SLOW_MS = 2000
export const MAX_TEXT = 100
export const THINKING_SPEECH = '让我想一想'

const MSG = {
  guestMic: '登录后可以和我说话，现在可以打字或点主题',
  consent: '请家长在家长中心开启学习伙伴',
  noChild: '先选一个小朋友，再和我说话吧',
  insecure: '要用 HTTPS 打开才能说话，可以打字或点主题',
  noMic: '这个浏览器不能录音，可以打字或点主题',
  micDenied: '没有麦克风权限，可以打字或点主题',
  retry: '没听清，再说一次吧',
  minute: '说得太快啦，歇一分钟再来',
  daily: '今天聊够啦，明天再来',
  network: '网络开小差了，再试一次'
}

/**
 * 把接口错误映射为面板异常。
 * @param {unknown} error axios 错误
 * @return {{kind: string, message: string}}
 */
export function companionError(error) {
  const status = error?.response?.status
  const code = error?.response?.data?.code
  if (status === 429) return code === 4291 ? { kind: 'quota', message: MSG.minute } : { kind: 'quota', message: MSG.daily }
  if (status === 403 && code === 4033) return { kind: 'consent', message: MSG.consent }
  if (status === 401) return { kind: 'login', message: MSG.guestMic }
  if (status === 409) return { kind: 'child', message: MSG.noChild }
  // 400/4001 录音无效(太短/格式/过大/没识别出内容);503/5031 识别服务不可用:都请孩子再说一次
  if ((status === 400 && code === 4001) || status === 503) return { kind: 'retry', message: MSG.retry }
  return { kind: 'network', message: MSG.network }
}

/** 麦克风启动失败(含非 HTTPS)的提示;打字和点主题照常可用。 */
function micError(error) {
  if (globalThis.isSecureContext === false) return { kind: 'mic', message: MSG.insecure }
  if (!globalThis.navigator?.mediaDevices?.getUserMedia) return { kind: 'mic', message: MSG.noMic }
  const name = error?.name || ''
  if (name === 'NotAllowedError' || name === 'SecurityError') return { kind: 'mic', message: MSG.micDenied }
  return { kind: 'mic', message: MSG.noMic }
}

/**
 * @param {{auth: {loggedIn: boolean, hasChild: boolean, consented: boolean},
 *          router: {push: Function}}} deps auth 为 pinia store(已解包),router 为路由实例
 */
export function useCompanion({ auth, router }) {
  const recorder = useRecorder()
  const tts = useTts()

  const open = ref(false)
  const phase = ref('idle')
  const slow = ref(false)
  const result = ref(null)
  const topics = ref([])
  const error = ref(null)
  // 自动播放被浏览器拦截时显示「播放」按钮,由孩子点一下补播
  const needsPlayButton = ref(false)
  const lastSpeech = ref('')

  let seq = 0
  let controller = null
  let recordTimer = null
  let slowTimer = null
  let stopRequested = false

  /** 录音入口可见性:游客不显示;已登录未同意显示但点了提示家长开启 */
  const micMode = computed(() => {
    if (!auth.loggedIn) return 'hidden'
    if (!auth.hasChild) return 'no-child'
    if (!auth.consented) return 'consent'
    return 'ready'
  })

  /** 给小老鼠组件的状态 */
  const mouseState = computed(() => {
    switch (phase.value) {
      case 'recording': return 'listening'
      case 'recognizing':
      case 'thinking': return slow.value ? 'thinking' : 'listening'
      case 'speaking':
      case 'choosing': return result.value?.type === 'NO_MATCH' ? 'idle' : 'cheer'
      case 'error': return error.value?.kind === 'consent' || error.value?.kind === 'mic' ? 'safety' : 'idle'
      default: return 'idle'
    }
  })

  function clearTimers() {
    clearTimeout(recordTimer)
    clearTimeout(slowTimer)
    recordTimer = slowTimer = null
    slow.value = false
  }

  /** 新开一轮:作废旧请求,返回本轮序号 */
  function nextTurn() {
    controller?.abort()
    controller = new AbortController()
    clearTimers()
    error.value = null
    needsPlayButton.value = false
    return ++seq
  }

  const isCurrent = (turn) => turn === seq

  function fail(turn, err) {
    if (!isCurrent(turn)) return
    clearTimers()
    error.value = err
    phase.value = 'error'
  }

  /** 超过 2s 还没结果:显示思考动画并播“让我想一想”(只播一次) */
  function startSlowTimer(turn) {
    if (slowTimer) return
    slowTimer = setTimeout(() => {
      if (!isCurrent(turn) || !['recognizing', 'thinking'].includes(phase.value)) return
      slow.value = true
      tts.play(THINKING_SPEECH, 'zh').catch(() => {})
    }, SLOW_MS)
  }

  /** 播报 speech;被自动播放策略拦下时显示播放按钮 */
  async function speak(turn, text) {
    if (!text) return
    lastSpeech.value = text
    stopActiveTts()
    try {
      await tts.playAndWait(text, 'zh')
    } catch (e) {
      if (isCurrent(turn) && e?.name === 'NotAllowedError') needsPlayButton.value = true
    }
  }

  async function openPanel() {
    open.value = true
    if (topics.value.length) return
    try {
      const list = await fetchTopics()
      topics.value = Array.isArray(list) ? list : []
    } catch {
      topics.value = []
    }
  }

  /** 停录音、停播报、丢弃在途响应,回到待机 */
  function cancel() {
    seq++
    controller?.abort()
    controller = null
    clearTimers()
    stopRequested = false
    // 只在录音中才停;识别阶段 stopRecording 已在等 recorder.stop,重复调用会覆盖它的 onstop
    if (phase.value === 'recording') recorder.stop()
    tts.stop()
    phase.value = 'idle'
    error.value = null
    needsPlayButton.value = false
  }

  /** 关闭面板:不跳转、不打断课程(课程自己的音频此时并未被面板占用) */
  function closePanel() {
    cancel()
    result.value = null
    open.value = false
  }

  async function startRecording() {
    if (micMode.value !== 'ready') {
      const message = { hidden: MSG.guestMic, 'no-child': MSG.noChild, consent: MSG.consent }[micMode.value]
      error.value = { kind: micMode.value === 'consent' ? 'consent' : 'login', message }
      phase.value = 'error'
      return
    }
    if (phase.value === 'recording') return
    const turn = nextTurn()
    stopRequested = false
    stopActiveTts()
    tts.stop()
    try {
      await recorder.start()
    } catch (e) {
      return fail(turn, micError(e))
    }
    if (!isCurrent(turn)) {
      recorder.stop()
      return
    }
    phase.value = 'recording'
    recordTimer = setTimeout(() => stopRecording(), MAX_RECORD_MS)
    // 孩子在麦克风授权弹窗期间就松手了
    if (stopRequested) await stopRecording()
  }

  async function stopRecording() {
    if (phase.value !== 'recording') {
      stopRequested = true
      return
    }
    const turn = seq
    clearTimers()
    phase.value = 'recognizing'
    const blob = await recorder.stop()
    if (!isCurrent(turn)) return
    if (!blob) return fail(turn, { kind: 'retry', message: MSG.retry })
    startSlowTimer(turn)
    let text = ''
    try {
      const res = await recognizeSpeech(blob, { signal: controller?.signal })
      text = (res?.text || '').trim()
    } catch (e) {
      return fail(turn, companionError(e))
    }
    if (!isCurrent(turn)) return
    if (!text) return fail(turn, { kind: 'retry', message: MSG.retry })
    await search(turn, text)
  }

  /** 打字或点主题入口 */
  async function submitText(raw) {
    const text = String(raw || '').trim().slice(0, MAX_TEXT)
    if (!text) return
    const turn = nextTurn()
    stopActiveTts()
    tts.stop()
    await search(turn, text)
  }

  async function search(turn, text) {
    phase.value = 'thinking'
    startSlowTimer(turn)
    let res
    try {
      res = await askGuide({ text }, { signal: controller?.signal })
    } catch (e) {
      return fail(turn, companionError(e))
    }
    if (!isCurrent(turn)) return
    clearTimers()
    tts.stop()
    result.value = {
      type: res?.type || 'NO_MATCH',
      speech: res?.speech || '',
      candidates: (res?.candidates || []).slice(0, 3),
      topics: res?.topics?.length ? res.topics : topics.value
    }
    phase.value = 'speaking'
    await speak(turn, result.value.speech)
    if (isCurrent(turn)) phase.value = 'choosing'
  }

  /** 孩子点了候选卡「去学」或主题入口才跳转;route 一律由服务端给 */
  function go(target) {
    if (!target?.route) return
    closePanel()
    router.push(target.route)
  }

  /** 播放按钮:用户手势下补播 */
  async function replay() {
    needsPlayButton.value = false
    await speak(seq, lastSpeech.value)
  }

  return {
    open, phase, slow, result, topics, error, needsPlayButton, micMode, mouseState,
    openPanel, closePanel, cancel, startRecording, stopRecording, submitText, go, replay
  }
}
