package com.englishapp.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishapp.data.model.Lesson
import com.englishapp.data.remote.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons

    fun loadLessons(unitId: Int) {
        viewModelScope.launch {
            try {
                _lessons.value = RetrofitClient.apiService.getLessonsByUnit(unitId)
            } catch (e: Exception) {
                _lessons.value = emptyList()
            }
        }
    }
}
