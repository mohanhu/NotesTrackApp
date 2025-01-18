package com.example.notestrack.core.data.repository

import com.example.notestrack.core.domain.repository.DataStorePreference
import com.example.notestrack.core.domain.repository.SessionPref
import com.example.notestrack.core.domain.repository.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeSessionPrefImplTest:SessionPref {

    override val userId: Long = 0

    override fun setUserId(userId: Long) {

    }
}

class FakeDataStorePreferenceTest : DataStorePreference{
    override val userData: Flow<UserData>
        get() = flow {  }

    override suspend fun setUserName(userId: Long) {
        Unit
    }

    override suspend fun setCardName(cardBackGround: String) {
        Unit
    }

    override suspend fun setLightTheme(isLightTheme: String) {
        Unit
    }

}