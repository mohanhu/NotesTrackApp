package com.example.notestrack.core.data.repository

import android.content.SharedPreferences
import com.example.notestrack.core.domain.repository.SessionPref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SessionPrefImpl(
    private val sharedPreferences: SharedPreferences
) : SessionPref {


    override val userId: Long
        get() = sharedPreferences.getLong(SessionData.SessionKey.USER_ID,0L)?:0L

    override fun setUserId(userId: Long) {
        sharedPreferences.edit().also {
            it.putLong(SessionData.SessionKey.USER_ID,userId)
        }.apply()
    }
}

object SessionData {
    const val Session = "NOTE_SHARE_PREF"
    object SessionKey {
        const val USER_ID = "USER_ID"
    }
}