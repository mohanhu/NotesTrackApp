package com.example.notestrack.notedetails.di

import com.example.notestrack.notedetails.data.repository.AllNoteRepositoryImpl
import com.example.notestrack.notedetails.domain.repository.AllNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NotesDiModule {

    @Binds
    @Singleton
    fun provideNoteRepository(noteRepositoryImpl: AllNoteRepositoryImpl):AllNoteRepository

}