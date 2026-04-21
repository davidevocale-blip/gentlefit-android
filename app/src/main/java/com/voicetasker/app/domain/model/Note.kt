package com.voicetasker.app.domain.model

/**
 * Domain model for a voice note.
 * Pure Kotlin — no Android dependencies.
 */
data class Note(
    val id: Long = 0,
    val title: String,
    val transcription: String,
    val audioFilePath: String,
    val categoryId: Long,
    val categoryName: String = "",
    val categoryColor: String = "",
    val scheduledDate: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val durationMs: Long,
    val isPinned: Boolean = false,
    val isCompleted: Boolean = false
)
