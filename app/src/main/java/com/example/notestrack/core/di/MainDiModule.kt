package com.example.notestrack.core.di

import com.example.notestrack.core.data.repository.MainRepositoryImpl
import com.example.notestrack.core.domain.repository.MainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MainDiModule {

    @Binds
    @Singleton
    fun provideMainRepository(mainRepositoryImpl: MainRepositoryImpl): MainRepository

}