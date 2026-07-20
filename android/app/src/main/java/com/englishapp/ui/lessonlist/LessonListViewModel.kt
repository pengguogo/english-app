package com.englishapp.ui.lessonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishapp.data.model.Lesson
import com.englishapp.repository.LessonRepository
import com.englishapp.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 课时列表页 UI 状态。
 * @param lessons 课时列表
 * @param completedLessonIds 已完成课时的 ID 集合(用于解锁判断)
 * @param isLoading 是否加载中
 * @param error 错误信息
 */
data class LessonListUiState(
    val lessons: List<Lesson> = emptyList(),
    val completedLessonIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /**
     * 判断指定索引的课时是否可学。
     * 第一个课时始终可学,后续课时需要前一个已完成。
     */
    fun isAvailable(index: Int): Boolean {
        if (index == 0) return true
        val prevLesson = lessons.getOrNull(index - 1) ?: return false
        return completedLessonIds.contains(prevLesson.id)
    }
}

/**
 * 课时列表页 ViewModel。
 * 加载某单元下的所有课时,并查询每节课的完成状态。
 * @author englishapp
 * @since 1.0.0
 */
@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonListUiState(isLoading = true))
    val uiState: StateFlow<LessonListUiState> = _uiState.asStateFlow()

    /**
     * 加载某单元下的所有课时及进度。
     * @param unitId 单元 ID
     */
    fun loadLessons(unitId: Int) {
        viewModelScope.launch {
            _uiState.value = LessonListUiState(isLoading = true)
            try {
                val lessons = lessonRepository.getLessonsByUnit(unitId)
                // 查询每节课的完成状态
                val completedIds = mutableSetOf<Int>()
                lessons.forEach { lesson ->
                    val progress = progressRepository.getLessonProgress(lesson.id)
                    if (progress?.status == "COMPLETED") {
                        completedIds.add(lesson.id)
                    }
                }
                _uiState.value = LessonListUiState(
                    lessons = lessons,
                    completedLessonIds = completedIds,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = LessonListUiState(
                    isLoading = false,
                    error = "加载失败,请返回重试"
                )
            }
        }
    }
}
