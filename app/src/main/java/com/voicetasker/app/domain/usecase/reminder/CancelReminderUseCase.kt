package com.voicetasker.app.domain.usecase.reminder

import com.voicetasker.app.domain.repository.ReminderRepository
import javax.inject.Inject

/** Cancels a scheduled reminder, removing both the WorkManager task and database entry. */
class CancelReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(reminderId: Long) {
        val reminder = reminderRepository.getReminderById(reminderId)
        if (reminder != null && reminder.workRequestId.isNotBlank()) {
            reminderRepository.cancelReminderWork(reminder.workRequestId)
        }
        reminderRepository.deleteReminder(reminderId)
    }
}
