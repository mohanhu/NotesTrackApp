package com.example.notestrack.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.notestrack.core.domain.repository.DataStorePreference
import com.example.notestrack.core.domain.repository.UserData
import com.example.notestrack.profile.presentation.viewmodel.PhoneDarkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStorePreferenceImpl @Inject constructor(
    private val dataStorePreference: DataStore<Preferences>
) :DataStorePreference {

    override val userData: Flow<UserData> = dataStorePreference.data.map {
        UserData(
            isLightTheme = it[PreferencesKey.LightThemeKey]?: PhoneDarkState.Default.name,
            userId = it[PreferencesKey.UserId]?:0L,
            cardBackGround = it[PreferencesKey.CardBackGround]?:""
        )
    }.catch {

    }

    override suspend fun setCardName(cardBackGround: String) {
        dataStorePreference.edit {
            it[PreferencesKey.CardBackGround] = cardBackGround
        }
    }

    override suspend fun setUserName(userId: Long) {
        dataStorePreference.edit {
            it[PreferencesKey.UserId] = userId
        }
    }

    override suspend fun setLightTheme(isLightTheme: String) {
        dataStorePreference.edit {
            it[PreferencesKey.LightThemeKey] = isLightTheme
        }
    }
}

object PreferencesKey{
     val LightThemeKey = stringPreferencesKey("LightThemeKey")
     val UserId = longPreferencesKey("UserId")
     val CardBackGround = stringPreferencesKey("CardBackGround")
}