package com.example.notestrack.addnote.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.richlib.Rich
import com.example.notestrack.richlib.RichEditDataClass
import com.example.notestrack.richlib.RichTypeEnum
import com.example.notestrack.richlib.spanrichlib.BlockKitData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
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
class AddNotesViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        NotesUiState()
    )


    val accept: (NotesUiAction)->Unit

    init {
        accept = ::onUiAction
    }

    private fun onUiAction(notesUiAction: NotesUiAction) {
        when(notesUiAction){
            is NotesUiAction.SelectRichStyle -> {
                updateListWhichStyleSelect(notesUiAction.richEditDataClass,notesUiAction.selectNow)
            }
        }
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
}

data class NotesUiState(
    val richStyle: List<RichEditDataClass> = Rich.generateRichStyleData(),
    val inputTextUpload: String=""
)