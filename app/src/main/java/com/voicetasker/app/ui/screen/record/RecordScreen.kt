package com.voicetasker.app.ui.screen.record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.ITALIAN)

    LaunchedEffect(uiState.isSaved) { if (uiState.isSaved) onNavigateBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registra nota") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Indietro") }
                },
                actions = {
                    if (!uiState.isRecording && uiState.audioFilePath != null) {
                        IconButton(onClick = viewModel::saveNote) { Icon(Icons.Filled.Check, "Salva", tint = MaterialTheme.colorScheme.primary) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Waveform
            WaveformVisualizer(amplitudes = uiState.amplitudes, isActive = uiState.isRecording, barColor = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))

            // Timer
            val minutes = (uiState.recordingDurationMs / 60000).toInt()
            val seconds = ((uiState.recordingDurationMs % 60000) / 1000).toInt()
            Text(String.format("%02d:%02d", minutes, seconds), style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(24.dp))

            // Record button
            RecordButton(isRecording = uiState.isRecording, onClick = { if (uiState.isRecording) viewModel.stopRecording() else viewModel.startRecording() })
            Spacer(Modifier.height(8.dp))
            Text(if (uiState.isRecording) "Tocca per fermare" else if (uiState.audioFilePath != null) "Registrazione completata" else "Tocca per registrare",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Post-recording form
            if (!uiState.isRecording && uiState.audioFilePath != null) {
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(Modifier.height(16.dp))

                // Title
                OutlinedTextField(value = uiState.title, onValueChange = viewModel::onTitleChanged, label = { Text("Titolo") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(12.dp))

                // Transcription
                OutlinedTextField(value = uiState.transcription, onValueChange = viewModel::onTranscriptionChanged,
                    label = { Text("Trascrizione") }, modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = MaterialTheme.shapes.medium)
                if (uiState.isTranscribing) {
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(16.dp))

                // Category selection
                Text("Categoria", style = MaterialTheme.typography.titleMedium)
                if (uiState.suggestedCategoryId != null) {
                    val sugName = uiState.categories.find { it.id == uiState.suggestedCategoryId }?.name ?: ""
                    Text("Suggerita: $sugName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    uiState.categories.forEach { cat ->
                        val c = try { Color(android.graphics.Color.parseColor(cat.colorHex)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                        CategoryChip(cat.name, c, isSelected = uiState.selectedCategoryId == cat.id, onClick = { viewModel.onCategorySelected(cat.id) })
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Date picker
                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                    Icon(Icons.Filled.CalendarMonth, null)
                    Spacer(Modifier.width(8.dp))
                    Text(dateFormat.format(Date(uiState.scheduledDate)))
                }
                Spacer(Modifier.height(16.dp))

                // Reminders
                ReminderPicker(selectedTypes = uiState.selectedReminders, onTypeToggled = viewModel::onReminderToggled)
                Spacer(Modifier.height(24.dp))

                // Save button
                Button(onClick = viewModel::saveNote, modifier = Modifier.fillMaxWidth().height(52.dp), shape = MaterialTheme.shapes.medium) {
                    Text("Salva nota", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(32.dp))
            }

            uiState.errorMessage?.let { error ->
                Spacer(Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.scheduledDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.onScheduledDateChanged(it) }; showDatePicker = false }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Annulla") } }
        ) { DatePicker(state = datePickerState) }
    }
}
