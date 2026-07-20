package com.englishapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * 应用形状规范。
 * 圆角对应 Web 端令牌:sm(8)/md(16)/lg(24)/pill(999)。
 * 儿童友好的大圆角设计。
 * @author englishapp
 * @since 1.0.0
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(50)
)
