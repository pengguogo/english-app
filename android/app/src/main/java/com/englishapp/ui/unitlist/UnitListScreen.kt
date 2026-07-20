package com.englishapp.ui.unitlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.englishapp.components.BackBar
import com.englishapp.data.model.Unit
import com.englishapp.ui.theme.CardBackground
import com.englishapp.ui.theme.GradientPrimaryEnd
import com.englishapp.ui.theme.GradientPrimaryStart
import com.englishapp.ui.theme.MutedBackground
import com.englishapp.ui.theme.Primary
import com.englishapp.ui.theme.Success
import com.englishapp.ui.theme.TextSecondary
import com.englishapp.ui.theme.TextTertiary
import com.englishapp.ui.theme.Warning

/**
 * 单元列表页:展示某主题下的所有单元及学习进度。
 * 场景 banner + 单元卡片列表(含进度条)。
 * 对应 Web 端 ThemeView.vue。
 * @author englishapp
 * @since 1.0.0
 */
@Composable
fun UnitListScreen(
    themeId: Int,
    onBack: () -> Unit,
    onUnitClick: (Int) -> Unit,
    viewModel: UnitListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(themeId) {
        viewModel.loadUnits(themeId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BackBar(title = "交通工具乐园", onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 场景 banner
            item { SceneBanner() }

            when {
                uiState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Primary)
                        }
                    }
                }
                uiState.error != null -> {
                    item {
                        Text(
                            text = uiState.error!!,
                            color = Warning,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    // 单元列表
                    itemsIndexed(uiState.units) { index, unit ->
                        UnitCard(
                            unit = unit,
                            index = index,
                            onClick = { onUnitClick(unit.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 场景 banner:渐变背景 + 标题 + 副标题。
 */
@Composable
private fun SceneBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🚗 交通工具乐园",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "探索陆地、天空和水上的交通工具",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * 单元卡片:左侧彩色竖条 + 图标 + 名称 + 进度条。
 */
@Composable
private fun UnitCard(
    unit: Unit,
    index: Int,
    onClick: () -> Unit
) {
    val sceneConfig = getSceneConfig(index)
    val isLocked = unit.isLocked
    val progress = if (unit.totalLessons > 0) {
        unit.completedLessons.toFloat() / unit.totalLessons
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked) { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧场景图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MutedBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(text = sceneConfig.icon, fontSize = 24.sp)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = unit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "已完成 ${unit.completedLessons} / ${unit.totalLessons} 课",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                // 进度条
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = sceneConfig.color,
                    trackColor = MutedBackground
                )
            }

            // 右侧状态图标
            Text(
                text = if (isLocked) "🔒" else if (progress >= 1f) "✓" else "▶",
                fontSize = 20.sp,
                color = if (progress >= 1f) Success else Primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

/** 场景配置(图标 + 主色调) */
private data class SceneConfig(val icon: String, val color: Color)

private fun getSceneConfig(index: Int): SceneConfig = when (index % 3) {
    0 -> SceneConfig("🚗", Success)
    1 -> SceneConfig("✈️", Primary)
    else -> SceneConfig("🚢", Primary)
}
