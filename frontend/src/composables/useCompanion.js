/**
 * @file useCompanion.js - 学习伙伴面板状态机
 * @description 管理“按住说话 / 打字 / 点主题 → 识别 → 对话(/agent/chat) → 播报 → 等孩子选 / 答题”的流程。
 *  phase: idle 待机 | recording 录音中 | recognizing 识别中 | thinking 检索中
 *         | speaking 播报中 | choosing 等待选择 | error 异常
 *  每次发起流程分配一个序号 seq;cancel() 让 seq 失效并 abort 请求,过期响应一律丢弃。
 *  只有孩子点了「去学」或主题入口才 router.push(服务端给的 route),前端不拼。
 *  EXPLAIN 带 quiz 时,孩子点选项调 /agent/answer:答错播 hint 可再选,答对后锁定不再提交。
 *  在课程页时把 learningContext({lessonId, itemIndex})带进 chat 的 context。
 *  会话:chat 响应的 sessionId 存在面板里,下一轮 chat 和 answer 都带上;关闭面板或切页(endSession)后清空,
 *  下次开始新会话。服务端判断会话是否属于当前孩子,前端只透传。
 */
import { computed, ref } from 'vue'
import { answerQuiz, chatWithAgent, fetchTopics, recognizeSpeech } from '../api/agent'
import { learningContext } from '../stores/learningContext'
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
  network: '网络开小差了，再试一次',
  quizExpired: '这道题过期啦，再问我一次吧'
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
  // 当前题的答题状态:wrong 为答错过的选项 id;solved 后不能再提交
  const quiz = ref(null)

  let seq = 0
  // 当前会话 id,只在面板内部流转,不展示
  let sessionId = null
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
      case 'choosing':
        if (result.value?.type === 'SAFE_REPLY') return 'safety'
        if (result.value?.type === 'NO_MATCH') return 'idle'
        if (quiz.value?.solved) return 'cheer'
        return quiz.value?.wrong.length ? 'idle' : 'cheer'
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
    quiz.value = null
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
    // 在途的答题请求已作废,放开选项让孩子可以重新点
    if (quiz.value) quiz.value.pending = false
  }

  /** 结束当前会话:下一轮 chat 不带 sessionId,由服务端新建 */
  function endSession() {
    sessionId = null
  }

  /** 关闭面板:不跳转、不打断课程(课程自己的音频此时并未被面板占用) */
  function closePanel() {
    cancel()
    endSession()
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
    let turnToken
    try {
      const res = await recognizeSpeech(blob, { signal: controller?.signal })
      text = (res?.text || '').trim()
      turnToken = res?.turnToken || undefined
    } catch (e) {
      return fail(turn, companionError(e))
    }
    if (!isCurrent(turn)) return
    if (!text) return fail(turn, { kind: 'retry', message: MSG.retry })
    // asr 已扣过回合,带上 turnToken 让 chat 不再重复扣
    await search(turn, text.slice(0, MAX_TEXT), turnToken)
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

  async function search(turn, text, turnToken) {
    phase.value = 'thinking'
    startSlowTimer(turn)
    const body = { text }
    if (turnToken) body.turnToken = turnToken
    if (sessionId) body.sessionId = sessionId
    if (learningContext.value) body.context = { ...learningContext.value }
    let res
    try {
      res = await chatWithAgent(body, { signal: controller?.signal })
    } catch (e) {
      return fail(turn, companionError(e))
    }
    if (!isCurrent(turn)) return
    if (res?.sessionId) sessionId = res.sessionId
    clearTimers()
    tts.stop()
    const type = res?.type || 'NO_MATCH'
    const listed = type === 'RECOMMEND' || type === 'CLARIFY'
    const explain = type === 'EXPLAIN' ? res?.explain || null : null
    const options = res?.quiz?.options
    result.value = {
      type,
      speech: res?.speech || '',
      // 安全话术只显示话术和主题入口,不显示候选卡/讲解
      candidates: listed ? (res?.candidates || []).slice(0, 3) : [],
      explain,
      quiz: explain && res?.quiz?.quizToken && Array.isArray(options) && options.length >= 2
        ? { ...res.quiz, options: options.slice(0, 3) } : null,
      learnMore: explain && res?.learnMore?.route ? res.learnMore : null,
      topics: res?.topics?.length ? res.topics : topics.value
    }
    quiz.value = result.value.quiz ? { wrong: [], solved: false, pending: false, expired: false, feedback: '' } : null
    phase.value = 'speaking'
    await speak(turn, result.value.speech)
    if (isCurrent(turn)) phase.value = 'choosing'
  }

  /**
   * 孩子点了点选题的某个选项。答错播 hint 可再选;答对锁定,豆豆切 cheer。
   * @param {{id: string}} option 选项
   */
  async function answer(option) {
    const state = quiz.value
    const token = result.value?.quiz?.quizToken
    if (!state || !token || !option?.id || state.solved || state.pending || state.expired) return
    if (state.wrong.includes(option.id)) return
    const turn = seq
    state.pending = true
    let res
    try {
      const body = { quizToken: token, optionId: option.id }
      if (sessionId) body.sessionId = sessionId
      res = await answerQuiz(body, { signal: controller?.signal })
    } catch (e) {
      if (!isCurrent(turn)) return
      state.pending = false
      // 400/4002 令牌过期、换了孩子或被篡改:提示重新问,选项全部禁用
      if (e?.response?.status === 400 && e?.response?.data?.code === 4002) {
        state.expired = true
        state.feedback = MSG.quizExpired
      } else {
        state.feedback = companionError(e).message
      }
      return
    }
    if (!isCurrent(turn)) return
    state.pending = false
    if (res?.correct) {
      state.solved = true
      state.picked = option.id
      state.feedback = res.speech || ''
    } else {
      state.wrong.push(option.id)
      state.feedback = res?.hint || res?.speech || ''
    }
    phase.value = 'speaking'
    await speak(turn, state.feedback)
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
    open, phase, slow, result, topics, error, needsPlayButton, micMode, mouseState, quiz,
    openPanel, closePanel, cancel, endSession, startRecording, stopRecording, submitText, answer, go, replay
  }
}
