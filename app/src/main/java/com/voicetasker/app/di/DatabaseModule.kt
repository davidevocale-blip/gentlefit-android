package com.voicetasker.app.di

import android.content.Context
import androidx.room.Room
import com.voicetasker.app.data.local.VoiceTaskerDatabase
import com.voicetasker.app.data.local.dao.CategoryDao
import com.voicetasker.app.data.local.dao.NoteDao
import com.voicetasker.app.data.local.dao.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): VoiceTaskerDatabase {
        return Room.databaseBuilder(
            context,
            VoiceTaskerDatabase::class.java,
            VoiceTaskerDatabase.DATABASE_NAME
        )
            .addCallback(VoiceTaskerDatabase.getCallback())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideNoteDao(database: VoiceTaskerDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideCategoryDao(database: VoiceTaskerDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideReminderDao(database: VoiceTaskerDatabase): ReminderDao = database.reminderDao()
}
