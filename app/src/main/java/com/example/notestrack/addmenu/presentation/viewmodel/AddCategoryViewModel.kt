package com.example.notestrack.addmenu.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.notestrack.addmenu.domain.repository.AddCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val addCategoryRepository: AddCategoryRepository
): ViewModel(){

    private val _uiState = MutableStateFlow(AddCategoryUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        AddCategoryUiState()
    )

    private val _uiTypingTitle = MutableSharedFlow<AddCategoryUiAction.TypingTitle>()

    val accept: (AddCategoryUiAction)->Unit

    val imageListPaging = addCategoryRepository.getSearchCard().cachedIn(viewModelScope)

    init {
        _uiTypingTitle.asSharedFlow().filterIsInstance<AddCategoryUiAction.TypingTitle>().distinctUntilChanged().onEach { type->
            _uiState.update { it.copy(categoryTitle = type.title ) }
        }.launchIn(viewModelScope)

        accept = ::onUIAction
    }

    private fun onUIAction(addCategoryUiAction: AddCategoryUiAction) {
        viewModelScope.launch {
            when(addCategoryUiAction){
                is AddCategoryUiAction.ChooseIcon -> {
                    _uiState.update { it.copy(categoryImage = addCategoryUiAction.image ) }
                }
                is AddCategoryUiAction.TypingTitle -> {
                    _uiTypingTitle.emit(addCategoryUiAction)
                }

                is AddCategoryUiAction.ChooseColorCardStroke -> {
                    _uiState.update { it.copy(color = addCategoryUiAction.color) }
                }
            }
        }
    }
}

sealed interface AddCategoryUiAction{
    data class ChooseIcon(val image:String): AddCategoryUiAction

    data class TypingTitle(val title:String): AddCategoryUiAction

    data class ChooseColorCardStroke(val color:String): AddCategoryUiAction
}

data class AddCategoryUiState(
    val categoryImage:String = "",
    val categoryTitle:String = "",
    val color:String = "",
)