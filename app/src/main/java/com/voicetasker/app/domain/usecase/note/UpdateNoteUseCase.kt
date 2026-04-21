package com.voicetasker.app.domain.usecase.note

import com.voicetasker.app.domain.model.Note
import com.voicetasker.app.domain.repository.NoteRepository
import javax.inject.Inject

/** Updates an existing note. */
class UpdateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        noteRepository.updateNote(note)
    }
}
