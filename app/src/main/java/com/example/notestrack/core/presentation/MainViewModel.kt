package com.example.notestrack.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.core.domain.repository.DataStorePreference
import com.example.notestrack.core.domain.repository.MainRepository
import com.example.notestrack.core.domain.repository.SessionPref
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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
        private val sessionPref: SessionPref,
        private val dataStorePreference: DataStorePreference
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

        dataStorePreference.userData.map { it.isLightTheme }.onEach { theme->
            println("MainViewModel >>>>>>>>>.$theme")
            _uiState.update { it.copy(isLightTheme = theme) }
        }.launchIn(viewModelScope)

        dataStorePreference.userData.map { it.userId }.distinctUntilChanged().onEach { id->
            _uiState.update { it.copy(userId = id) }
        }.launchIn(viewModelScope)
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
    val userDetailEntity: UserDetailEntity=UserDetailEntity(),
    val isLightTheme:String = ""
)