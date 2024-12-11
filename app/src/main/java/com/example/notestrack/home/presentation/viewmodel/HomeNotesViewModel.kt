package com.example.notestrack.home.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeNotesViewModel
@Inject constructor(
    @ApplicationContext context: Context
): ViewModel(){

    private val _uiState = MutableStateFlow(HomeNoteUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        HomeNoteUiState()
    )

    val accept: (HomeNoteUiAction)->Unit

    init {
        accept = ::onUiAction
    }


    private fun onUiAction(homeNoteUiAction: HomeNoteUiAction) {
        when(homeNoteUiAction){

            else -> {}
        }
    }
}

data class HomeNoteUiState(
    val homeCategoryList : List<NotesHomeMenuData> = listOf(
        NotesHomeMenuData(),
        NotesHomeMenuData(),
        NotesHomeMenuData(),
        NotesHomeMenuData()
    )
)

sealed interface HomeNoteUiAction {

}