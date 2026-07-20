package com.englishapp.ui.lessonlearn

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.englishapp.R
import com.englishapp.components.AppButton
import com.englishapp.components.BackBar
import com.englishapp.components.ButtonSize
import com.englishapp.components.ButtonVariant
import com.englishapp.components.StarBar
import com.englishapp.components.StarSize
import com.englishapp.ui.theme.Accent
import com.englishapp.ui.theme.CardBackground
import com.englishapp.ui.theme.GradientPrimaryEnd
import com.englishapp.ui.theme.GradientPrimaryStart
import com.englishapp.ui.theme.MutedBackground
import com.englishapp.ui.theme.Orange
import com.englishapp.ui.theme.Primary
import com.englishapp.ui.theme.Success
import com.englishapp.ui.theme.TextSecondary
import com.englishapp.ui.theme.TextTertiary
import com.englishapp.ui.theme.Warning

/**
 * 核心学习页:听发音 → 跟读录音 → 评分 → 切换下一项 → 完成结算。
 * 完整的学习闭环,对接后端 TTS 与发音评测 API。
 * 对应 Web 端 LessonView.vue。
 * @author englishapp
 * @since 1.0.0
 */
@Composable
fun LessonLearnScreen(
    lessonId: Int,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    viewModel: LessonLearnViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    when {
        uiState.isLoading -> LoadingView()
        uiState.isComplete -> CompletionScreen(
            state = uiState,
            onComplete = { viewModel.finishLesson(lessonId, onComplete) }
        )
        else -> LearnContent(
            state = uiState,
            onPlayTts = { viewModel.playTts() },
            onRecord = {
                if (hasPermission) {
                    viewModel.toggleRecording()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            onNext = { viewModel.nextItem() },
            onBack = onBack
        )
    }
}

/**
 * 学习内容主区:进度条 + 学习卡片 + TTS 按钮 + 录音按钮 + 评分结果。
 */
@Composable
private fun LearnContent(
    state: LessonLearnUiState,
    onPlayTts: () -> Unit,
    onRecord: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val item = state.currentItem ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BackBar(title = "学习中", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 进度条
            ProgressSection(
                current = state.currentIndex + 1,
                total = state.totalItems,
                progress = state.progressPercent
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 学习卡片:emoji + 英文 + 音标 + 中文
            LearnCard(item = item)

            Spacer(modifier = Modifier.height(32.dp))

            // TTS 听发音按钮
            TtsButton(isPlaying = state.isPlayingTts, onClick = onPlayTts)

            Spacer(modifier = Modifier.height(24.dp))

            // 录音按钮(大圆形)
            RecordButton(
                isRecording = state.isRecording,
                isScoring = state.isScoring,
                hasScore = state.currentScore != null,
                onClick = onRecord
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 评分结果
            if (state.currentScore != null) {
                ScoreResult(
                    score = state.currentScore,
                    stars = state.currentStars,
                    feedback = state.scoreFeedback
                )
                Spacer(modifier = Modifier.height(24.dp))
                // 下一题按钮
                AppButton(
                    text = if (state.isLastItem) "完成本课" else "下一题",
                    onClick = onNext,
                    variant = ButtonVariant.SUCCESS,
                    size = ButtonSize.LG,
                    block = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * 进度条区域。
 */
@Composable
private fun ProgressSection(current: Int, total: Int, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "第 $current / $total 题",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = Primary,
            trackColor = MutedBackground
        )
    }
}

/**
 * 学习卡片:大 emoji + 英文单词 + 音标 + 中文释义。
 */
@Composable
private fun LearnCard(item: LearnItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 吉祥物陪伴
            Image(
                painter = painterResource(id = R.drawable.mascot_companion),
                contentDescription = "Mimi 陪你学习",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 大 emoji
            Text(text = item.emoji, fontSize = 72.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // 英文单词
            Text(
                text = item.text,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            // 音标
            if (item.phonetic.isNotEmpty()) {
                Text(
                    text = "/${item.phonetic}/",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 中文释义
            if (item.translation.isNotEmpty()) {
                Text(
                    text = item.translation,
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * TTS 听发音按钮。
 */
@Composable
private fun TtsButton(isPlaying: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Primary.copy(alpha = 0.1f))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPlaying) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Primary,
                strokeWidth = 2.dp
            )
            Text(
                text = "播放中...",
                color = Primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        } else {
            Text(text = "🔊", fontSize = 24.sp)
            Text(
                text = "听发音",
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

/**
 * 录音按钮(大圆形,带脉冲动画)。
 */
@Composable
private fun RecordButton(
    isRecording: Boolean,
    isScoring: Boolean,
    hasScore: Boolean,
    onClick: () -> Unit
) {
    // 录音中按钮放大动画
    val scale by animateFloatAsState(
        targetValue = if (isRecording) 1.2f else 1f,
        animationSpec = tween(300),
        label = "record_scale"
    )

    Box(
        modifier = Modifier
            .size(96.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                when {
                    isScoring -> MutedBackground
                    isRecording -> Warning
                    hasScore -> Success
                    else -> Orange
                }
            )
            .clickable(enabled = !isScoring) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            isScoring -> CircularProgressIndicator(
                color = Primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
            isRecording -> Text(text = "⏹", fontSize = 32.sp, color = Color.White)
            else -> Text(text = "🎤", fontSize = 40.sp)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = when {
            isScoring -> "评分中..."
            isRecording -> "点击停止"
            else -> "点击录音"
        },
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
    )
}

/**
 * 评分结果:分数 + 星星 + 反馈文案。
 */
@Composable
private fun ScoreResult(score: Int, stars: Int, feedback: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 星星
            StarBar(stars = stars, size = StarSize.LG)

            Spacer(modifier = Modifier.height(12.dp))

            // 分数
            Text(
                text = "${score}分",
                style = MaterialTheme.typography.displayMedium,
                color = when {
                    score >= 80 -> Success
                    score >= 60 -> Primary
                    else -> Warning
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 反馈文案
            Text(
                text = feedback,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 完成结算页:吉祥物庆祝 + 星星 + 分数 + 完成按钮。
 */
@Composable
private fun CompletionScreen(
    state: LessonLearnUiState,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 吉祥物庆祝
        Image(
            painter = painterResource(id = R.drawable.mascot_celebrate),
            contentDescription = "Mimi 庆祝",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 标题
        Text(
            text = "太棒了!",
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "课程已完成 🎉",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 星星(大)
        StarBar(stars = 3, size = StarSize.LG)

        Spacer(modifier = Modifier.height(32.dp))

        // 完成按钮
        AppButton(
            text = if (state.isSubmitting) "保存中..." else "完成课程",
            onClick = onComplete,
            variant = ButtonVariant.SUCCESS,
            size = ButtonSize.LG,
            block = true,
            loading = state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 加载中视图。
 */
@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Primary)
    }
}
