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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllNoteViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val allNoteRepository: AllNoteRepository
) :ViewModel(){

    private val _uiState = MutableStateFlow(AllNoteUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        AllNoteUiState()
    )

    val accept: (AllNoteUiAction)->Unit

    init {
        accept = ::onUiAction
    }

    private fun onUiAction(allNoteUiAction: AllNoteUiAction) {
        when(allNoteUiAction){
            is AllNoteUiAction.FetchNotesByMenuId -> {
                _uiState.update { it.copy(currentNoteMenuId = allNoteUiAction.menuId) }
                fetchNotes()
            }
        }
    }

    private fun fetchNotes() = viewModelScope.launch(Dispatchers.IO){
        val menuId = _uiState.value.currentNoteMenuId
        allNoteRepository.fetchNotesByMenuId(menuId).also { ent->
            _uiState.update {
                it.copy(notesData = ent)
            }
        }
    }

}

data class AllNoteUiState(
    val notesData: List<NotesData> = listOf(),
    val currentNoteMenuId:Long = 0,
)

sealed interface AllNoteUiAction{
    data class FetchNotesByMenuId(val menuId:Long):AllNoteUiAction
}