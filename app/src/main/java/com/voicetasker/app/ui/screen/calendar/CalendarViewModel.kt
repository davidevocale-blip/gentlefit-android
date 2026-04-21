package com.voicetasker.app.ui.screen.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.model.Note
import com.voicetasker.app.domain.usecase.category.GetCategoriesUseCase
import com.voicetasker.app.domain.usecase.note.GetNotesByDateUseCase
import com.voicetasker.app.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class CalendarUiState(
    val selectedDate: Long = System.currentTimeMillis(),
    val currentMonth: Calendar = Calendar.getInstance(),
    val notesForSelectedDate: List<Note> = emptyList(),
    val daysWithNotes: Set<Int> = emptySet(),
    val categories: List<Category> = emptyList()
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getNotesByDateUseCase: GetNotesByDateUseCase,
    private val noteRepository: NoteRepository,
    getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { getCategoriesUseCase().collect { cats -> _uiState.update { it.copy(categories = cats) } } }
        loadNotesForDate(System.currentTimeMillis())
        loadDaysWithNotes()
    }

    fun onDateSelected(dateMillis: Long) {
        _uiState.update { it.copy(selectedDate = dateMillis) }
        loadNotesForDate(dateMillis)
    }

    fun onMonthChanged(offset: Int) {
        val cal = _uiState.value.currentMonth.clone() as Calendar
        cal.add(Calendar.MONTH, offset)
        _uiState.update { it.copy(currentMonth = cal) }
        loadDaysWithNotes()
    }

    private fun loadNotesForDate(dateMillis: Long) {
        viewModelScope.launch {
            getNotesByDateUseCase(dateMillis).collect { notes ->
                _uiState.update { it.copy(notesForSelectedDate = notes) }
            }
        }
    }

    private fun loadDaysWithNotes() {
        val cal = _uiState.value.currentMonth.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis
        viewModelScope.launch {
            noteRepository.getDaysWithNotes(start, end).collect { dayEpochs ->
                val days = dayEpochs.map { epoch ->
                    val c = Calendar.getInstance(); c.timeInMillis = epoch * 86400000L; c.get(Calendar.DAY_OF_MONTH)
                }.toSet()
                _uiState.update { it.copy(daysWithNotes = days) }
            }
        }
    }

    fun getCategoryColor(categoryId: Long): androidx.compose.ui.graphics.Color {
        val hex = uiState.value.categories.find { it.id == categoryId }?.colorHex ?: "#6C63FF"
        return try { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { androidx.compose.ui.graphics.Color(0xFF6C63FF) }
    }

    fun getCategoryName(categoryId: Long): String = uiState.value.categories.find { it.id == categoryId }?.name ?: ""
}
