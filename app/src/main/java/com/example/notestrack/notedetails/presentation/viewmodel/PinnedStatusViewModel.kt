package com.example.notestrack.notedetails.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.notedetails.data.model.NotesData
import com.example.notestrack.notedetails.domain.repository.AllNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinnedStatusViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val allNoteRepository: AllNoteRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(PinNoteUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        PinNoteUiState()
    )

    val accept: (PinNoteUiAction)->Unit

    init {
        accept = ::onUiAction

        fetchNotes()
    }

    private fun onUiAction(pinNoteUiAction: PinNoteUiAction) {
        when(pinNoteUiAction){
            is PinNoteUiAction.UpdatePinStatus -> pinStatusUpdate(pinNoteUiAction.notesId)
        }
    }

    private fun pinStatusUpdate(notesId: Long) = viewModelScope.launch(Dispatchers.IO){
        allNoteRepository.updatePinStatus(false,notesId)
    }

    private fun fetchNotes() = viewModelScope.launch(Dispatchers.IO){
        allNoteRepository.fetchNotesAll().onEach { ent->
            _uiState.update { state ->
                state.copy(notesData = ent.filter { it.pinnedStatus })
            }
        }.launchIn(viewModelScope)
    }
}


data class PinNoteUiState(
    val notesData: List<NotesData> = listOf(),
)

sealed interface PinNoteUiAction{
    data class UpdatePinStatus(val notesId: Long): PinNoteUiAction
}