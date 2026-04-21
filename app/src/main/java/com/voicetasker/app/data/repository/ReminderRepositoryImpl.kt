package com.voicetasker.app.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.voicetasker.app.data.local.dao.ReminderDao
import com.voicetasker.app.data.local.entity.ReminderEntity
import com.voicetasker.app.domain.model.Reminder
import com.voicetasker.app.domain.model.ReminderType
import com.voicetasker.app.domain.repository.ReminderRepository
import com.voicetasker.app.worker.ReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    @ApplicationContext private val context: Context
) : ReminderRepository {

    override fun getRemindersForNote(noteId: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersForNote(noteId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPendingReminders(): Flow<List<Reminder>> {
        return reminderDao.getPendingReminders(System.currentTimeMillis()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getReminderById(reminderId: Long): Reminder? {
        return reminderDao.getReminderById(reminderId)?.toDomain()
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder.toEntity())
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder.toEntity())
    }

    override suspend fun deleteReminder(reminderId: Long) {
        reminderDao.deleteReminderById(reminderId)
    }

    override suspend fun deleteRemindersForNote(noteId: Long) {
        reminderDao.deleteRemindersForNote(noteId)
    }

    override suspend fun markAsTriggered(reminderId: Long) {
        reminderDao.markAsTriggered(reminderId)
    }

    override suspend fun scheduleReminderWork(reminder: Reminder): String {
        val delay = reminder.triggerAt - System.currentTimeMillis()
        if (delay <= 0) return ""

        val inputData = Data.Builder()
            .putLong(ReminderWorker.KEY_NOTE_ID, reminder.noteId)
            .putLong(ReminderWorker.KEY_REMINDER_ID, reminder.id)
            .putString(ReminderWorker.KEY_REMINDER_TYPE, reminder.type.toDbString())
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_${reminder.noteId}")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        return workRequest.id.toString()
    }

    override suspend fun cancelReminderWork(workRequestId: String) {
        try {
            val uuid = UUID.fromString(workRequestId)
            WorkManager.getInstance(context).cancelWorkById(uuid)
        } catch (_: IllegalArgumentException) {
            // Invalid UUID, ignore
        }
    }

    private fun ReminderEntity.toDomain() = Reminder(
        id = id, noteId = noteId, triggerAt = triggerAt,
        type = ReminderType.fromString(type),
        isTriggered = isTriggered, workRequestId = workRequestId
    )

    private fun Reminder.toEntity() = ReminderEntity(
        id = id, noteId = noteId, triggerAt = triggerAt,
        type = type.toDbString(),
        isTriggered = isTriggered, workRequestId = workRequestId
    )
}
