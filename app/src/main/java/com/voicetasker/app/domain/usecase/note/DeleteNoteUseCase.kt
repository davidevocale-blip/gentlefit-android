package com.voicetasker.app.domain.usecase.note

import com.voicetasker.app.domain.repository.NoteRepository
import com.voicetasker.app.domain.repository.ReminderRepository
import javax.inject.Inject

/** Deletes a note and all its associated reminders. */
class DeleteNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(noteId: Long) {
        // Cancel all WorkManager tasks for this note's reminders first
        val reminders = reminderRepository.getRemindersForNote(noteId)
        // Note: we handle the cancellation in the repository impl
        reminderRepository.deleteRemindersForNote(noteId)
        noteRepository.deleteNote(noteId)
    }
}
