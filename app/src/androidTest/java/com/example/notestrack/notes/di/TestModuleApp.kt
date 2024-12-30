package com.example.notestrack.notes.di

import android.content.Context
import androidx.room.Room
import com.example.notestrack.core.local.NotesDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestModuleApp {

    @Provides
    @Singleton
    @Named("db_testing")
     fun provideInMemoryDataBase(
        @ApplicationContext context: Context
    ): NotesDataBase = Room.inMemoryDatabaseBuilder(context,
        NotesDataBase::class.java).allowMainThreadQueries().build()

    @Provides
    @Singleton
    @Named("Notes_Dao")
    fun provideNotesDao(
        @Named("db_testing") notesDataBase: NotesDataBase
    ) = notesDataBase.notesDao

}