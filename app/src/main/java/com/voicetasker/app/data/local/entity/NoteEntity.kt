package com.voicetasker.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a voice note.
 * Each note has an audio file, transcription, category, scheduled date, and metadata.
 */
@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["scheduledDate"]),
        Index(value = ["createdAt"])
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val transcription: String,
    val audioFilePath: String,
    val categoryId: Long = 1,
    val scheduledDate: Long,          // epoch millis — date/time of the commitment
    val createdAt: Long,              // epoch millis — when the note was created
    val updatedAt: Long,              // epoch millis — last modification
    val durationMs: Long,             // audio duration in milliseconds
    val isPinned: Boolean = false,
    val isCompleted: Boolean = false
)
