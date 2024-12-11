package com.example.notestrack.home.presentation.fragment

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
import com.example.notestrack.databinding.FragmentHomeBinding
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.home.presentation.adapter.NotesHomeAdapter
import com.example.notestrack.home.presentation.viewmodel.HomeNoteUiAction
import com.example.notestrack.home.presentation.viewmodel.HomeNoteUiState
import com.example.notestrack.home.presentation.viewmodel.HomeNotesViewModel
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

        bindList(uiState.map { it.homeCategoryList })

    }

    private fun FragmentHomeBinding.bindList(listFlow: Flow<List<NotesHomeMenuData>>) {
        rvNotesTitle.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        val adapter = NotesHomeAdapter()
        rvNotesTitle.adapter = adapter

        listFlow.distinctUntilChanged().onEach { data->
            adapter.submitList(data)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}