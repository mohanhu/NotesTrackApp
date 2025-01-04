package com.example.notestrack.task.di

import com.example.notestrack.task.data.repository.DefaultTaskRemoteDataSource
import com.example.notestrack.task.data.repository.DefaultTaskRepository
import com.example.notestrack.task.domain.repository.TaskRemoteDataSource
import com.example.notestrack.task.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface TaskModule {

    @Binds
    @Singleton
    fun provideTaskRepository(defaultTaskRepository: DefaultTaskRepository):TaskRepository


    @Binds
    @Singleton
    fun provideTaskRemoteDataSource(remoteDataSource: DefaultTaskRemoteDataSource):TaskRemoteDataSource

}