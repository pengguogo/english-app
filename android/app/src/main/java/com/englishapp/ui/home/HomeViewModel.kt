package com.englishapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishapp.data.model.Theme
import com.englishapp.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页 UI 状态。
 * @param themes 主题列表
 * @param isLoading 是否加载中
 * @param error 错误信息,null 表示无错误
 */
data class HomeUiState(
    val themes: List<Theme> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 首页 ViewModel。
 * 加载主题列表,管理加载/错误状态。
 * @author englishapp
 * @since 1.0.0
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /** 加载所有主题 */
    fun loadThemes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val themes = themeRepository.getThemes()
                _uiState.value = HomeUiState(themes = themes, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = "加载失败,请下拉刷新重试"
                )
            }
        }
    }
}
