package com.englishapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.englishapp.ui.theme.Accent
import com.englishapp.ui.theme.BorderLight

/**
 * 星星尺寸枚举。
 */
enum class StarSize { SM, LG }

/**
 * 星星显示组件。
 * 显示 3 颗星,根据 stars 参数高亮对应数量。
 * 对应 Web 端 StarBar 组件。
 * @author englishapp
 * @since 1.0.0
 */
@Composable
fun StarBar(
    stars: Int,
    modifier: Modifier = Modifier,
    size: StarSize = StarSize.SM
) {
    val iconSize = when (size) {
        StarSize.SM -> 20.dp
        StarSize.LG -> 40.dp
    }

    Row(modifier = modifier) {
        repeat(3) { index ->
            val isFilled = index < stars
            Icon(
                imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = if (isFilled) "获得的星星" else "未获得的星星",
                modifier = Modifier.size(iconSize),
                tint = if (isFilled) Accent else BorderLight
            )
        }
    }
}
