package com.example.notestrack.profile.presentation.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notestrack.R
import com.example.notestrack.databinding.FragmentProfileBinding
import com.example.notestrack.profile.presentation.dialog.EmojiPickerDialog
import com.example.notestrack.profile.presentation.viewmodel.PhoneDarkState
import com.example.notestrack.profile.presentation.viewmodel.ProfileUiAction
import com.example.notestrack.profile.presentation.viewmodel.ProfileUiState
import com.example.notestrack.profile.presentation.viewmodel.ProfileViewModel
import com.example.notestrack.utils.ViewExtentions.hideKeyBoard
import com.example.notestrack.utils.ViewExtentions.showKeyBoard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val binding: FragmentProfileBinding by lazy { FragmentProfileBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        binding.bindState(
            viewModel.uiState,
            viewModel.accept
        )
    }

    private fun FragmentProfileBinding.bindState(
        uiState: StateFlow<ProfileUiState>,
        accept: (ProfileUiAction) -> Unit
    ) {
        bindToolBar(uiState)

        onClickListener(accept)
    }

    private fun FragmentProfileBinding.bindToolBar(uiState: StateFlow<ProfileUiState>) {

        uiState.map { it.userDetailEntity }.distinctUntilChanged().onEach {
            tvUserName.text = it.userName
            ivUserImage.text = it.userImage
            evUser.setText(it.userName)
            ivEditUserImage.text = it.userImage
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.isLightTheme }.onEach {
//            selectIcon.isSelected = it
            tvStatusOfTheme.text =  it
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.isValidInput }.distinctUntilChanged().onEach {
            btnConfirmOuter.isVisible = it && editProfile.isVisible
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun FragmentProfileBinding.onClickListener(accept: (ProfileUiAction) -> Unit) {
        cvEdit.setOnClickListener {
            editProfile.isVisible = !editProfile.isVisible
            btnConfirmOuter.isVisible = editProfile.isVisible
            if (editProfile.isVisible){
                evUser.showKeyBoard()
            }
            else{
                evUser.hideKeyBoard()
            }
        }

        darkThemeContainer.setOnClickListener {

            val popupMenu = PopupMenu(requireContext(),tvStatusOfTheme,Gravity.BOTTOM)
            listOf(
                "Default",
                "Light",
                "Dark"
            ).forEachIndexed { index, i ->
                popupMenu.menu.add(index,index,index,i)
            }
            popupMenu.setOnMenuItemClickListener { menu->
                tvStatusOfTheme.text = when(menu.itemId){
                    0->{
                        viewModel.setTheme(PhoneDarkState.Default.name)
                        menu.title
                    }
                    1->{
                        viewModel.setTheme(PhoneDarkState.Light.name)
                        menu.title
                    }
                    else->{
                        viewModel.setTheme(PhoneDarkState.Dark.name)
                        menu.title
                    }
                }
                true
            }
            popupMenu.show()
//            selectIcon.isSelected = !selectIcon.isSelected
//            viewModel.setTheme(selectIcon.isSelected)
        }
        evUser.doAfterTextChanged {
            accept.invoke(ProfileUiAction.TypingStateOfName(it.toString()))
        }
        btnConfirmOuter.setOnClickListener {
            accept.invoke(ProfileUiAction.UpdateUserDetails)
            lifecycleScope.launch(Dispatchers.Main) {
                evUser.hideKeyBoard()
                editProfile.isVisible = false
            }
        }

        cvEditUserImage.setOnClickListener {
            EmojiPickerDialog(requireContext(),
                pickedEmoji = {
                    accept.invoke(ProfileUiAction.TypingStateOfImage(it))
                    ivEditUserImage.text = it
                }).also {
                it.show()
            }
        }

        saveContainer.setOnClickListener {
            findNavController().navigate(R.id.pinnedFragment)
        }
    }
}