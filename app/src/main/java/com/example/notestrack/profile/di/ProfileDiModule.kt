package com.example.notestrack.profile.di

import com.example.notestrack.profile.data.repository.ProfileRepositoryImpl
import com.example.notestrack.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ProfileDiModule {

    @Singleton
    @Binds
    fun provideProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl):ProfileRepository

}