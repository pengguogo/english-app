/**
 * @file 家庭账号相关 API
 * @description 对应后端 /api/v1/auth/* 与 /api/v1/children*。
 *  字段以 ea-auth 接口清单 v1 为准;login/register/consent/children/current 的 data 与 /me 相同。
 */
import http from './http'

// 登录/注册页自己展示错误,不触发全局跳转
const quiet = { skipAuthRedirect: true }

/** 当前会话;未登录 data=null,同时触发后端下发 XSRF-TOKEN cookie */
export const getMe = () => http.get('/auth/me', quiet)
export const register = (data) => http.post('/auth/register', data, quiet)
export const login = (data) => http.post('/auth/login', data, quiet)
export const logout = () => http.post('/auth/logout', null, quiet)
/** 监护人同意 */
export const submitConsent = (data) => http.post('/auth/consent', data)
/** 家长二次验证(10 分钟有效) */
export const verifyParent = (password) => http.post('/auth/parent-verify', { password }, quiet)

export const getChildren = () => http.get('/children')
export const createChild = (data) => http.post('/children', data)
/** 切换当前孩子(设计稿 5.4: PUT /children/current) */
export const selectChild = (childId) => http.put('/children/current', { childId })
