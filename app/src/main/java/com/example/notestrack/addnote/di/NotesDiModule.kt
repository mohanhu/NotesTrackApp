package com.example.notestrack.addnote.di

import com.example.notestrack.addnote.data.reposiotory.NotesRepositoryImpl
import com.example.notestrack.addnote.domain.repository.NotesRepository
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
    fun provideNotesModule(notesRepository: NotesRepositoryImpl): NotesRepository

}