/**
 * 统一音标显示：去掉数据首尾已有的斜杠，再包一层 /x/。
 * @param {string} value 原始音标，如 "/ef/"、"ef"、"//f//"
 * @returns {string} 形如 "/ef/"；空值返回 ""
 */
export function formatPhonetic(value) {
  const core = String(value ?? '').trim().replace(/^\/+|\/+$/g, '').trim()
  return core ? `/${core}/` : ''
}
