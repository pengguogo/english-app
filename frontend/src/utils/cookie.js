/**
 * cookie.js - 读取非 HttpOnly cookie(XSRF-TOKEN)
 * @param {string} name cookie 名
 * @return {string|null} 解码后的值
 */
export function getCookie(name) {
  const hit = document.cookie.split('; ').find(item => item.startsWith(`${name}=`))
  return hit ? decodeURIComponent(hit.slice(name.length + 1)) : null
}
