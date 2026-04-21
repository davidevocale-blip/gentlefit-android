package com.voicetasker.app.ui.screen.notedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.model.Note
import com.voicetasker.app.domain.model.Reminder
import com.voicetasker.app.domain.model.ReminderType
import com.voicetasker.app.domain.repository.ReminderRepository
import com.voicetasker.app.domain.usecase.category.GetCategoriesUseCase
import com.voicetasker.app.domain.usecase.note.DeleteNoteUseCase
import com.voicetasker.app.domain.usecase.note.UpdateNoteUseCase
import com.voicetasker.app.domain.usecase.reminder.CancelReminderUseCase
import com.voicetasker.app.domain.usecase.reminder.ScheduleReminderUseCase
import com.voicetasker.app.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteDetailUiState(
    val note: Note? = null,
    val categories: List<Category> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val isEditing: Boolean = false,
    val editTitle: String = "",
    val editTranscription: String = "",
    val editCategoryId: Long? = null,
    val editScheduledDate: Long = 0,
    val isDeleted: Boolean = false
)

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    private val reminderRepository: ReminderRepository,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val cancelReminderUseCase: CancelReminderUseCase,
    getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val noteId: Long = savedStateHandle.get<Long>("noteId") ?: 0L
    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { noteRepository.getNoteById(noteId).collect { note -> note?.let { _uiState.update { s -> s.copy(note = note) } } } }
        viewModelScope.launch { getCategoriesUseCase().collect { cats -> _uiState.update { it.copy(categories = cats) } } }
        viewModelScope.launch { reminderRepository.getRemindersForNote(noteId).collect { rems -> _uiState.update { it.copy(reminders = rems) } } }
    }

    fun startEditing() {
        val note = _uiState.value.note ?: return
        _uiState.update { it.copy(isEditing = true, editTitle = note.title, editTranscription = note.transcription, editCategoryId = note.categoryId, editScheduledDate = note.scheduledDate) }
    }

    fun onEditTitleChanged(t: String) { _uiState.update { it.copy(editTitle = t) } }
    fun onEditTranscriptionChanged(t: String) { _uiState.update { it.copy(editTranscription = t) } }
    fun onEditCategoryChanged(id: Long) { _uiState.update { it.copy(editCategoryId = id) } }
    fun onEditDateChanged(d: Long) { _uiState.update { it.copy(editScheduledDate = d) } }

    fun saveEdits() {
        val note = _uiState.value.note ?: return
        val state = _uiState.value
        viewModelScope.launch {
            updateNoteUseCase(note.copy(title = state.editTitle, transcription = state.editTranscription, categoryId = state.editCategoryId ?: note.categoryId, scheduledDate = state.editScheduledDate, updatedAt = System.currentTimeMillis()))
            _uiState.update { it.copy(isEditing = false) }
        }
    }

    fun cancelEditing() { _uiState.update { it.copy(isEditing = false) } }

    fun deleteNote() {
        viewModelScope.launch { deleteNoteUseCase(noteId); _uiState.update { it.copy(isDeleted = true) } }
    }

    fun addReminder(type: ReminderType) {
        val note = _uiState.value.note ?: return
        viewModelScope.launch { scheduleReminderUseCase(noteId, note.scheduledDate, type) }
    }

    fun removeReminder(reminderId: Long) {
        viewModelScope.launch { cancelReminderUseCase(reminderId) }
    }
}
