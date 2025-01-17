package com.example.notestrack.core.domain.repository

import kotlinx.coroutines.flow.Flow


interface DataStorePreference {

    val userData : Flow<UserData>

    suspend fun setUserName(userId:Long)

    suspend fun setCardName(cardBackGround: String)

    suspend fun setLightTheme(isLightTheme: String)
}

data class UserData(
    val userId:Long,
    val isLightTheme:String,
    val cardBackGround:String
)