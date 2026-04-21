package com.voicetasker.app.ui.screen.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voicetasker.app.ui.components.CalendarView
import com.voicetasker.app.ui.components.NoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToNoteDetail: (Long) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(2.dp)) {
                    CalendarView(
                        selectedDate = uiState.selectedDate,
                        currentMonth = uiState.currentMonth,
                        daysWithNotes = uiState.daysWithNotes,
                        onDateSelected = viewModel::onDateSelected,
                        onMonthChanged = viewModel::onMonthChanged,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                Text("Note del giorno", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (uiState.notesForSelectedDate.isEmpty()) {
                item {
                    Text("Nessun impegno per questa data", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp))
                }
            } else {
                items(uiState.notesForSelectedDate, key = { it.id }) { note ->
                    NoteCard(note, viewModel.getCategoryColor(note.categoryId), viewModel.getCategoryName(note.categoryId), onClick = { onNavigateToNoteDetail(note.id) })
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
