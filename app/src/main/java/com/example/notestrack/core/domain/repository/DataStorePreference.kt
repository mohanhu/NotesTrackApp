package com.example.notestrack.core.domain.repository

import kotlinx.coroutines.flow.Flow


interface DataStorePreference {

    val userData : Flow<UserData>

    suspend fun setLightTheme(isLightTheme: Boolean)
}

data class UserData(
    val isLightTheme:Boolean
)