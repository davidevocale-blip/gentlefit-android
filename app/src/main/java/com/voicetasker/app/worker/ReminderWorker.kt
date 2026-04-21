package com.voicetasker.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.voicetasker.app.MainActivity
import com.voicetasker.app.R
import com.voicetasker.app.data.local.dao.NoteDao
import com.voicetasker.app.data.local.dao.ReminderDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WorkManager worker that fires reminder notifications.
 * Triggered at the scheduled time with note details.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val noteDao: NoteDao,
    private val reminderDao: ReminderDao
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_NOTE_ID = "note_id"
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_REMINDER_TYPE = "reminder_type"
        const val CHANNEL_ID = "voicetasker_reminders"
        const val CHANNEL_NAME = "Promemoria VoiceTasker"
    }

    override suspend fun doWork(): Result {
        val noteId = inputData.getLong(KEY_NOTE_ID, -1)
        val reminderId = inputData.getLong(KEY_REMINDER_ID, -1)
        val reminderType = inputData.getString(KEY_REMINDER_TYPE) ?: ""

        if (noteId == -1L) return Result.failure()

        val note = noteDao.getNoteByIdOnce(noteId) ?: return Result.failure()

        // Mark reminder as triggered
        if (reminderId != -1L) {
            reminderDao.markAsTriggered(reminderId)
        }

        // Build the notification
        createNotificationChannel()

        val typeLabel = when (reminderType) {
            "1_DAY" -> "domani"
            "12_HOURS" -> "tra 12 ore"
            "2_HOURS" -> "tra 2 ore"
            else -> "in arrivo"
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_note", noteId)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, noteId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("📌 ${note.title}")
            .setContentText("Impegno $typeLabel")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${note.transcription.take(200)}\n\nImpegno previsto $typeLabel")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.notify(noteId.toInt(), notification)

        return Result.success()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifiche promemoria per le note vocali"
            enableVibration(true)
        }
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
