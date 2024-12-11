package com.example.notestrack.addmenu.di

import com.example.notestrack.addmenu.data.repository.AddCategoryDataSourceImpl
import com.example.notestrack.addmenu.data.repository.AddCategoryRepositoryImpl
import com.example.notestrack.addmenu.domain.repository.AddCategoryDataSource
import com.example.notestrack.addmenu.domain.repository.AddCategoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AddMenuModule {

    @Binds
    @Singleton
    fun provideAddCategoryDataSource(addCategoryDataSourceImpl: AddCategoryDataSourceImpl):AddCategoryDataSource

    @Binds
    @Singleton
    fun provideAddCategoryRepository(addCategoryRepositoryImpl: AddCategoryRepositoryImpl):AddCategoryRepository

}