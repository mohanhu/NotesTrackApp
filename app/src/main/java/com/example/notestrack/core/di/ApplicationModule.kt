package com.example.notestrack.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notestrack.BuildConfig
import com.example.notestrack.core.data.repository.DataStorePreferenceImpl
import com.example.notestrack.core.data.repository.SessionData
import com.example.notestrack.core.data.repository.SessionPrefImpl
import com.example.notestrack.core.domain.repository.DataStorePreference
import com.example.notestrack.core.domain.repository.SessionPref
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

    private val Context.dataStore by preferencesDataStore("")

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


    @Singleton
    @Provides
    fun provideSession(@ApplicationContext context: Context): SessionPref = SessionPrefImpl(context.getSharedPreferences(SessionData.Session,Context.MODE_PRIVATE))

    @Provides
    @Singleton
    fun providersUserDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

}


@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule{

    @Binds
    @Singleton
    fun provideNetworkMonitor(networkCollector: NetworkCollector):NetworkMonitor

    @Binds
    @Singleton
    fun provideDataStore(dataStorePreferenceImpl: DataStorePreferenceImpl):DataStorePreference

}


object AppLevelModuleConstants {
    const val CONNECTION_TIMEOUT = 3L
    const val READ_TIMEOUT = 3L
    const val WRITE_TIMEOUT = 3L
    const val SERVER_KEY = "oDbvdSYL2uPTkwp2bHEdtuS8GqublkJREVIxZZhm3RIeLDQTmY6KGE7i"
}