package com.englishapp.ui.lessonlearn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishapp.audio.AudioPlayer
import com.englishapp.audio.WavRecorder
import com.englishapp.data.model.Lesson
import com.englishapp.repository.LessonRepository
import com.englishapp.repository.ProgressRepository
import com.englishapp.repository.VoiceRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 单个学习项(单词或句型)。
 * @param text 英文文本(单词或句子)
 * @param emoji 表情符号
 * @param phonetic 音标
 * @param translation 中文释义
 */
data class LearnItem(
    val text: String,
    val emoji: String,
    val phonetic: String,
    val translation: String
)

/**
 * 核心学习页 UI 状态。
 */
data class LessonLearnUiState(
    val isLoading: Boolean = false,
    val lesson: Lesson? = null,
    val items: List<LearnItem> = emptyList(),
    val currentIndex: Int = 0,
    val currentScore: Int? = null,
    val currentStars: Int = 0,
    val scoreFeedback: String = "",
    val isScoring: Boolean = false,
    val isPlayingTts: Boolean = false,
    val isRecording: Boolean = false,
    val isComplete: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null
) {
    /** 当前学习项 */
    val currentItem: LearnItem? get() = items.getOrNull(currentIndex)

    /** 学习项总数 */
    val totalItems: Int get() = items.size

    /** 进度百分比 */
    val progressPercent: Float
        get() = if (totalItems == 0) 0f else (currentIndex + 1f) / totalItems

    /** 是否最后一项 */
    val isLastItem: Boolean get() = currentIndex >= totalItems - 1
}

/**
 * 核心学习页 ViewModel。
 * 实现"听发音 → 跟读录音 → 评分 → 切换下一项 → 完成课时"的完整学习闭环。
 * @author englishapp
 * @since 1.0.0
 */
@HiltViewModel
class LessonLearnViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository,
    private val voiceRepository: VoiceRepository,
    private val wavRecorder: WavRecorder,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonLearnUiState(isLoading = true))
    val uiState: StateFlow<LessonLearnUiState> = _uiState.asStateFlow()

    /** 记录每个学习项的历史最佳分,用于最终结算平均 */
    private val bestScores = mutableListOf<Int>()

    /** 根据分数计算星级(0-3),阈值 80/60/40 */
    private fun scoreToStars(score: Int): Int = when {
        score >= 80 -> 3
        score >= 60 -> 2
        score >= 40 -> 1
        else -> 0
    }

    /** 整个课时累计最佳分数(各 item 历史最佳的平均值) */
    private fun totalBestScore(): Int {
        if (bestScores.isEmpty()) return 0
        return bestScores.sum() / bestScores.size
    }

    /** 整个课时最终获得的星星数 */
    private fun totalStars(): Int = scoreToStars(totalBestScore())

    /**
     * 加载课时详情并解析 content。
     * @param lessonId 课时 ID
     */
    fun loadLesson(lessonId: Int) {
        viewModelScope.launch {
            _uiState.value = LessonLearnUiState(isLoading = true)
            try {
                val lesson = lessonRepository.getLessonById(lessonId)
                val items = parseContent(lesson.content)
                bestScores.clear()
                repeat(items.size) { bestScores.add(0) }
                _uiState.value = LessonLearnUiState(
                    isLoading = false,
                    lesson = lesson,
                    items = items
                )
            } catch (e: Exception) {
                _uiState.value = LessonLearnUiState(
                    isLoading = false,
                    error = "加载课时失败,请返回重试"
                )
            }
        }
    }

    /**
     * 解析后端 content JSON,统一转换为 LearnItem 列表。
     * 单词课: {"words": ["apple", ...]}
     * 句型课: {"sentences": ["I have a car", ...]}
     */
    private fun parseContent(content: String): List<LearnItem> {
        val gson = Gson()
        val json: JsonObject = gson.fromJson(content, JsonObject::class.java) ?: return emptyList()
        val items = mutableListOf<LearnItem>()

        json.getAsJsonArray("words")?.forEach { elem ->
            val word = elem.asString
            val meta = WORD_META_MAP[word.lowercase()]
            items.add(LearnItem(
                text = word,
                emoji = meta?.emoji ?: "🔤",
                phonetic = meta?.phonetic ?: "",
                translation = meta?.translation ?: ""
            ))
        }

        json.getAsJsonArray("sentences")?.forEach { elem ->
            items.add(LearnItem(
                text = elem.asString,
                emoji = "💬",
                phonetic = "",
                translation = ""
            ))
        }

        return items
    }

    /**
     * 播放当前学习项的英文发音(TTS)。
     * 调用后端 /voice/tts 接口获取音频并播放。
     */
    fun playTts() {
        val text = _uiState.value.currentItem?.text ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPlayingTts = true)
            try {
                val audioData = voiceRepository.textToSpeech(text)
                if (audioData != null && audioData.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        audioPlayer.play(audioData, onComplete = {
                            _uiState.value = _uiState.value.copy(isPlayingTts = false)
                        })
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isPlayingTts = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isPlayingTts = false)
            }
        }
    }

    /**
     * 开始/停止录音。
     * 开始录音时启动 WavRecorder;停止录音时获取 WAV 数据并调用评分接口。
     */
    fun toggleRecording() {
        if (_uiState.value.isRecording) {
            stopRecordingAndScore()
        } else {
            startRecording()
        }
    }

    /** 开始录音 */
    private fun startRecording() {
        try {
            wavRecorder.start()
            _uiState.value = _uiState.value.copy(isRecording = true)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = "无法启动录音,请检查麦克风权限")
        }
    }

    /** 停止录音并调用发音评测 */
    private fun stopRecordingAndScore() {
        val text = _uiState.value.currentItem?.text ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRecording = false, isScoring = true)
            try {
                val wavData = withContext(Dispatchers.IO) { wavRecorder.stop() }
                val result = voiceRepository.scorePronunciation(wavData, text)
                val index = _uiState.value.currentIndex
                // 更新历史最佳分
                if (index < bestScores.size) {
                    bestScores[index] = maxOf(bestScores[index], result.score)
                }
                _uiState.value = _uiState.value.copy(
                    isScoring = false,
                    currentScore = result.score,
                    currentStars = scoreToStars(result.score),
                    scoreFeedback = result.feedback
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isScoring = false,
                    scoreFeedback = "评分失败,请重试"
                )
            }
        }
    }

    /** 切换到下一个学习项,若已是最后一项则进入完成结算 */
    fun nextItem() {
        val state = _uiState.value
        if (state.isLastItem) {
            _uiState.value = state.copy(isComplete = true)
        } else {
            _uiState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                currentScore = null,
                currentStars = 0,
                scoreFeedback = ""
            )
        }
    }

    /**
     * 完成课时:提交进度(星星 + 分数)。
     * @param lessonId 课时 ID
     * @param onSuccess 提交成功回调
     */
    fun finishLesson(lessonId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            try {
                progressRepository.completeLesson(lessonId, totalStars(), totalBestScore())
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    error = "保存失败,请重试"
                )
            }
        }
    }

    /** 清除错误信息 */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
        if (wavRecorder.isRecording()) {
            wavRecorder.stop()
        }
    }

    companion object {
        /** 单词到 emoji/音标/释义的映射表(与 Web 端一致) */
        private val WORD_META_MAP = mapOf(
            "apple" to WordMeta("🍎", "ˈæpl", "苹果"),
            "banana" to WordMeta("🍌", "bəˈnɑːnə", "香蕉"),
            "orange" to WordMeta("🍊", "ˈɒrɪndʒ", "橙子"),
            "grape" to WordMeta("🍇", "greɪp", "葡萄"),
            "car" to WordMeta("🚗", "kɑː", "小汽车"),
            "bus" to WordMeta("🚌", "bʌs", "公交车"),
            "bike" to WordMeta("🚲", "baɪk", "自行车"),
            "train" to WordMeta("🚂", "treɪn", "火车"),
            "plane" to WordMeta("✈️", "pleɪn", "飞机"),
            "helicopter" to WordMeta("🚁", "ˈhelɪkɒptə", "直升机"),
            "balloon" to WordMeta("🎈", "bəˈluːn", "热气球"),
            "rocket" to WordMeta("🚀", "ˈrɒkɪt", "火箭"),
            "boat" to WordMeta("⛵", "bəʊt", "小船"),
            "ship" to WordMeta("🚢", "ʃɪp", "大船"),
            "submarine" to WordMeta("🤿", "ˌsʌbməˈriːn", "潜水艇")
        )
    }
}

/** 单词元数据(emoji + 音标 + 中文释义) */
private data class WordMeta(val emoji: String, val phonetic: String, val translation: String)
