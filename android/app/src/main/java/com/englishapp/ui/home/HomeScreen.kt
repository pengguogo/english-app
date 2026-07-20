package com.englishapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.englishapp.R
import com.englishapp.data.model.Theme
import com.englishapp.ui.theme.CardBackground
import com.englishapp.ui.theme.GradientPrimaryEnd
import com.englishapp.ui.theme.GradientPrimaryStart
import com.englishapp.ui.theme.MutedBackground
import com.englishapp.ui.theme.Primary
import com.englishapp.ui.theme.TextTertiary
import com.englishapp.ui.theme.Warning

/**
 * 首页:展示学习主题列表。
 * 吉祥物欢迎 header + 主题卡片网格,儿童友好的视觉风格。
 * 对应 Web 端 HomeView.vue。
 * @author englishapp
 * @since 1.0.0
 */
@Composable
fun HomeScreen(
    onThemeClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadThemes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        WelcomeHeader()

        Text(
            text = "选择主题",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp)
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            uiState.error != null -> {
                Text(
                    text = uiState.error!!,
                    color = Warning,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                // 两列网格:手动分行避免嵌套滚动
                uiState.themes.chunked(2).forEach { rowThemes ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowThemes.forEach { theme ->
                            ThemeCard(
                                theme = theme,
                                modifier = Modifier.weight(1f),
                                onClick = { onThemeClick(theme.id) }
                            )
                        }
                        // 奇数个时填充占位
                        if (rowThemes.size == 1) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

/**
 * 欢迎头部:渐变背景 + 吉祥物 + 欢迎文案。
 */
@Composable
private fun WelcomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "嗨,小朋友!",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "今天一起学英语吧 🎈",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.mascot_welcome),
                contentDescription = "小老鼠 Mimi",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

/**
 * 单个主题卡片。
 */
@Composable
private fun ThemeCard(
    theme: Theme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable(enabled = !theme.isLocked) { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = getThemeIcon(theme.id), fontSize = 48.sp)
            Text(
                text = theme.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = if (theme.isLocked) "🔒 待解锁" else "点击进入 →",
                style = MaterialTheme.typography.bodySmall,
                color = if (theme.isLocked) TextTertiary else Primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/** 根据主题 ID 返回 emoji 图标 */
private fun getThemeIcon(id: Int): String = when (id) {
    1 -> "🍎"
    2 -> "🚗"
    else -> "📚"
}
