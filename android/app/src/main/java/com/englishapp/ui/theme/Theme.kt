package com.englishapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * 应用主题入口。
 * 组合配色方案、字体规范、形状规范。
 * @author englishapp
 * @since 1.0.0
 */
@Composable
fun EnglishAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
