/**
 * @file 学习伙伴记录 API(阶段 3,家长中心)
 * @description 均需家长二次验证;失效时 http 拦截器(403/4031)弹家长密码框后重试。
 *  - GET    /parent/agent-sessions?childId=&flagged=&page=&size=  会话列表,page 从 0 开始
 *  - GET    /parent/agent-sessions/{id}                           会话详情(消息 + 答题)
 *  - DELETE /parent/agent-sessions/{id}                           删除会话(级联)
 *  - GET    /parent/learning-topics?childId=                      各孩子的主题学习情况与复习建议
 *  时间为 ISO-8601 带时区;不属于本家庭返回 404「记录不存在」。
 */
import http from './http'

/** 去掉空值,避免把 childId= 这类空参数发给后端 */
const clean = (params) => Object.fromEntries(Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== ''))

/**
 * @param {{childId?: number, flagged?: boolean, page?: number, size?: number}} params
 * @return {Promise<{items: Array, hasMore: boolean}>}
 */
export const listAgentSessions = (params = {}) => http.get('/parent/agent-sessions', { params: clean(params) })

/** @return {Promise<{session: object, messages: Array, interactions: Array}>} */
export const getAgentSession = (id) => http.get(`/parent/agent-sessions/${id}`)

export const deleteAgentSession = (id) => http.delete(`/parent/agent-sessions/${id}`)

/** @return {Promise<Array<{childId, themeId, themeName, status, sessions: number[], correct, wrong, lastAt, suggestion?}>>} */
export const listLearningTopics = (params = {}) => http.get('/parent/learning-topics', { params: clean(params) })
