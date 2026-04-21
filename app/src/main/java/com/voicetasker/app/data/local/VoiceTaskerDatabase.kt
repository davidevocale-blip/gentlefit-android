package com.voicetasker.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.voicetasker.app.data.local.dao.CategoryDao
import com.voicetasker.app.data.local.dao.NoteDao
import com.voicetasker.app.data.local.dao.ReminderDao
import com.voicetasker.app.data.local.entity.CategoryEntity
import com.voicetasker.app.data.local.entity.NoteEntity
import com.voicetasker.app.data.local.entity.ReminderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database for VoiceTasker.
 * Pre-populates default categories on first creation.
 */
@Database(
    entities = [
        NoteEntity::class,
        CategoryEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VoiceTaskerDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "voicetasker_db"

        /**
         * Callback to pre-populate default categories on database creation.
         */
        fun getCallback(): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val now = System.currentTimeMillis()
                    // Insert default categories
                    db.execSQL(
                        """INSERT INTO categories (name, colorHex, iconName, isDefault, createdAt) 
                           VALUES ('Lavoro', '#6C63FF', 'Work', 1, $now)"""
                    )
                    db.execSQL(
                        """INSERT INTO categories (name, colorHex, iconName, isDefault, createdAt) 
                           VALUES ('Personale', '#FF6584', 'Person', 1, $now)"""
                    )
                    db.execSQL(
                        """INSERT INTO categories (name, colorHex, iconName, isDefault, createdAt) 
                           VALUES ('Salute', '#00D9A6', 'FavoriteOutlined', 1, $now)"""
                    )
                }
            }
        }
    }
}
