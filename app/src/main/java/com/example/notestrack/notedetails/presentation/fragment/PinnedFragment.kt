package com.example.notestrack.notedetails.presentation.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notestrack.R
import com.example.notestrack.databinding.FragmentPinnedBinding
import com.example.notestrack.notedetails.presentation.adapter.NotesDataAdapter
import com.example.notestrack.notedetails.presentation.viewmodel.PinNoteUiAction
import com.example.notestrack.notedetails.presentation.viewmodel.PinNoteUiState
import com.example.notestrack.notedetails.presentation.viewmodel.PinnedStatusViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PinnedFragment : Fragment() {

    private val binding: FragmentPinnedBinding by lazy { FragmentPinnedBinding.inflate(layoutInflater) }

    private val viewModel: PinnedStatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        binding.bindState(viewModel.uiState,viewModel.accept)
    }

    private fun FragmentPinnedBinding.bindState(uiState: StateFlow<PinNoteUiState>, accept: (PinNoteUiAction) -> Unit) {

        bindList(uiState,accept)

        onCLickListener(accept)
    }

    private fun FragmentPinnedBinding.onCLickListener(accept: (PinNoteUiAction) -> Unit) {
        ivBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun FragmentPinnedBinding.bindList(uiState: StateFlow<PinNoteUiState>, accept: (PinNoteUiAction) -> Unit) {
        val adapter = NotesDataAdapter(
            onLongClickListener = {data,v ->
                PopupMenu(requireContext(),v,Gravity.END).also {
                    it.menu.add(0,R.id.pinned,0,if (data.pinnedStatus) "Unpin" else "Pin")

                    it.setOnMenuItemClickListener { menu->
                        when(menu.itemId){
                            R.id.pinned->{
                                accept.invoke(PinNoteUiAction.UpdatePinStatus(data.notesId))
                            }
                        }
                        true
                    }

                    it.show()
                }
            },
            onClickMessage = {_,_->},
            pinClickListener = {
                accept.invoke(PinNoteUiAction.UpdatePinStatus(it.notesId))
            }
        )
        rvImageChoosen.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        rvImageChoosen.adapter = adapter

        uiState.map { it.notesData }.distinctUntilChanged().onEach {
            adapter.submitList(it)
            rvImageChoosen.isVisible = it.isNotEmpty()
            placeLottie.root.isVisible = it.isEmpty()
            progress.isVisible = it.isEmpty()
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}