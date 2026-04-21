package com.voicetasker.app.domain.repository

import com.voicetasker.app.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for note operations.
 * Implemented in the data layer.
 */
interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(noteId: Long): Flow<Note?>
    suspend fun getNoteByIdOnce(noteId: Long): Note?
    fun getNotesForDate(dateMillis: Long): Flow<List<Note>>
    fun getNotesInRange(startMillis: Long, endMillis: Long): Flow<List<Note>>
    fun getNotesByCategory(categoryId: Long): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    fun getDaysWithNotes(startMillis: Long, endMillis: Long): Flow<List<Long>>
    suspend fun countNotesSince(sinceMillis: Long): Int
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(noteId: Long)
}
