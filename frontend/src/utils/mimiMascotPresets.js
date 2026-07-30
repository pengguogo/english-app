/**
 * mimiMascotPresets.js - Mimi 吉祥物场景预设
 * 用途: 统一管理各场景的资源选择与动效策略，保持页面侧只传 variant/size 即可。
 * 作者: english-app
 * 创建日期: 2026-07-30
 */

/**
 * @typedef {"welcome"|"companion"|"celebrate"|"avatar"|"empty"} MimiVariant
 */

/**
 * @typedef {"none"|"float"|"breathe"|"celebrate"} MimiMotion
 */

/**
 * 获取吉祥物场景动效预设。
 * @param {MimiVariant} variant 场景类型
 * @return {{ motion: MimiMotion }} 预设对象
 */
export function getMimiPreset(variant) {
  // Guard Clauses：未知 variant 回退 companion（更安全）
  if (!variant) return { motion: 'breathe' }

  if (variant === 'welcome') return { motion: 'float' }
  if (variant === 'companion') return { motion: 'breathe' }
  if (variant === 'celebrate') return { motion: 'celebrate' }

  // avatar/empty 需要克制一些：仅轻微呼吸或漂浮，不做弹跳
  if (variant === 'avatar') return { motion: 'breathe' }
  if (variant === 'empty') return { motion: 'float' }

  return { motion: 'breathe' }
}

