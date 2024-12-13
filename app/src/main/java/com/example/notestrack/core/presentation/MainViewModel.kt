package com.example.notestrack.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.core.domain.repository.MainRepository
import com.example.notestrack.core.domain.repository.SessionPref
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject constructor(
        private val mainRepository: MainRepository,
        private val sessionPref: SessionPref
    ): ViewModel(){

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        MainUiState()
    )

    private val accept:(MainUiAction)->Unit

    init {

        accept = ::onUiAction

        viewModelScope.launch {
            delay(1500)
            _uiState.update { it.copy(splashScreenState = true) }
        }

       viewModelScope.launch {
           _uiState.update {
               it.copy(userId = sessionPref.userId)
           }
           if (sessionPref.userId==0L){
               Instant.now().toEpochMilli()?.also { s ->
                   sessionPref.setUserId(s)
                   _uiState.update {
                       it.copy(userId = sessionPref.userId)
                   }
               }
           }
       }
    }

    private fun onUiAction(mainUiAction: MainUiAction) {
        when(mainUiAction){

            else -> {}
        }
    }

}

sealed interface MainUiAction{
}

data class MainUiState(
    val splashScreenState:Boolean = false,
    val userId: Long = 0,
    val userDetailEntity: UserDetailEntity=UserDetailEntity()
)