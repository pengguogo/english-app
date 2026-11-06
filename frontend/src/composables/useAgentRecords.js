/**
 * @file useAgentRecords.js - 家长中心「学习伙伴记录」页面状态
 * @description 会话列表(按孩子 / 只看提醒筛选,分页加载更多)、详情、删除,以及「主题」标签页。
 *  家长验证失效(403/4031)时 http 拦截器弹 ParentVerifyModal;家长取消验证则进入 locked,页面显示「验证并进入」。
 *  这里只管数据,删除的二次确认由页面负责。
 */
import { ref } from 'vue'
import { deleteAgentSession, getAgentSession, listAgentSessions, listLearningTopics } from '../api/agentRecords'
import { getChildren } from '../api/auth'

export const PAGE_SIZE = 20
export const SOURCE_LABEL = { LESSON: '课程页', FREE: '自由提问' }
// 不出现“已掌握”:状态只描述孩子做过什么
export const STATUS_LABEL = { SEEN: '接触过', ANSWERED: '回答过', PRACTICE: '建议再练习' }
const NOT_FOUND = '记录不存在，可能已被删除'

/** 家长验证失效(403/4031)且家长取消了验证 */
const isLocked = (e) => e?.response?.status === 403 && e?.response?.data?.code === 4031
const isNotFound = (e) => e?.response?.status === 404
const errorText = (e, fallback) => e?.response?.data?.message || fallback

/** ISO 时间 → 北京时间「2026-11-06 18:30」;无法解析时原样返回 */
export function formatTime(iso) {
  const d = new Date(iso)
  if (!iso || Number.isNaN(d.getTime())) return iso || ''
  const p = Object.fromEntries(new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai', year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false
  }).formatToParts(d).map((x) => [x.type, x.value]))
  return `${p.year}-${p.month}-${p.day} ${p.hour}:${p.minute}`
}

/**
 * 把一条消息转成详情页展示用的结构。
 * AGENT 消息的讲解标题拼在 text 开头并用【】括起来,这里拆成 title + body。
 * @return {{kind: 'child'|'agent'|'safe', who: string, title: string, body: string, time: string}}
 */
export function toBubble(m) {
  const text = String(m?.text || '')
  const time = formatTime(m?.createdAt)
  if (m?.role === 'CHILD') return { kind: 'child', who: '孩子', title: '', body: text, time }
  const safe = m?.moderation === 'SAFE_REPLY' || m?.moderation === 'OUTPUT_BLOCKED'
  const match = /^【([^】]*)】\s*/.exec(text)
  return {
    kind: safe ? 'safe' : 'agent',
    who: safe ? '豆豆（安全回复）' : '豆豆',
    title: match ? match[1] : '',
    body: match ? text.slice(match[0].length) : text,
    time
  }
}

/**
 * 答题记录:把选项 id 换成文字。同一道题只记首次作答;
 * 未作答时 chosen=null、correct=null,result 为 'none'(显示“未作答”)。
 */
export function toAnswer(it) {
  const options = Array.isArray(it?.options) ? it.options : []
  const answered = it?.chosen !== null && it?.chosen !== undefined
  const picked = answered ? options.find((o) => String(o.id) === String(it.chosen)) : null
  return {
    question: it?.question || '',
    options: options.map((o) => o.text),
    chosen: answered ? (picked ? picked.text : String(it.chosen)) : '未作答',
    result: !answered ? 'none' : it?.correct ? 'right' : 'wrong',
    time: formatTime(it?.createdAt)
  }
}

/** 主题按孩子分组,保留服务端顺序 */
export function groupTopics(list, children) {
  const names = new Map(children.map((c) => [c.id, c.nickname]))
  const groups = new Map()
  for (const t of Array.isArray(list) ? list : []) {
    if (!groups.has(t.childId)) groups.set(t.childId, { childId: t.childId, childName: names.get(t.childId) || `孩子 ${t.childId}`, topics: [] })
    groups.get(t.childId).topics.push(t)
  }
  return [...groups.values()]
}

export function useAgentRecords() {
  const locked = ref(false)
  const error = ref('')
  const children = ref([])
  const filter = ref({ childId: '', flaggedOnly: false })

  const sessions = ref([])
  const hasMore = ref(false)
  const loading = ref(false)
  let page = 0
  let listSeq = 0

  const detail = ref(null)
  const detailLoading = ref(false)
  const deleting = ref(false)

  const topicGroups = ref([])
  const topicsLoading = ref(false)

  function handle(e, fallback) {
    if (isLocked(e)) locked.value = true
    error.value = isLocked(e) ? '' : errorText(e, fallback)
  }

  async function loadChildren() {
    try {
      const list = await getChildren()
      children.value = Array.isArray(list) ? list : []
    } catch {
      children.value = []
    }
  }

  /** 拉一页;reset=true 时从第 0 页重来(切筛选、首次进入) */
  async function loadSessions(reset = true) {
    if (!reset && (loading.value || !hasMore.value)) return
    const seq = ++listSeq
    if (reset) page = 0
    loading.value = true
    error.value = ''
    try {
      const res = await listAgentSessions({
        childId: filter.value.childId,
        flagged: filter.value.flaggedOnly ? true : undefined,
        page,
        size: PAGE_SIZE
      })
      // 筛选切换后,旧请求的结果丢弃
      if (seq !== listSeq) return
      const items = Array.isArray(res?.items) ? res.items : []
      sessions.value = reset ? items : [...sessions.value, ...items.filter((x) => !sessions.value.some((s) => s.id === x.id))]
      hasMore.value = Boolean(res?.hasMore)
      page += 1
      locked.value = false
    } catch (e) {
      if (seq === listSeq) handle(e, '加载失败，请重试')
    } finally {
      if (seq === listSeq) loading.value = false
    }
  }

  const loadMore = () => loadSessions(false)

  async function openDetail(id) {
    detailLoading.value = true
    error.value = ''
    try {
      const res = await getAgentSession(id)
      detail.value = {
        id,
        session: res?.session || sessions.value.find((s) => s.id === id) || {},
        bubbles: (res?.messages || []).map(toBubble),
        answers: (res?.interactions || []).map(toAnswer)
      }
    } catch (e) {
      if (isNotFound(e)) {
        dropSession(id)
        error.value = NOT_FOUND
      } else handle(e, '加载失败，请重试')
    } finally {
      detailLoading.value = false
    }
  }

  const closeDetail = () => { detail.value = null }

  function dropSession(id) {
    sessions.value = sessions.value.filter((s) => s.id !== id)
    if (detail.value?.id === id) detail.value = null
  }

  /** 删除会话;成功或已不存在都从列表移除,并刷新主题(派生的复习建议会重算) */
  async function removeSession(id) {
    if (deleting.value) return false
    deleting.value = true
    error.value = ''
    try {
      await deleteAgentSession(id)
      dropSession(id)
      return true
    } catch (e) {
      if (isNotFound(e)) { dropSession(id); return true }
      handle(e, '删除失败，请重试')
      return false
    } finally {
      deleting.value = false
      if (topicGroups.value.length) loadTopics()
    }
  }

  async function loadTopics() {
    topicsLoading.value = true
    error.value = ''
    try {
      const list = await listLearningTopics({ childId: filter.value.childId })
      topicGroups.value = groupTopics(list, children.value)
      locked.value = false
    } catch (e) {
      handle(e, '加载失败，请重试')
    } finally {
      topicsLoading.value = false
    }
  }

  return {
    locked, error, children, filter, sessions, hasMore, loading, detail, detailLoading, deleting, topicGroups, topicsLoading,
    loadChildren, loadSessions, loadMore, openDetail, closeDetail, removeSession, loadTopics
  }
}
