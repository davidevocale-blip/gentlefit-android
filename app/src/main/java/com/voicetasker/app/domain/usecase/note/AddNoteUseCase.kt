package com.voicetasker.app.domain.usecase.note

import com.voicetasker.app.domain.model.Note
import com.voicetasker.app.domain.repository.NoteRepository
import javax.inject.Inject

/** Adds a new voice note to the database. Returns the inserted note ID. */
class AddNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Long {
        return noteRepository.insertNote(note)
    }
}
