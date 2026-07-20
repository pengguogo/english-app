package com.englishapp.ui.unitlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishapp.data.model.Unit
import com.englishapp.repository.UnitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 单元列表页 UI 状态。
 */
data class UnitListUiState(
    val units: List<Unit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 单元列表页 ViewModel。
 * 根据主题 ID 加载单元列表(含进度)。
 * @author englishapp
 * @since 1.0.0
 */
@HiltViewModel
class UnitListViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnitListUiState(isLoading = true))
    val uiState: StateFlow<UnitListUiState> = _uiState.asStateFlow()

    /**
     * 加载某主题下的所有单元。
     * @param themeId 主题 ID
     */
    fun loadUnits(themeId: Int) {
        viewModelScope.launch {
            _uiState.value = UnitListUiState(isLoading = true)
            try {
                val units = unitRepository.getUnitsByTheme(themeId)
                _uiState.value = UnitListUiState(units = units, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = UnitListUiState(
                    isLoading = false,
                    error = "加载失败,请返回重试"
                )
            }
        }
    }
}
