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
 * @typedef {"none"|"float"|"breathe"|"bounce"} MimiMotion
 */

/**
 * @typedef {"none"|"sway"|"nod"|"wiggle"} MimiSecondaryMotion
 */

/**
 * @typedef {"none"|"pulse"} MimiAuraMotion
 */

/**
 * @typedef {"default"|"celebrate-excited"} MimiMotionStyle
 */

/**
 * 获取吉祥物场景动效预设。
 * @param {MimiVariant} variant 场景类型
 * @return {{ motion: MimiMotion, secondaryMotion: MimiSecondaryMotion, auraMotion: MimiAuraMotion, motionStyle: MimiMotionStyle }} 预设对象
 */
export function getMimiPreset(variant) {
  // Guard Clauses：未知 variant 回退 companion（更安全）
  if (!variant) {
    return { motion: 'breathe', secondaryMotion: 'nod', auraMotion: 'none', motionStyle: 'default' }
  }

  if (variant === 'welcome') {
    return { motion: 'float', secondaryMotion: 'sway', auraMotion: 'none', motionStyle: 'default' }
  }

  if (variant === 'companion') {
    return { motion: 'breathe', secondaryMotion: 'nod', auraMotion: 'none', motionStyle: 'default' }
  }

  if (variant === 'celebrate') {
    return { motion: 'bounce', secondaryMotion: 'wiggle', auraMotion: 'pulse', motionStyle: 'celebrate-excited' }
  }

  // avatar/empty 需要克制一些：仅轻微呼吸或漂浮，不做弹跳
  if (variant === 'avatar') {
    return { motion: 'breathe', secondaryMotion: 'none', auraMotion: 'none', motionStyle: 'default' }
  }

  if (variant === 'empty') {
    return { motion: 'float', secondaryMotion: 'sway', auraMotion: 'none', motionStyle: 'default' }
  }

  return { motion: 'breathe', secondaryMotion: 'nod', auraMotion: 'none', motionStyle: 'default' }
}
