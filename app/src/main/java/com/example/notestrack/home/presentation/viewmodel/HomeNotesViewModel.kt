package com.example.notestrack.home.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.addmenu.data.model.mapper.CategoryMapper.toNotesHomeMenuData
import com.example.notestrack.core.domain.repository.MainRepository
import com.example.notestrack.core.domain.repository.SessionPref
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import com.example.notestrack.utils.convertMsToDateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
class HomeNotesViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    private val mainRepository: MainRepository,
    private val sessionPref: SessionPref
): ViewModel(){

    private val _uiState = MutableStateFlow(HomeNoteUiState())
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        HomeNoteUiState()
    )

    val accept: (HomeNoteUiAction)->Unit

    private var homejob:Job?=null

    init {
        accept = ::onUiAction

        _uiState.update { it.copy(userId = sessionPref.userId) }

        uiState.map { it.selectedPickedDate }.distinctUntilChanged().onEach {
            if (it==0L){
                accept.invoke(HomeNoteUiAction.FetchUserDetails)
            }
            else{
                fetchDetailsWhereEqualByDate(it)
            }
        }.launchIn(viewModelScope)

        uiState.map { it.searchQuery }.distinctUntilChanged().onEach { search->
            search.ifEmpty { fetchDetailsWhereEqualByDate(
                uiState.value.selectedPickedDate.takeIf { it>0L }?:Instant.now().toEpochMilli()
            ) }
            search.also { query->
                _uiState.value.homeCategoryList.filter {
                    it.menuTitle.lowercase().contains(query.lowercase())||
                            convertMsToDateFormat(it.createdAt).lowercase().contains(query.lowercase())
                }.also { state->
                    _uiState.update { it.copy(homeCategoryList = state) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchDetailsWhereEqualByDate(dateInMs: Long) =viewModelScope.launch(Dispatchers.IO){
        if (homejob?.isActive == true) {
            homejob?.cancel(CancellationException("New One Come"))
        }
        homejob = mainRepository.getUserRelationWithNotesWhereEqualToDate(sessionPref.userId,dateInMs).onEach { relations ->
            println("mainRepository.getUserRelationWithNotes >>>$relations")
            if (relations.isNotEmpty()){
                _uiState.update { state->
                    state.copy(homeCategoryList = relations.first().categoryTableEntity.toNotesHomeMenuData())
                }
            }
            else{
                _uiState.update { state->
                    state.copy(homeCategoryList = listOf())
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onUiAction(homeNoteUiAction: HomeNoteUiAction) {
        when(homeNoteUiAction){
            HomeNoteUiAction.FetchUserDetails -> {
                fetchHomeList()
                fetchUserHeader()
            }

            is HomeNoteUiAction.DeleteItem -> deleteItem(homeNoteUiAction.data)
            is HomeNoteUiAction.DatePickerFilter -> {
                _uiState.update { it.copy(selectedPickedDate = homeNoteUiAction.date) }
            }

            is HomeNoteUiAction.OnTypeToSearch -> {
                _uiState.update { it.copy(searchQuery = homeNoteUiAction.search) }
            }
        }
    }

    private fun fetchUserHeader() = viewModelScope.launch(Dispatchers.IO){
        mainRepository.selectUserDetails().onEach { entities ->
            if (entities.isEmpty()){
                mainRepository.insertUserDetails(
                    UserDetailEntity(
                        userId = _uiState.value.userId,
                        userName = "Buddy",
                        userImage = "🤗"
                    )
                )
            }
            else{
                _uiState.update {
                    it.copy(userDetailEntity = entities[0])
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteItem(data: NotesHomeMenuData) = viewModelScope.launch(Dispatchers.IO){
        mainRepository.deleteCategory(data.menuNotesId)
    }

    private fun fetchHomeList() = viewModelScope.launch(Dispatchers.IO) {

        if (homejob?.isActive == true) {
            homejob?.cancel(CancellationException("New One Come"))
        }

        homejob = mainRepository.getUserRelationWithNotes(sessionPref.userId).onEach { relations ->
            println("mainRepository.getUserRelationWithNotes >>>$relations")
            if (relations.isNotEmpty()){
                _uiState.update { state->
                    state.copy(homeCategoryList = relations.first().categoryTableEntity.toNotesHomeMenuData())
                }
            }
            else{
                _uiState.update { state->
                    state.copy(homeCategoryList = listOf())
                }
            }
        }.launchIn(viewModelScope)
    }
}

data class HomeNoteUiState(
    val homeCategoryList : List<NotesHomeMenuData> = listOf(),
    val userId: Long = 0,
    val userDetailEntity: UserDetailEntity=UserDetailEntity(),
    val selectedPickedDate:Long = 0L,
    val searchQuery:String = ""
)

sealed interface HomeNoteUiAction {
    data object FetchUserDetails : HomeNoteUiAction
    data class DeleteItem(val data: NotesHomeMenuData): HomeNoteUiAction
    data class DatePickerFilter(val date:Long): HomeNoteUiAction
    data class OnTypeToSearch(val search:String): HomeNoteUiAction
}