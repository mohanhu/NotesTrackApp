package com.example.notestrack.splash.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.core.domain.repository.DataStorePreference
import com.example.notestrack.core.domain.repository.MainRepository
import com.example.notestrack.core.domain.repository.SessionPref
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val sessionPref: SessionPref,
    private val dataStorePreference: DataStorePreference,
    private val mainRepository: MainRepository
):ViewModel(){

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        SplashUiState()
    )

    fun updateCurrentScrollIndex(position:Int) = viewModelScope.launch(Dispatchers.IO){
        _uiState.update {
            it.copy(currentScreenIndex = position)
        }
    }

    fun updateSessionUserId() = viewModelScope.launch(Dispatchers.IO){
        Instant.now().toEpochMilli()?.also { s ->
            sessionPref.setUserId(s)
            dataStorePreference.setUserName(s)
            mainRepository.insertUserDetails(
                UserDetailEntity(
                    userId = s,
                    userName = "Buddy",
                    userImage = "🤗"
                )
            )
        }
    }

}

data class SplashUiState(
    val currentScreenIndex : Int = 0
)