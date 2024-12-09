package com.example.notestrack.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notestrack.core.local.NotesDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(
        @ApplicationContext context: Context
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(ApplicationConstants.BASE_URL)
        .client(
            OkHttpClient().newBuilder()
                .build()
        ).build()


    @Provides
    @Singleton
    fun provideDataBase(
        @ApplicationContext context: Context
    ):NotesDataBase = Room.databaseBuilder(context,
        NotesDataBase::class.java,
        "NotesDataBase").fallbackToDestructiveMigration().build()
}

object ApplicationConstants {
    const val BASE_URL = ""
}