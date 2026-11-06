import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { runInNewContext } from 'node:vm'
import { ref } from 'vue'

const source = readFileSync(new URL('../src/composables/useAgentRecords.js', import.meta.url), 'utf8')
  .replace(/^import .*\n/gm, '').replace(/^export /gm, '')
const httpError = (status, code, message) => Object.assign(new Error('x'), { response: { status, data: { code, message } } })
const plain = (v) => JSON.parse(JSON.stringify(v))

const item = (id, extra = {}) => ({ id, childId: 1, childName: '小米', topic: '水果', sourcePage: 'FREE', flagged: false, flagLabel: null,
  turns: 2, createdAt: '2026-11-06T18:30:00+08:00', updatedAt: '2026-11-06T18:35:00+08:00', ...extra })

function setup(api = {}) {
  const calls = { list: [], del: [], topics: [] }
  const sandbox = {
    ref, Intl, Date, Number, String, Boolean, Array, Object, Map, Set, console,
    listAgentSessions: async (p) => { calls.list.push(plain(p)); return api.list ? api.list(p) : { items: [item(1)], hasMore: false } },
    getAgentSession: api.get || (async () => ({ session: item(1), messages: [], interactions: [] })),
    deleteAgentSession: async (id) => { calls.del.push(id); return api.del ? api.del(id) : null },
    listLearningTopics: async (p) => { calls.topics.push(plain(p)); return api.topics ? api.topics(p) : [] },
    getChildren: api.children || (async () => [{ id: 1, nickname: '小米' }, { id: 2, nickname: '豆包' }])
  }
  const mod = runInNewContext(`${source}\n({ useAgentRecords, toBubble, toAnswer, groupTopics, formatTime, STATUS_LABEL })`, sandbox)
  return { mod, r: mod.useAgentRecords(), calls }
}

test('时间按北京时间显示', () => {
  const { mod } = setup()
  assert.equal(mod.formatTime('2026-11-06T10:30:00Z'), '2026-11-06 18:30')
  assert.equal(mod.formatTime(null), '')
})

test('列表：按孩子和只看提醒筛选，page 从 0 开始，加载更多翻页并去重', async () => {
  const pages = [{ items: [item(3), item(2)], hasMore: true }, { items: [item(2), item(1)], hasMore: false }]
  const { r, calls } = setup({ list: () => pages.shift() })
  r.filter.value = { childId: 2, flaggedOnly: true }
  await r.loadSessions()
  assert.deepEqual(calls.list[0], { childId: 2, flagged: true, page: 0, size: 20 })
  await r.loadMore()
  assert.equal(calls.list[1].page, 1)
  assert.deepEqual(plain(r.sessions.value.map((s) => s.id)), [3, 2, 1])
  assert.equal(r.hasMore.value, false)
  await r.loadMore()
  assert.equal(calls.list.length, 2)
})

test('列表：不勾只看提醒时不传 flagged；切筛选从第 0 页重来', async () => {
  const { r, calls } = setup({ list: () => ({ items: [item(1)], hasMore: true }) })
  await r.loadSessions()
  await r.loadMore()
  await r.loadSessions()
  assert.equal('flagged' in calls.list[0], false)
  assert.equal(calls.list[2].page, 0)
  assert.equal(r.sessions.value.length, 1)
})

test('家长取消验证(403/4031)进入 locked，验证后重新加载解除', async () => {
  let fail = true
  const { r } = setup({ list: () => { if (fail) throw httpError(403, 4031); return { items: [], hasMore: false } } })
  await r.loadSessions()
  assert.equal(r.locked.value, true)
  assert.equal(r.error.value, '')
  fail = false
  await r.loadSessions()
  assert.equal(r.locked.value, false)
})

test('其他错误显示服务端 message，不进 locked', async () => {
  const { r } = setup({ list: () => { throw httpError(500, 500, '服务异常') } })
  await r.loadSessions()
  assert.equal(r.locked.value, false)
  assert.equal(r.error.value, '服务异常')
})

test('详情：区分孩子 / 豆豆 / 安全回复，讲解标题从【】拆出', async () => {
  const { r } = setup({ get: async () => ({ session: item(1), messages: [
    { role: 'CHILD', text: '苹果是什么', moderation: 'NONE', createdAt: '2026-11-06T18:30:00+08:00' },
    { role: 'AGENT', text: '【苹果 apple】苹果是红红的水果', moderation: 'NONE' },
    { role: 'AGENT', text: '这个问题问问爸爸妈妈吧', moderation: 'SAFE_REPLY' },
    { role: 'AGENT', text: '我们换个话题吧', moderation: 'OUTPUT_BLOCKED' }
  ], interactions: [] }) })
  await r.openDetail(1)
  const b = plain(r.detail.value.bubbles)
  assert.deepEqual(b.map((x) => x.kind), ['child', 'agent', 'safe', 'safe'])
  assert.equal(b[0].time, '2026-11-06 18:30')
  assert.equal(b[1].title, '苹果 apple')
  assert.equal(b[1].body, '苹果是红红的水果')
  assert.equal(b[2].who, '豆豆（安全回复）')
})

test('详情：答题记录把选项 id 换成文字，未作答显示“未作答”', async () => {
  const opts = [{ id: 'a', text: '红色' }, { id: 'b', text: '蓝色' }]
  const { r } = setup({ get: async () => ({ session: item(1), messages: [], interactions: [
    { question: '苹果是什么颜色', options: opts, chosen: 'a', correct: true },
    { question: '天空是什么颜色', options: opts, chosen: 'a', correct: false },
    { question: '香蕉是什么颜色', options: opts, chosen: null, correct: null }
  ] }) })
  await r.openDetail(1)
  const a = plain(r.detail.value.answers)
  assert.deepEqual(a.map((x) => [x.chosen, x.result]), [['红色', 'right'], ['红色', 'wrong'], ['未作答', 'none']])
  assert.deepEqual(a[0].options, ['红色', '蓝色'])
})

test('详情 404：从列表移除并提示记录不存在', async () => {
  const { r } = setup({ get: async () => { throw httpError(404, 404, '记录不存在') } })
  await r.loadSessions()
  await r.openDetail(1)
  assert.equal(r.sessions.value.length, 0)
  assert.equal(r.detail.value, null)
  assert.match(r.error.value, /记录不存在/)
})

test('删除：成功后从列表和详情移除；404 也当作已删除', async () => {
  const { r, calls } = setup({ list: () => ({ items: [item(1), item(2)], hasMore: false }),
    del: (id) => { if (id === 2) throw httpError(404, 404, '记录不存在') } })
  await r.loadSessions()
  await r.openDetail(1)
  assert.equal(await r.removeSession(1), true)
  assert.equal(r.detail.value, null)
  assert.equal(await r.removeSession(2), true)
  assert.deepEqual(calls.del, [1, 2])
  assert.equal(r.sessions.value.length, 0)
})

test('删除失败：保留在列表并提示', async () => {
  const { r } = setup({ del: () => { throw httpError(500, 500) } })
  await r.loadSessions()
  assert.equal(await r.removeSession(1), false)
  assert.equal(r.sessions.value.length, 1)
  assert.equal(r.error.value, '删除失败，请重试')
})

test('主题：按孩子分组，状态文案不出现“已掌握”，删除后重新拉取', async () => {
  const { r, calls, mod } = setup({ topics: () => [
    { childId: 1, themeId: 3, themeName: '水果', status: 'PRACTICE', sessions: [5, 4], correct: 1, wrong: 2,
      suggestion: { id: 9, basis: '最近 3 次答错 2 次', route: '/lesson/12?themeId=3&unitId=5&subjectId=1' } },
    { childId: 2, themeId: 4, themeName: '动物', status: 'SEEN', sessions: [6], correct: 0, wrong: 0 },
    { childId: 1, themeId: 5, themeName: '颜色', status: 'ANSWERED', sessions: [7], correct: 3, wrong: 0 }
  ] })
  await r.loadChildren()
  r.filter.value.childId = ''
  await r.loadTopics()
  // 空 childId 由 api 层 clean() 去掉,这里只断言不带其他参数
  assert.deepEqual(calls.topics[0], { childId: '' })
  const g = plain(r.topicGroups.value)
  assert.deepEqual(g.map((x) => [x.childName, x.topics.map((t) => t.themeName)]), [['小米', ['水果', '颜色']], ['豆包', ['动物']]])
  assert.equal(Object.values(mod.STATUS_LABEL).some((s) => s.includes('掌握')), false)
  await r.loadSessions()
  await r.removeSession(1)
  assert.equal(calls.topics.length, 2)
})
