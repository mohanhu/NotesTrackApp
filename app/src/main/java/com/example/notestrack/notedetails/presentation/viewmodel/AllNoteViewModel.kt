package com.example.notestrack.notedetails.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.addnote.data.local.entity.NotesTableEntity
import com.example.notestrack.addnote.domain.repository.NotesRepository
import com.example.notestrack.home.domain.model.NotesHomeMenuData
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
class AllNoteViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val allNoteRepository: AllNoteRepository,
    private val notesRepository: NotesRepository
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

            is AllNoteUiAction.DeleteItem -> deleteItem(allNoteUiAction.notesId)
            is AllNoteUiAction.UpdatePinStatus ->  updatePinStatus(allNoteUiAction.notesId,allNoteUiAction.pinnedStatus)
            is AllNoteUiAction.UndoAction -> undoAction(allNoteUiAction.data)
        }
    }

    private fun undoAction(allNoteUiAction: NotesData) =viewModelScope.launch(Dispatchers.IO){
        notesRepository.addNotes(
            NotesTableEntity(
                notesId = allNoteUiAction.notesId,
                notesName = allNoteUiAction.notesName,
                notesDesc = allNoteUiAction.notesDesc,
                notesBlock = allNoteUiAction.notesBlock,
                date = allNoteUiAction.date,
                categoryId = allNoteUiAction.categoryId,
                pinnedStatus = allNoteUiAction.pinnedStatus
            )
        )
    }

    private fun updatePinStatus(notesId: Long, pinnedStatus: Boolean) = viewModelScope.launch(Dispatchers.IO){
        allNoteRepository.updatePinStatus(!pinnedStatus,notesId)
    }

    private fun deleteItem(notesId: Long) = viewModelScope.launch(Dispatchers.IO){
        allNoteRepository.deleteNotesId(notesId)
    }

    private fun fetchNotes() = viewModelScope.launch(Dispatchers.IO){
        val menuId = _uiState.value.currentNoteMenuId
        allNoteRepository.fetchNotesByMenuId(menuId).onEach { ent->
            _uiState.update {
                it.copy(notesData = ent)
            }
        }.launchIn(viewModelScope)

        allNoteRepository.fetchCategoryMenuId(menuId).also { ent->
            _uiState.update {
                it.copy(notesHomeMenuData = ent)
            }
        }
    }

}

data class AllNoteUiState(
    val notesData: List<NotesData> = listOf(),
    val currentNoteMenuId:Long = 0,
    val notesHomeMenuData: NotesHomeMenuData=NotesHomeMenuData()
)

sealed interface AllNoteUiAction{
    data class FetchNotesByMenuId(val menuId:Long):AllNoteUiAction
    data class DeleteItem(val notesId: Long) : AllNoteUiAction
    data class UpdatePinStatus(val pinnedStatus: Boolean, val notesId: Long): AllNoteUiAction
    data class UndoAction(val data: NotesData): AllNoteUiAction
}