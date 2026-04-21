package com.voicetasker.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.voicetasker.app.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for reminders.
 */
@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE noteId = :noteId ORDER BY triggerAt ASC")
    fun getRemindersForNote(noteId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE isTriggered = 0 AND triggerAt > :currentTime ORDER BY triggerAt ASC")
    fun getPendingReminders(currentTime: Long): Flow<List<ReminderEntity>>

    @Query("UPDATE reminders SET isTriggered = 1 WHERE id = :reminderId")
    suspend fun markAsTriggered(reminderId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE noteId = :noteId")
    suspend fun deleteRemindersForNote(noteId: Long)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminderById(reminderId: Long)
}
