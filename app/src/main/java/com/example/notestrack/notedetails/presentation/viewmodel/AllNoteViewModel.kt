package com.example.notestrack.notedetails.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.addmenu.data.model.mapper.CategoryMapper.toNotesHomeMenuData
import com.example.notestrack.addnote.data.local.entity.NotesTableEntity
import com.example.notestrack.addnote.domain.repository.NotesRepository
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.home.presentation.viewmodel.HomeNoteUiAction
import com.example.notestrack.home.presentation.viewmodel.LoadState
import com.example.notestrack.notedetails.data.model.NotesData
import com.example.notestrack.notedetails.domain.repository.AllNoteRepository
import com.example.notestrack.utils.convertMsToDateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
import java.util.concurrent.CancellationException
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

    private var homejob:Job?= null

    init {
        accept = ::onUiAction

        uiState.map { it.selectedPickedDate }.distinctUntilChanged().onEach {
            if (it==0L){
                fetchNotes()
            }
            else{
                fetchDetailsWhereEqualByDate(it)
            }
        }.launchIn(viewModelScope)

        uiState.map { it.searchQuery }.distinctUntilChanged().onEach { search->
            if (search == "IDLE") return@onEach

            search.ifEmpty {
                if (uiState.value.selectedPickedDate>0){
                    fetchDetailsWhereEqualByDate(uiState.value.selectedPickedDate)
                }
                else{
                    fetchNotes()
                }
             }
            _uiState.update { it.copy(loadState = LoadState.LOAD) }
            delay(200)
            search.also { query->
                _uiState.value.notesData.filter {
                    it.notesDesc.lowercase().contains(query.lowercase())||
                    it.notesName.lowercase().contains(query.lowercase())||
                    convertMsToDateFormat(it.date).lowercase().contains(query.lowercase())
                }.also { state->
                    _uiState.update { it.copy(notesData = state) }
                    if (state.isNotEmpty()) {
                        _uiState.update { it.copy(loadState = LoadState.NOT_LOAD) }
                    }
                    else {
                        _uiState.update { it.copy(loadState = LoadState.EMPTY) }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(allNoteUiAction: AllNoteUiAction) {
        when(allNoteUiAction){
            is AllNoteUiAction.FetchNotesByMenuId -> {
                _uiState.update { it.copy(currentNoteMenuId = allNoteUiAction.menuId) }
                fetchNotes()
                fetchHeader()
            }

            is AllNoteUiAction.DeleteItem -> deleteItem(allNoteUiAction.notesId)
            is AllNoteUiAction.UpdatePinStatus ->  updatePinStatus(allNoteUiAction.notesId,allNoteUiAction.pinnedStatus)
            is AllNoteUiAction.UndoAction -> undoAction(allNoteUiAction.data)
            is AllNoteUiAction.DatePickerFilter -> {
                _uiState.update { it.copy(selectedPickedDate = allNoteUiAction.date) }
            }

            is AllNoteUiAction.OnTypeToSearch -> {
                _uiState.update { it.copy(searchQuery = allNoteUiAction.search) }
            }
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

    private fun fetchHeader() = viewModelScope.launch(Dispatchers.IO){
        val menuId = _uiState.value.currentNoteMenuId
        allNoteRepository.fetchCategoryMenuId(menuId).also { ent->
            _uiState.update {
                it.copy(notesHomeMenuData = ent)
            }
        }
    }

    private fun fetchNotes() = viewModelScope.launch(Dispatchers.IO){
        val menuId = _uiState.value.currentNoteMenuId
        if (homejob?.isActive == true) {
            homejob?.cancel(CancellationException("New One Come"))
        }
        homejob = allNoteRepository.fetchNotesByMenuId(menuId).onEach { ent->
            _uiState.update {
                it.copy(
                    notesData = ent,
                    loadState = if (ent.isEmpty()) LoadState.EMPTY else LoadState.NOT_LOAD
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchDetailsWhereEqualByDate(dateInMs: Long) = viewModelScope.launch(Dispatchers.IO){
        if (homejob?.isActive == true) {
            homejob?.cancel(CancellationException("New One Come"))
        }
        val menuId = _uiState.value.currentNoteMenuId
        homejob = allNoteRepository.fetchNotesWhereEqualToDate(dateInMs = dateInMs, menuId = menuId).onEach { relations ->
            println("mainRepository.getUserRelationWithNotes >>>$relations")
            _uiState.update { state->
                state.copy(
                    notesData = relations,
                    loadState = if (relations.isEmpty()) LoadState.EMPTY else LoadState.NOT_LOAD
                    )
            }
        }.launchIn(viewModelScope)
    }
}

data class AllNoteUiState(
    val notesData: List<NotesData> = listOf(),
    val currentNoteMenuId:Long = 0,
    val notesHomeMenuData: NotesHomeMenuData=NotesHomeMenuData(),
    val selectedPickedDate:Long = 0L,
    val searchQuery:String = "IDLE",
    val loadState: LoadState = LoadState.IDLE
)

sealed interface AllNoteUiAction{
    data class FetchNotesByMenuId(val menuId:Long):AllNoteUiAction
    data class DeleteItem(val notesId: Long) : AllNoteUiAction
    data class UpdatePinStatus(val pinnedStatus: Boolean, val notesId: Long): AllNoteUiAction
    data class UndoAction(val data: NotesData): AllNoteUiAction
    data class DatePickerFilter(val date:Long): AllNoteUiAction
    data class OnTypeToSearch(val search:String): AllNoteUiAction
}