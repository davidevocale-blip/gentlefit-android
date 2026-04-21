package com.voicetasker.app.data.repository

import com.voicetasker.app.data.local.dao.NoteDao
import com.voicetasker.app.data.local.entity.NoteEntity
import com.voicetasker.app.domain.model.Note
import com.voicetasker.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNoteById(noteId: Long): Flow<Note?> {
        return noteDao.getNoteById(noteId).map { it?.toDomain() }
    }

    override suspend fun getNoteByIdOnce(noteId: Long): Note? {
        return noteDao.getNoteByIdOnce(noteId)?.toDomain()
    }

    override fun getNotesForDate(dateMillis: Long): Flow<List<Note>> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        return noteDao.getNotesForDate(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotesInRange(startMillis: Long, endMillis: Long): Flow<List<Note>> {
        return noteDao.getNotesInRange(startMillis, endMillis).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotesByCategory(categoryId: Long): Flow<List<Note>> {
        return noteDao.getNotesByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDaysWithNotes(startMillis: Long, endMillis: Long): Flow<List<Long>> {
        return noteDao.getDaysWithNotes(startMillis, endMillis)
    }

    override suspend fun countNotesSince(sinceMillis: Long): Int {
        return noteDao.countNotesSince(sinceMillis)
    }

    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    override suspend fun deleteNote(noteId: Long) {
        noteDao.deleteNoteById(noteId)
    }

    // --- Mappers ---

    private fun NoteEntity.toDomain() = Note(
        id = id,
        title = title,
        transcription = transcription,
        audioFilePath = audioFilePath,
        categoryId = categoryId,
        scheduledDate = scheduledDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
        durationMs = durationMs,
        isPinned = isPinned,
        isCompleted = isCompleted
    )

    private fun Note.toEntity() = NoteEntity(
        id = id,
        title = title,
        transcription = transcription,
        audioFilePath = audioFilePath,
        categoryId = categoryId,
        scheduledDate = scheduledDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
        durationMs = durationMs,
        isPinned = isPinned,
        isCompleted = isCompleted
    )
}
