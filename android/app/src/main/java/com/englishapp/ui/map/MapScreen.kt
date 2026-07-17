package com.englishapp.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.englishapp.data.model.Lesson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    unitId: Int = 1,
    viewModel: MapViewModel = hiltViewModel()
) {
    val lessons by viewModel.lessons.collectAsState()

    LaunchedEffect(unitId) {
        viewModel.loadLessons(unitId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("学习地图") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lessons) { lesson ->
                LessonItem(lesson = lesson)
            }
        }
    }
}

@Composable
fun LessonItem(lesson: Lesson) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = lesson.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "类型: ${lesson.type}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
