package com.example.notestrack.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notestrack.BuildConfig
import com.example.notestrack.core.local.NotesDataBase
import com.example.notestrack.core.service.ApiService
import com.example.notestrack.utils.network.NetworkCollector
import com.example.notestrack.utils.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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
        .baseUrl(BuildConfig.PHOTO_BASE_URL)
        .client(
            OkHttpClient().newBuilder()
                .connectTimeout(AppLevelModuleConstants.CONNECTION_TIMEOUT,TimeUnit.MINUTES)
                .readTimeout(AppLevelModuleConstants.READ_TIMEOUT,TimeUnit.MINUTES)
                .writeTimeout(AppLevelModuleConstants.WRITE_TIMEOUT,TimeUnit.MINUTES)
                .addInterceptor(JwtIntercepter())
                .build()).build()


    @Provides
    @Singleton
    fun provideService(@ApplicationContext context: Context, retrofit: Retrofit) :ApiService = retrofit.create(ApiService::class.java)


    @Provides
    @Singleton
    fun provideDataBase(
        @ApplicationContext context: Context
    ):NotesDataBase = Room.databaseBuilder(context,
        NotesDataBase::class.java,
        "NotesDataBase").fallbackToDestructiveMigration().build()
}


@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule{

    @Binds
    @Singleton
    fun provideNetworkMonitor(networkCollector: NetworkCollector):NetworkMonitor
}


object AppLevelModuleConstants {
    const val CONNECTION_TIMEOUT = 3L
    const val READ_TIMEOUT = 3L
    const val WRITE_TIMEOUT = 3L
    const val SERVER_KEY = "oDbvdSYL2uPTkwp2bHEdtuS8GqublkJREVIxZZhm3RIeLDQTmY6KGE7i"
}