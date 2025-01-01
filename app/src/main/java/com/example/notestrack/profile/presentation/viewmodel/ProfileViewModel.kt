package com.example.notestrack.profile.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notestrack.core.domain.repository.DataStorePreference
import com.example.notestrack.core.domain.repository.SessionPref
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import com.example.notestrack.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val profileRepository: ProfileRepository,
    private val sessionPref: SessionPref,
    private val dataStorePreference: DataStorePreference
) :ViewModel(){

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    private val _typeUiState = MutableSharedFlow<ProfileUiAction>()

    val userId = sessionPref.userId

    val accept: (ProfileUiAction)->Unit


    init {

        accept =::onUiAction

        fetchUserDetails()

        dataStorePreference.userData.map { it.isLightTheme }.onEach { theme->
            println("sessionPref.setLightTheme >>$theme")
            _uiState.update { it.copy(isLightTheme = theme) }
        }.launchIn(viewModelScope)

        _typeUiState.asSharedFlow().filterIsInstance<ProfileUiAction.TypingStateOfName>().distinctUntilChanged().onEach { name->
            _uiState.update { it.copy(editUserName = name.userName) }
            checkValidName(name.userName)
        }.launchIn(viewModelScope)
        _typeUiState.asSharedFlow().filterIsInstance<ProfileUiAction.TypingStateOfImage>().distinctUntilChanged().onEach { image->
            _uiState.update { it.copy(editUserImage = image.userImage) }
        }.launchIn(viewModelScope)
    }

    private fun checkValidName(userName: String) = viewModelScope.launch(Dispatchers.IO){
        _uiState.update {
            it.copy(isValidInput = userName.trimIndent().isNotEmpty())
        }
    }

    private fun onUiAction(profileUiAction: ProfileUiAction) {
        when(profileUiAction){
            is ProfileUiAction.UpdateUserDetails -> {
                updateUserDetails()
            }

            is ProfileUiAction.TypingStateOfImage,
            is ProfileUiAction.TypingStateOfName -> {
                viewModelScope.launch(Dispatchers.IO){_typeUiState.emit(profileUiAction)}
            }
        }
    }

    private fun updateUserDetails() = viewModelScope.launch(Dispatchers.IO){
        val userName = _uiState.value.editUserName
        val userImage = _uiState.value.editUserImage.takeIf { it.isNotEmpty() }?:"😃"
        profileRepository.updateUserDetails(userId,userName,userImage)
        _uiState.update { it.copy(isValidInput = false) }
    }

    private fun fetchUserDetails() = viewModelScope.launch(Dispatchers.IO){
        profileRepository.getUserDetails(userId).onEach { ent->
            _uiState.update { it.copy(userDetailEntity = ent) }
        }.launchIn(viewModelScope)
    }

    fun setTheme(isLight: String) = viewModelScope.launch {
        dataStorePreference.setLightTheme(isLight)
        _uiState.update { it.copy(isLightTheme = isLight) }
    }
}

sealed interface ProfileUiAction{
    data object UpdateUserDetails:ProfileUiAction
    data class TypingStateOfName(val userName:String): ProfileUiAction
    data class TypingStateOfImage(val userImage:String): ProfileUiAction
}

data class ProfileUiState(
    val userDetailEntity: UserDetailEntity = UserDetailEntity(),
    val editUserName:String = "",
    val editUserImage:String = "",
    val isValidInput:Boolean = false,
    val isLightTheme:String = PhoneDarkState.Default.name
)

enum class PhoneDarkState{
    Default,
    Light,
    Dark
}