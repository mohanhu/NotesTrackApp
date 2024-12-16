package com.example.notestrack.notedetails.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notestrack.R
import com.example.notestrack.databinding.FragmentAllNotesDetailsBinding
import com.example.notestrack.notedetails.presentation.adapter.NotesDataAdapter
import com.example.notestrack.notedetails.presentation.viewmodel.AllNoteUiAction
import com.example.notestrack.notedetails.presentation.viewmodel.AllNoteUiState
import com.example.notestrack.notedetails.presentation.viewmodel.AllNoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AllNotesDetailsFragment : Fragment() {

    private val binding: FragmentAllNotesDetailsBinding by lazy { FragmentAllNotesDetailsBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<AllNoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        val menuId = arguments?.getLong("addNotesFragment",0L) ?:0L
        println("allNoteRepository.fetchNotesByMenuId(menuId) >>>${menuId}")
        viewModel.accept.invoke(AllNoteUiAction.FetchNotesByMenuId(menuId = menuId))

        binding.bindState(
            viewModel.accept,
            viewModel.uiState
        )
    }

    private fun FragmentAllNotesDetailsBinding.bindState(
        accept: (AllNoteUiAction) -> Unit,
        uiState: StateFlow<AllNoteUiState>
    ) {

        bindList(uiState)

        onClickListener(accept,uiState)

    }

    private fun FragmentAllNotesDetailsBinding.onClickListener(
        accept: (AllNoteUiAction) -> Unit,
        uiState: StateFlow<AllNoteUiState>
    ) {
        floatAdd.setOnClickListener {
            val menuId = uiState.value.currentNoteMenuId
            val bundle = bundleOf("addNotesFragment" to menuId)
            findNavController().navigate(R.id.addNotesFragment,bundle)
        }
    }

    private fun FragmentAllNotesDetailsBinding.bindList(uiState: StateFlow<AllNoteUiState>) {
        rvImageChoosen.layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
        val adapter = NotesDataAdapter(

        )
        rvImageChoosen.adapter = adapter
        uiState.map { it.notesData }.distinctUntilChanged().onEach {
            adapter.submitList(it)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}