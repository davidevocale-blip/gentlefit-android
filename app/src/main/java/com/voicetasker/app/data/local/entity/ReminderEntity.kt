package com.voicetasker.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a reminder for a note.
 * Multiple reminders can exist for a single note (e.g. 1 day, 12h, 2h before).
 */
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["noteId"]),
        Index(value = ["triggerAt"])
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,
    val triggerAt: Long,              // epoch millis — when to fire the reminder
    val type: String,                 // "1_DAY", "12_HOURS", "2_HOURS"
    val isTriggered: Boolean = false, // whether notification has already fired
    val workRequestId: String = ""    // UUID of the WorkManager request
)
