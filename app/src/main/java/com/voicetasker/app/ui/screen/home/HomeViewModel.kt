package com.voicetasker.app.ui.screen.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicetasker.app.domain.model.Category
import com.voicetasker.app.domain.model.Note
import com.voicetasker.app.domain.usecase.category.GetCategoriesUseCase
import com.voicetasker.app.domain.usecase.note.DeleteNoteUseCase
import com.voicetasker.app.domain.usecase.note.GetNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val notes: List<Note> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val isLoading: Boolean = true,
    val isPremium: Boolean = false,
    val freeNotesRemaining: Int = 5
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        getNotesUseCase(),
        getCategoriesUseCase(),
        _searchQuery,
        _selectedCategoryId
    ) { notes, categories, query, categoryId ->
        val filteredNotes = notes
            .filter { note ->
                if (query.isBlank()) true
                else note.title.contains(query, ignoreCase = true) ||
                        note.transcription.contains(query, ignoreCase = true)
            }
            .filter { note ->
                if (categoryId == null) true
                else note.categoryId == categoryId
            }

        HomeUiState(
            notes = filteredNotes,
            categories = categories,
            searchQuery = query,
            selectedCategoryId = categoryId,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryFilterChanged(categoryId: Long?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
        }
    }

    fun getCategoryColor(categoryId: Long): Color {
        val hex = uiState.value.categories.find { it.id == categoryId }?.colorHex ?: "#6C63FF"
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (_: Exception) {
            Color(0xFF6C63FF)
        }
    }

    fun getCategoryName(categoryId: Long): String {
        return uiState.value.categories.find { it.id == categoryId }?.name ?: "Senza categoria"
    }
}
