package com.example.notestrack.addnote.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notestrack.addnote.presentation.adapter.RichStyleAdapter
import com.example.notestrack.addnote.presentation.viewmodel.AddNotesViewModel
import com.example.notestrack.addnote.presentation.viewmodel.NotesUiAction
import com.example.notestrack.addnote.presentation.viewmodel.NotesUiState
import com.example.notestrack.databinding.FragmentAddNotesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AddNotesFragment : Fragment() {

    private val binding by lazy { FragmentAddNotesBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<AddNotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bindState(viewModel.uiState, viewModel.accept)
    }

    private fun FragmentAddNotesBinding.bindState(
        uiState: StateFlow<NotesUiState>,
        accept: (NotesUiAction) -> Unit
    ) {

        bindList(uiState, accept)

    }

    private fun FragmentAddNotesBinding.bindList(
        uiState: StateFlow<NotesUiState>,
        accept: (NotesUiAction) -> Unit
    ) {
        val adapter = RichStyleAdapter(
            richRefreshStyle = {

            }
        )
        rvStyleMark.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        rvStyleMark.adapter = adapter
        uiState.map { it.richStyle }.distinctUntilChanged().onEach {
            adapter.submitList(it)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}