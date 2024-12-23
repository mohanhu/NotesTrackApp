package com.example.notestrack.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.notestrack.core.domain.repository.DataStorePreference
import com.example.notestrack.core.domain.repository.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStorePreferenceImpl @Inject constructor(
    private val dataStorePreference: DataStore<Preferences>
) :DataStorePreference {

    override val userData: Flow<UserData> = dataStorePreference.data.map {
        UserData(
            isLightTheme = it[PreferencesKey.LightThemeKey]?:true
        )
    }.catch {

    }

    override suspend fun setLightTheme(isLightTheme: Boolean) {
        dataStorePreference.edit {
            it[PreferencesKey.LightThemeKey] = isLightTheme
        }
    }
}

object PreferencesKey{
     val LightThemeKey = booleanPreferencesKey("LightThemeKey")
}