package com.example.notestrack.addnote.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.addnote.data.local.entity.NotesTableEntity
import com.example.notestrack.addnote.domain.repository.NotesRepository
import com.example.notestrack.notedetails.data.model.NotesData
import com.example.notestrack.richlib.Rich
import com.example.notestrack.richlib.RichEditDataClass
import com.example.notestrack.richlib.RichTypeEnum
import com.example.notestrack.richlib.spanrichlib.BlockKitData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddNotesViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        NotesUiState()
    )

    private val _uiEvent= MutableSharedFlow<NotesUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val accept: (NotesUiAction)->Unit

    init {
        accept = ::onUiAction
    }

    private fun onUiAction(notesUiAction: NotesUiAction) {
        when(notesUiAction){
            is NotesUiAction.SelectRichStyle -> {
                updateListWhichStyleSelect(notesUiAction.richEditDataClass,notesUiAction.selectNow)
            }

            is NotesUiAction.TypeStateOfDescription -> {
                _uiState.update { it.copy(description = notesUiAction.description) }
                checkValid()
            }
            is NotesUiAction.TypeStateOfTitle -> {
                _uiState.update { it.copy(title = notesUiAction.title) }
                checkValid()
            }

            NotesUiAction.SubmitNotes -> {
                if (_uiState.value.editNotesId==0L){
                    submitNote()
                }
                else{
                    editSubmitNote()
                }
            }
            is NotesUiAction.UpdateCurrentNoteMenuId -> {
                _uiState.update { it.copy(currentNoteMenuId = notesUiAction.menuId) }
            }

            is NotesUiAction.EditNotesHomeMenuData -> updateEditStatus(notesUiAction.editData)
        }
    }

    private fun updateEditStatus(editData: NotesData) = viewModelScope.launch(Dispatchers.IO){
        _uiState.update {
            it.copy(
                title = editData.notesName,
                description = editData.notesDesc,
                inputTextUpload = editData.notesBlock,
                currentNoteMenuId = editData.categoryId,
                editNotesId = editData.notesId
            )
        }
    }

    private fun editSubmitNote() =viewModelScope.launch(Dispatchers.IO){
        notesRepository.addNotes(
            NotesTableEntity(
                notesId = _uiState.value.editNotesId,
                notesName = _uiState.value.title,
                notesDesc = _uiState.value.description,
                notesBlock = _uiState.value.inputTextUpload,
                categoryId = _uiState.value.currentNoteMenuId,
                date = Instant.now().toEpochMilli()
            )
        )
        _uiState.update { it.copy(editNotesId = 0L) }
        sendUiEvent(NotesUiEvent.ShowSnackBar("Notes Edited Successfully 🎉"))
        delay(1000)
        sendUiEvent(NotesUiEvent.NavigateToBack)
    }

    private fun submitNote() =viewModelScope.launch(Dispatchers.IO){
        notesRepository.addNotes(
            NotesTableEntity(
                notesName = _uiState.value.title,
                notesDesc = _uiState.value.description,
                notesBlock = _uiState.value.inputTextUpload,
                categoryId = _uiState.value.currentNoteMenuId,
                date = Instant.now().toEpochMilli()
            )
        )
        _uiState.update { it.copy(editNotesId = 0L) }
        sendUiEvent(NotesUiEvent.ShowSnackBar("Notes Added Successfully 🎉"))
        delay(1000)
        sendUiEvent(NotesUiEvent.NavigateToBack)
    }

    private fun checkValid() = viewModelScope.launch(Dispatchers.IO){
        val description = _uiState.value.description
        val title = _uiState.value.title

        _uiState.update {
            it.copy(
                isButtonEnabled = (description.trimIndent().isNotEmpty() && title.trimIndent().isNotEmpty())
            )
        }

    }

    private fun sendUiEvent(notesUiEvent: NotesUiEvent) = viewModelScope.launch(Dispatchers.IO){
        _uiEvent.emit(notesUiEvent)
    }

    private fun updateListWhichStyleSelect(richEditDataClass: RichTypeEnum,selectNow:Boolean) = viewModelScope.launch(Dispatchers.IO){
        _uiState.value.richStyle.map { rich->
            if (rich.richType==richEditDataClass){
                rich.copy(isSelect = if (selectNow) selectNow else !rich.isSelect)
            }
            else{
                rich.copy(isSelect = false)
            }
        }.also { data->
            _uiState.update { it.copy(richStyle = data) }
        }
    }

    fun generateJsonBlockApplied(jsonBlockRich: BlockKitData) = viewModelScope.launch(Dispatchers.IO){
        val jsonObject = JsonObject()
        JsonArray().apply {
            jsonBlockRich.block?.forEach{ data ->
                add((JsonObject().also {
                    it.addProperty("value",data.value)
                    it.addProperty("text",data.text)
                    it.addProperty("style",data.style)
                }))
            }
        }.also { array->
            jsonObject.add("block",array)
        }
        println("generateJsonBlockApplied jsonBlockRich >>> $jsonObject")
        _uiState.update { it.copy(inputTextUpload = jsonObject.toString()) }
    }
}

sealed interface NotesUiAction{
    data class SelectRichStyle(val richEditDataClass: RichTypeEnum,val selectNow:Boolean): NotesUiAction
    data class TypeStateOfTitle(val title:String): NotesUiAction
    data class TypeStateOfDescription(val description:String): NotesUiAction
    data object SubmitNotes: NotesUiAction
    data class UpdateCurrentNoteMenuId(val menuId:Long): NotesUiAction
    data class EditNotesHomeMenuData(val editData: NotesData): NotesUiAction
}

data class NotesUiState(
    val currentNoteMenuId:Long=0,
    val richStyle: List<RichEditDataClass> = Rich.generateRichStyleData(),
    val inputTextUpload: String="",
    val title:String="",
    val description:String="",
    val isButtonEnabled:Boolean = false,
    val editNotesId: Long = 0
)

sealed interface NotesUiEvent{
    data class ShowSnackBar(val message: String): NotesUiEvent
    data object NavigateToBack: NotesUiEvent
}