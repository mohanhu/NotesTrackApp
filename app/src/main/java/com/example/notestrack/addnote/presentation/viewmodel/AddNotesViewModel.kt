package com.example.notestrack.addnote.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.richlib.Rich
import com.example.notestrack.richlib.RichEditDataClass
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
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

            }
        }
    }
}

sealed interface NotesUiAction{
    data class SelectRichStyle(val richEditDataClass: RichEditDataClass): NotesUiAction
}

data class NotesUiState(
    val richStyle: List<RichEditDataClass> = Rich.generateRichStyleData()
)