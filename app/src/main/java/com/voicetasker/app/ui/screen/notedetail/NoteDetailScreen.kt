package com.voicetasker.app.ui.screen.notedetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voicetasker.app.domain.model.ReminderType
import com.voicetasker.app.ui.components.CategoryChip
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.ITALIAN)

    LaunchedEffect(uiState.isDeleted) { if (uiState.isDeleted) onNavigateBack() }

    val note = uiState.note
    if (note == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Modifica nota" else "Dettaglio nota") },
                navigationIcon = { IconButton(onClick = { if (uiState.isEditing) viewModel.cancelEditing() else onNavigateBack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro") } },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(onClick = viewModel::saveEdits) { Icon(Icons.Filled.Check, "Salva", tint = MaterialTheme.colorScheme.primary) }
                    } else {
                        IconButton(onClick = viewModel::startEditing) { Icon(Icons.Filled.Edit, "Modifica") }
                        IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Filled.Delete, "Elimina", tint = MaterialTheme.colorScheme.error) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            if (uiState.isEditing) {
                // Edit mode
                OutlinedTextField(value = uiState.editTitle, onValueChange = viewModel::onEditTitleChanged, label = { Text("Titolo") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = uiState.editTranscription, onValueChange = viewModel::onEditTranscriptionChanged, label = { Text("Trascrizione") }, modifier = Modifier.fillMaxWidth().height(150.dp), shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(16.dp))
                Text("Categoria", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.categories.forEach { cat ->
                        val c = try { Color(android.graphics.Color.parseColor(cat.colorHex)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                        CategoryChip(cat.name, c, isSelected = uiState.editCategoryId == cat.id, onClick = { viewModel.onEditCategoryChanged(cat.id) })
                    }
                }
            } else {
                // View mode
                Text(note.title.ifBlank { "Nota vocale" }, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                val catName = uiState.categories.find { it.id == note.categoryId }?.name ?: ""
                val catColor = uiState.categories.find { it.id == note.categoryId }?.colorHex ?: "#6C63FF"
                val color = try { Color(android.graphics.Color.parseColor(catColor)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                CategoryChip(catName, color)
                Spacer(Modifier.height(16.dp))

                // Date
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(dateFormat.format(Date(note.scheduledDate)), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Duration
                val min = (note.durationMs / 60000).toInt(); val sec = ((note.durationMs % 60000) / 1000).toInt()
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Timer, null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(8.dp))
                        Text("Durata: ${String.format("%02d:%02d", min, sec)}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Transcription
                Text("Trascrizione", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(1.dp), shape = MaterialTheme.shapes.medium) {
                    Text(note.transcription.ifBlank { "Nessuna trascrizione disponibile" }, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.height(16.dp))

                // Reminders
                Text("Promemoria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                if (uiState.reminders.isEmpty()) {
                    Text("Nessun promemoria impostato", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    uiState.reminders.forEach { rem ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.small, modifier = Modifier.padding(vertical = 2.dp)) {
                            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Notifications, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                                    Spacer(Modifier.width(8.dp))
                                    Text(rem.type.label, style = MaterialTheme.typography.bodyMedium)
                                }
                                if (!rem.isTriggered) { IconButton(onClick = { viewModel.removeReminder(rem.id) }, modifier = Modifier.size(24.dp)) { Icon(Icons.Filled.Close, "Rimuovi", Modifier.size(16.dp)) } }
                                else { Text("✓ Inviato", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary) }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                // Add reminder buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReminderType.entries.forEach { type ->
                        val alreadySet = uiState.reminders.any { it.type == type }
                        if (!alreadySet) {
                            AssistChip(onClick = { viewModel.addReminder(type) }, label = { Text(type.label, style = MaterialTheme.typography.labelSmall) }, leadingIcon = { Icon(Icons.Filled.Add, null, Modifier.size(14.dp)) })
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Elimina nota") },
            text = { Text("Sei sicuro di voler eliminare questa nota? L'azione non è reversibile.") },
            confirmButton = { TextButton(onClick = { showDeleteDialog = false; viewModel.deleteNote() }) { Text("Elimina", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Annulla") } }
        )
    }
}
