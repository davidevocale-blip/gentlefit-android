package com.voicetasker.app.domain.usecase.note

import com.voicetasker.app.domain.model.Note
import com.voicetasker.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Retrieves notes for a specific date. */
class GetNotesByDateUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(dateMillis: Long): Flow<List<Note>> {
        return noteRepository.getNotesForDate(dateMillis)
    }
}
