package com.voicetasker.app.domain.repository

import com.voicetasker.app.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for reminder operations.
 */
interface ReminderRepository {
    fun getRemindersForNote(noteId: Long): Flow<List<Reminder>>
    fun getPendingReminders(): Flow<List<Reminder>>
    suspend fun getReminderById(reminderId: Long): Reminder?
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminderId: Long)
    suspend fun deleteRemindersForNote(noteId: Long)
    suspend fun markAsTriggered(reminderId: Long)
    suspend fun scheduleReminderWork(reminder: Reminder): String
    suspend fun cancelReminderWork(workRequestId: String)
}
