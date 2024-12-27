package com.example.notestrack.home.presentation.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notestrack.R
import com.example.notestrack.databinding.FragmentHomeBinding
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.home.presentation.adapter.NotesHomeAdapter
import com.example.notestrack.home.presentation.viewmodel.HomeNoteUiAction
import com.example.notestrack.home.presentation.viewmodel.HomeNoteUiState
import com.example.notestrack.home.presentation.viewmodel.HomeNotesViewModel
import com.example.notestrack.profile.data.local.entity.UserDetailEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<HomeNotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bindState(
            viewModel.uiState,
            viewModel.accept
        )
    }

    private fun FragmentHomeBinding.bindState(
        uiState: StateFlow<HomeNoteUiState>,
        accept: (HomeNoteUiAction) -> Unit) {

        bindList(uiState.map { it.homeCategoryList },accept)

        bindUiDetails(uiState.map { it.userDetailEntity })

    }

    private fun FragmentHomeBinding.bindUiDetails(user: Flow<UserDetailEntity>) {

        user.onEach {
            tvUserName.text = getString(R.string.hey_s,it.userName)
            ivUserImage.text = it.userImage
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun FragmentHomeBinding.bindList(
        listFlow: Flow<List<NotesHomeMenuData>>,
        accept: (HomeNoteUiAction) -> Unit
    ) {
        rvNotesTitle.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        val adapter = NotesHomeAdapter(
            pick = {
                val bundle = bundleOf("addNotesFragment" to it.menuNotesId)
                findNavController().navigate(R.id.allNotesDetailsFragment,bundle)
            },
            longClick = { data,view->
                PopupMenu(requireContext(),view,Gravity.END).also {
                    it.inflate(R.menu.home_menu_item)
                    it.setOnMenuItemClickListener { menu->
                        when(menu.itemId){
                            R.id.delete->{
                                accept.invoke(HomeNoteUiAction.DeleteItem(data))
                            }
                            R.id.edit->{
                                val bundle = bundleOf("addMenuFragment" to data)
                                findNavController().navigate(R.id.addMenuFragment,bundle)
                            }
                        }
                        true
                    }
                    it.show()
                }
            }
        )
        rvNotesTitle.adapter = adapter

        listFlow.distinctUntilChanged().onEach { data->
            adapter.submitList(data)
            rvNotesTitle.isVisible = data.isNotEmpty()
            placeLottie.root.isVisible = data.isEmpty()
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}