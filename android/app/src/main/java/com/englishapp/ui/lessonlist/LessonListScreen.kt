package com.englishapp.ui.lessonlist

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.englishapp.components.BackBar
import com.englishapp.components.StarBar
import com.englishapp.data.model.Lesson
import com.englishapp.ui.theme.Accent
import com.englishapp.ui.theme.CardBackground
import com.englishapp.ui.theme.MutedBackground
import com.englishapp.ui.theme.Primary
import com.englishapp.ui.theme.Success
import com.englishapp.ui.theme.TextSecondary
import com.englishapp.ui.theme.TextTertiary
import com.englishapp.ui.theme.Warning

/**
 * 课时列表页:展示某单元下的所有课时及学习状态。
 * 每节课一张卡片,显示序号、名称、类型标签、解锁状态与已获星星。
 * 对应 Web 端 UnitView.vue。
 * @author englishapp
 * @since 1.0.0
 */
@Composable
fun LessonListScreen(
    unitId: Int,
    onBack: () -> Unit,
    onLessonClick: (Int) -> Unit,
    viewModel: LessonListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(unitId) {
        viewModel.loadLessons(unitId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BackBar(title = "课程列表", onBack = onBack)

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uiState.lessons) { index, lesson ->
                        LessonCard(
                            lesson = lesson,
                            index = index,
                            isAvailable = uiState.isAvailable(index),
                            isCompleted = uiState.completedLessonIds.contains(lesson.id),
                            onClick = { onLessonClick(lesson.id) }
                        )
                    }
                    // 底部留白
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

/**
 * 课时卡片:序号 + 名称 + 类型标签 + 星星 + 解锁状态。
 */
@Composable
private fun LessonCard(
    lesson: Lesson,
    index: Int,
    isAvailable: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val typeConfig = getLessonTypeConfig(lesson.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) CardBackground else MutedBackground
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧序号圆形
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isAvailable) typeConfig.color.copy(alpha = 0.15f) else MutedBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    color = if (isAvailable) typeConfig.color else TextTertiary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lesson.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isAvailable) MaterialTheme.colorScheme.onSurface else TextTertiary
                    )
                    // 类型标签
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(typeConfig.color.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = typeConfig.label,
                            color = typeConfig.color,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                // 子标题或状态
                Text(
                    text = if (isCompleted) "已完成" else if (isAvailable) "去学习" else "未解锁",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCompleted) Success else if (isAvailable) Primary else TextTertiary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 右侧状态:星星或锁
            if (isCompleted) {
                StarBar(stars = 3, size = com.englishapp.components.StarSize.SM)
            } else if (!isAvailable) {
                Text(text = "🔒", fontSize = 20.sp)
            }
        }
    }
}

/** 课时类型配置(标签 + 颜色) */
private data class LessonTypeConfig(val label: String, val color: Color)

private fun getLessonTypeConfig(type: String): LessonTypeConfig = when (type) {
    "word" -> LessonTypeConfig("单词", Success)
    "sentence" -> LessonTypeConfig("句型", Primary)
    else -> LessonTypeConfig("课程", Accent)
}
