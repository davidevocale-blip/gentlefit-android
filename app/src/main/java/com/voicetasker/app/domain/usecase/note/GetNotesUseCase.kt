package com.voicetasker.app.domain.usecase.note

import com.voicetasker.app.domain.model.Note
import com.voicetasker.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Retrieves all notes, optionally filtered by search query. */
class GetNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(searchQuery: String = ""): Flow<List<Note>> {
        return if (searchQuery.isBlank()) {
            noteRepository.getAllNotes()
        } else {
            noteRepository.searchNotes(searchQuery)
        }
    }
}
