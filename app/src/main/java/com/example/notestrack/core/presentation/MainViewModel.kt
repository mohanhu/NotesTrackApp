package com.example.notestrack.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject constructor(): ViewModel(){

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        MainUiState()
    )

    init {
        viewModelScope.launch {
            delay(1500)
            _uiState.update { it.copy(splashScreenState = true) }
        }
    }

}

data class MainUiState(
    val splashScreenState:Boolean = false
)