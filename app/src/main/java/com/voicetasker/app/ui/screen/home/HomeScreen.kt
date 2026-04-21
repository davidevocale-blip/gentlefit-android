package com.voicetasker.app.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voicetasker.app.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRecord: () -> Unit,
    onNavigateToNoteDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VoiceTasker", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = { RecordButton(isRecording = false, onClick = onNavigateToRecord) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues)) {
            if (!uiState.isPremium) {
                PremiumBanner(uiState.freeNotesRemaining, onNavigateToSettings, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            OutlinedTextField(
                value = uiState.searchQuery, onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cerca nelle note...") },
                leadingIcon = { Icon(Icons.Filled.Search, "Cerca") },
                singleLine = true, shape = MaterialTheme.shapes.medium
            )
            if (uiState.categories.isNotEmpty()) {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                    items(uiState.categories) { cat ->
                        val color = try { Color(android.graphics.Color.parseColor(cat.colorHex)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                        CategoryChip(cat.name, color, isSelected = uiState.selectedCategoryId == cat.id, onClick = { viewModel.onCategoryFilterChanged(cat.id) })
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            if (uiState.notes.isEmpty() && !uiState.isLoading) {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.MicNone, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(Modifier.height(16.dp))
                        Text("Nessuna nota vocale", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Premi il pulsante per registrare", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.notes, key = { it.id }) { note ->
                        NoteCard(note, viewModel.getCategoryColor(note.categoryId), viewModel.getCategoryName(note.categoryId), onClick = { onNavigateToNoteDetail(note.id) })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}
