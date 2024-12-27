package com.example.notestrack.addmenu.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.notestrack.addmenu.data.model.local.CategoryTableEntity
import com.example.notestrack.addmenu.domain.repository.AddCategoryRepository
import com.example.notestrack.core.domain.repository.SessionPref
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val addCategoryRepository: AddCategoryRepository,
    private val sessionPref: SessionPref
): ViewModel(){

    private val _uiState = MutableStateFlow(AddCategoryUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        AddCategoryUiState()
    )

    private val _uiTypingTitle = MutableSharedFlow<AddCategoryUiAction.TypingTitle>()

    private val _uiEvent = MutableSharedFlow<AddCategoryUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val accept: (AddCategoryUiAction)->Unit

    val imageListPaging = addCategoryRepository.getSearchCard().cachedIn(viewModelScope)

    private val userId = sessionPref.userId

    init {
        _uiTypingTitle.asSharedFlow().filterIsInstance<AddCategoryUiAction.TypingTitle>().distinctUntilChanged().onEach { type->
            _uiState.update { it.copy(categoryTitle = type.title ) }
            checkValidate()
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

                AddCategoryUiAction.SubmitMenuCategory -> {
                    if (_uiState.value.menuEditId==0L){
                        submitMenuCategory()
                    }
                    else{
                        editSubmitMenuCategory()
                    }
                }

                is AddCategoryUiAction.EditNotesHomeMenuData -> editCategoryData(addCategoryUiAction.edit)
            }
        }
    }

    private fun editCategoryData(edit: NotesHomeMenuData) = viewModelScope.launch(Dispatchers.IO){
        _uiState.update {
            it.copy(
                categoryTitle = edit.menuTitle,
                categoryImage = edit.thumbNail,
                color = edit.pickedColor,
                menuEditId = edit.menuNotesId
            )
        }
    }

    private suspend fun editSubmitMenuCategory() = viewModelScope.launch(Dispatchers.IO){
        addCategoryRepository.insertMenuCategory(
            CategoryTableEntity(
                categoryId = _uiState.value.menuEditId,
                categoryName = _uiState.value.categoryTitle,
                categoryImage = _uiState.value.categoryImage,
                color = _uiState.value.color.ifEmpty { "2CC2EC" },
                userId = userId,
                date = Instant.now().toEpochMilli()
            )
        )
        _uiState.update { it.copy(menuEditId = 0) }
        sendUiEvent(AddCategoryUiEvent.ShowSnackBar("Category Edited Successfully 🎉"))
        delay(500)
        sendUiEvent(AddCategoryUiEvent.BackStack)
    }

    private fun submitMenuCategory() = viewModelScope.launch(Dispatchers.IO){
        addCategoryRepository.insertMenuCategory(
            CategoryTableEntity(
                categoryName = _uiState.value.categoryTitle,
                categoryImage = _uiState.value.categoryImage,
                color = _uiState.value.color.ifEmpty { "2CC2EC" },
                userId = userId,
                date = Instant.now().toEpochMilli()
            )
        )
        _uiState.update { it.copy(menuEditId = 0) }
        sendUiEvent(AddCategoryUiEvent.ShowSnackBar("Category Added Successfully 🎉"))
        delay(500)
        sendUiEvent(AddCategoryUiEvent.BackStack)
    }

    private fun checkValidate() = viewModelScope.launch(Dispatchers.IO){
        val title :String = _uiState.value.categoryTitle
        title.trimIndent().also { state->
            _uiState.update { it.copy(buttonEnable = state.isNotEmpty()) }
        }
    }

    private fun sendUiEvent(addCategoryUiEvent: AddCategoryUiEvent)= viewModelScope.launch(Dispatchers.IO){
        _uiEvent.emit(addCategoryUiEvent)
    }
}

sealed interface AddCategoryUiAction{

    data object SubmitMenuCategory : AddCategoryUiAction

    data class ChooseIcon(val image:String): AddCategoryUiAction

    data class TypingTitle(val title:String): AddCategoryUiAction

    data class ChooseColorCardStroke(val color:String): AddCategoryUiAction

    data class EditNotesHomeMenuData(val edit: NotesHomeMenuData): AddCategoryUiAction
}

sealed interface AddCategoryUiEvent{
    data class ShowSnackBar(val message:String):AddCategoryUiEvent
    data object BackStack: AddCategoryUiEvent
}

data class AddCategoryUiState(
    val categoryImage:String = "https://projectsly.com/images/blog/task-management-strategies.png?v=1686553999071005322",
    val categoryTitle:String = "",
    val color:String = "#2CC2EC",
    val buttonEnable:Boolean = false,
    val menuEditId:Long = 0
)