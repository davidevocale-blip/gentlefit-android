package com.voicetasker.app.domain.usecase.reminder

import com.voicetasker.app.domain.model.Reminder
import com.voicetasker.app.domain.model.ReminderType
import com.voicetasker.app.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Schedules a reminder for a note.
 * Calculates trigger time from the note's scheduled date and reminder type offset.
 * Enqueues a WorkManager task and stores the reminder in the database.
 */
class ScheduleReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(
        noteId: Long,
        scheduledDate: Long,
        reminderType: ReminderType
    ): Long {
        val triggerAt = scheduledDate - reminderType.offsetMillis

        // Don't schedule if trigger time is in the past
        if (triggerAt <= System.currentTimeMillis()) {
            return -1
        }

        val reminder = Reminder(
            noteId = noteId,
            triggerAt = triggerAt,
            type = reminderType
        )

        // Schedule WorkManager task
        val workRequestId = reminderRepository.scheduleReminderWork(reminder)

        // Save to database with the work request ID
        return reminderRepository.insertReminder(
            reminder.copy(workRequestId = workRequestId)
        )
    }
}
