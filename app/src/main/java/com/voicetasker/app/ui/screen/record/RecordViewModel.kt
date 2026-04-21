package com.voicetasker.app.ui.screen.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.voicetasker.app.data.recorder.AudioRecorderImpl
import com.voicetasker.app.data.recorder.SpeechTranscriberImpl
import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.model.Note
import com.voicetasker.app.domain.model.ReminderType
import com.voicetasker.app.domain.usecase.category.GetCategoriesUseCase
import com.voicetasker.app.domain.usecase.category.SuggestCategoryUseCase
import com.voicetasker.app.domain.usecase.note.AddNoteUseCase
import com.voicetasker.app.domain.usecase.reminder.ScheduleReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecordUiState(
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val recordingDurationMs: Long = 0,
    val transcription: String = "",
    val isTranscribing: Boolean = false,
    val title: String = "",
    val audioFilePath: String? = null,
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val suggestedCategoryId: Long? = null,
    val scheduledDate: Long = System.currentTimeMillis(),
    val selectedReminders: Set<ReminderType> = emptySet(),
    val amplitudes: List<Int> = emptyList(),
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    application: Application,
    private val audioRecorder: AudioRecorderImpl,
    private val speechTranscriber: SpeechTranscriberImpl,
    private val addNoteUseCase: AddNoteUseCase,
    private val suggestCategoryUseCase: SuggestCategoryUseCase,
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    getCategoriesUseCase: GetCategoriesUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun startRecording() {
        val path = audioRecorder.startRecording()
        if (path != null) {
            _uiState.update { it.copy(isRecording = true, audioFilePath = path, amplitudes = emptyList()) }
            startAmplitudePolling()
        }
        speechTranscriber.startListening()
        viewModelScope.launch {
            speechTranscriber.state.collect { state ->
                when (state) {
                    is SpeechTranscriberImpl.TranscriptionState.Result ->
                        _uiState.update { it.copy(transcription = state.text, isTranscribing = false) }
                    is SpeechTranscriberImpl.TranscriptionState.PartialResult ->
                        _uiState.update { it.copy(transcription = state.text) }
                    is SpeechTranscriberImpl.TranscriptionState.Listening ->
                        _uiState.update { it.copy(isTranscribing = true) }
                    is SpeechTranscriberImpl.TranscriptionState.Error ->
                        _uiState.update { it.copy(errorMessage = state.message, isTranscribing = false) }
                    else -> {}
                }
            }
        }
    }

    fun stopRecording() {
        val (path, duration) = audioRecorder.stopRecording()
        speechTranscriber.stopListening()
        _uiState.update { it.copy(isRecording = false, isPaused = false, recordingDurationMs = duration, audioFilePath = path) }
        viewModelScope.launch {
            val transcription = _uiState.value.transcription
            if (transcription.isNotBlank()) {
                val suggested = suggestCategoryUseCase(transcription)
                _uiState.update { it.copy(suggestedCategoryId = suggested?.id, selectedCategoryId = suggested?.id) }
            }
        }
    }

    private fun startAmplitudePolling() {
        viewModelScope.launch {
            while (_uiState.value.isRecording) {
                val amp = audioRecorder.getMaxAmplitude()
                _uiState.update { it.copy(amplitudes = it.amplitudes + amp, recordingDurationMs = it.recordingDurationMs + 100) }
                delay(100)
            }
        }
    }

    fun onTitleChanged(title: String) { _uiState.update { it.copy(title = title) } }
    fun onTranscriptionChanged(text: String) { _uiState.update { it.copy(transcription = text) } }
    fun onCategorySelected(id: Long) { _uiState.update { it.copy(selectedCategoryId = id) } }
    fun onScheduledDateChanged(date: Long) { _uiState.update { it.copy(scheduledDate = date) } }
    fun onReminderToggled(type: ReminderType) {
        _uiState.update { state ->
            val updated = state.selectedReminders.toMutableSet()
            if (type in updated) updated.remove(type) else updated.add(type)
            state.copy(selectedReminders = updated)
        }
    }

    fun saveNote() {
        viewModelScope.launch {
            val state = _uiState.value
            val now = System.currentTimeMillis()
            val note = Note(
                title = state.title.ifBlank { "Nota vocale" },
                transcription = state.transcription,
                audioFilePath = state.audioFilePath ?: "",
                categoryId = state.selectedCategoryId ?: 1,
                scheduledDate = state.scheduledDate,
                createdAt = now, updatedAt = now,
                durationMs = state.recordingDurationMs
            )
            val noteId = addNoteUseCase(note)
            state.selectedReminders.forEach { type ->
                scheduleReminderUseCase(noteId, state.scheduledDate, type)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechTranscriber.destroy()
    }
}
