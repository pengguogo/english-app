package com.englishapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 应用配色方案。
 * 与 Web 前端设计令牌(tokens.css)保持一致,确保跨端视觉统一。
 * @author englishapp
 * @since 1.0.0
 */

// ===== 品牌色 =====
val Primary = Color(0xFF6B7CFF)        // 主按钮、导航、链接
val Accent = Color(0xFFFFD93D)         // 星星、奖励、CTA
val Success = Color(0xFF4CD964)        // 完成、正确反馈
val Warning = Color(0xFFFF6B6B)        // 错误、未解锁
val Orange = Color(0xFFFF9800)         // 评分、录音

// ===== 背景色 =====
val PageBackground = Color(0xFFFFF8F0)  // 页面暖底
val CardBackground = Color(0xFFFFFFFF) // 卡片白底
val MutedBackground = Color(0xFFF5F5F7) // 次级背景

// ===== 文字色 =====
val TextPrimary = Color(0xFF2D3142)    // 主要文字
val TextSecondary = Color(0xFF6B7280)  // 次要文字
val TextTertiary = Color(0xFF9CA3AF)   // 辅助文字

// ===== 边框/分隔 =====
val BorderLight = Color(0xFFE5E7EB)

// ===== 渐变 =====
val GradientPrimaryStart = Color(0xFF6B7CFF)
val GradientPrimaryEnd = Color(0xFF8B9CFF)
val GradientWarmStart = Color(0xFFFFF8F0)
val GradientWarmEnd = Color(0xFFFFF0E0)

// ===== Material3 配色 =====
val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Primary,
    onPrimaryContainer = Color.White,
    secondary = Accent,
    onSecondary = TextPrimary,
    tertiary = Orange,
    background = PageBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = MutedBackground,
    onSurfaceVariant = TextSecondary,
    error = Warning,
    onError = Color.White
)
