package com.englishapp.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.englishapp.ui.theme.Accent
import com.englishapp.ui.theme.Primary
import com.englishapp.ui.theme.Success
import com.englishapp.ui.theme.Warning

/**
 * 按钮样式枚举。
 * 对应 Web 端 AppButton 的 variant 属性。
 */
enum class ButtonVariant { PRIMARY, SUCCESS, WARNING, GHOST }

/**
 * 按钮尺寸枚举。
 * 对应 Web 端 AppButton 的 size 属性。
 */
enum class ButtonSize { MD, LG }

/**
 * 统一按钮组件。
 * 支持四种样式(primary/success/warning/ghost)和两种尺寸(md/lg),
 * 胶囊形大圆角,儿童友好的视觉风格。
 * @author englishapp
 * @since 1.0.0
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.MD,
    enabled: Boolean = true,
    loading: Boolean = false,
    block: Boolean = false
) {
    val buttonModifier = if (block) modifier else modifier
    val finalModifier = if (block) buttonModifier else buttonModifier

    val colors = when (variant) {
        ButtonVariant.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = Color.White,
            disabledContainerColor = Primary.copy(alpha = 0.4f),
            disabledContentColor = Color.White
        )
        ButtonVariant.SUCCESS -> ButtonDefaults.buttonColors(
            containerColor = Success,
            contentColor = Color.White,
            disabledContainerColor = Success.copy(alpha = 0.4f),
            disabledContentColor = Color.White
        )
        ButtonVariant.WARNING -> ButtonDefaults.buttonColors(
            containerColor = Warning,
            contentColor = Color.White,
            disabledContainerColor = Warning.copy(alpha = 0.4f),
            disabledContentColor = Color.White
        )
        ButtonVariant.GHOST -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Primary
        )
    }

    val height = when (size) {
        ButtonSize.MD -> 44.dp
        ButtonSize.LG -> 52.dp
    }

    val fontSize = when (size) {
        ButtonSize.MD -> 14.sp
        ButtonSize.LG -> 16.sp
    }

    Button(
        onClick = onClick,
        modifier = finalModifier.height(height),
        enabled = enabled && !loading,
        colors = colors,
        shape = RoundedCornerShape(50)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = text, fontSize = fontSize)
        }
    }
}
