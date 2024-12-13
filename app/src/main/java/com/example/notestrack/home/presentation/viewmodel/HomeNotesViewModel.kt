package com.example.notestrack.home.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.core.domain.repository.MainRepository
import com.example.notestrack.core.domain.repository.SessionPref
import com.example.notestrack.core.presentation.MainUiAction
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    init {
        accept = ::onUiAction

        _uiState.update { it.copy(userId = sessionPref.userId) }

        accept.invoke(HomeNoteUiAction.FetchUserDetails)
    }


    private fun onUiAction(homeNoteUiAction: HomeNoteUiAction) {
        when(homeNoteUiAction){
            HomeNoteUiAction.FetchUserDetails -> {
                fetchUserDetails()
            }
        }
    }

    private fun fetchUserDetails() = viewModelScope.launch(Dispatchers.IO) {

        mainRepository.getUserRelationWithNotes(sessionPref.userId).onEach {
            println("mainRepository.getUserRelationWithNotes >>>$it")
        }.launchIn(viewModelScope)

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
}

data class HomeNoteUiState(
    val homeCategoryList : List<NotesHomeMenuData> = listOf(
        NotesHomeMenuData(),
        NotesHomeMenuData(),
        NotesHomeMenuData(),
        NotesHomeMenuData()
    ),
    val userId: Long = 0,
    val userDetailEntity: UserDetailEntity=UserDetailEntity()
)

sealed interface HomeNoteUiAction {
    data object FetchUserDetails : HomeNoteUiAction
}