package com.example.notestrack.notedetails.presentation.fragment

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
import com.example.notestrack.databinding.FragmentAllNotesDetailsBinding
import com.example.notestrack.notedetails.presentation.adapter.NotesDataAdapter
import com.example.notestrack.notedetails.presentation.viewmodel.AllNoteUiAction
import com.example.notestrack.notedetails.presentation.viewmodel.AllNoteUiState
import com.example.notestrack.notedetails.presentation.viewmodel.AllNoteViewModel
import com.example.notestrack.utils.StringFormation.capitalise
import com.example.notestrack.utils.redirectBasedOnPattern
import com.google.android.material.snackbar.Snackbar
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

        bindList(uiState,accept)

        onClickListener(accept,uiState)

    }

    private fun FragmentAllNotesDetailsBinding.onClickListener(
        accept: (AllNoteUiAction) -> Unit,
        uiState: StateFlow<AllNoteUiState>
    ) {

        ivBack.setOnClickListener { findNavController().popBackStack() }

        floatAdd.setOnClickListener {
            val menuId = uiState.value.currentNoteMenuId
            val bundle = bundleOf("addNotesFragment" to menuId)
            findNavController().navigate(R.id.addNotesFragment,bundle)
        }
    }

    private fun FragmentAllNotesDetailsBinding.bindList(
        uiState: StateFlow<AllNoteUiState>,
        accept: (AllNoteUiAction) -> Unit
    ) {
        rvImageChoosen.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        val adapter = NotesDataAdapter(
            onLongClickListener = { data,view->
                PopupMenu(requireContext(),view, Gravity.END).also { menu ->
                    menu.inflate(R.menu.notes_item_menu)
                    menu.menu.findItem(R.id.pinned).setTitle(
                        if (data.pinnedStatus) "Unpin"
                        else "Pin"
                    )
                    menu.setOnMenuItemClickListener { menu->
                        when(menu.itemId){
                            R.id.delete->{
                                accept.invoke(AllNoteUiAction.DeleteItem(data.notesId))

                                Snackbar.make(root,"Deleted",Snackbar.LENGTH_SHORT).also {
                                    it.setAction("UNDO"){
                                        accept.invoke(AllNoteUiAction.UndoAction(data))
                                    }
                                    it.show()
                                }


                            }
                            R.id.edit->{
                                val bundle = bundleOf("editNotesFragment" to data)
                                findNavController().navigate(R.id.addNotesFragment,bundle)
                            }
                            R.id.pinned->{
                                accept.invoke(AllNoteUiAction.UpdatePinStatus( data.pinnedStatus,data.notesId))
                            }
                        }
                        true
                    }
                    menu.show()
                }
            },
            onClickMessage = { clickString,pattern->
                 findNavController().redirectBasedOnPattern(requireActivity(),clickString,pattern)
            },
            pinClickListener = { data->
                accept.invoke(AllNoteUiAction.UpdatePinStatus( data.pinnedStatus,data.notesId))
            }
        )
        rvImageChoosen.adapter = adapter
        uiState.map { it.notesData }.distinctUntilChanged().onEach {
            adapter.submitList(it)
            rvImageChoosen.isVisible = it.isNotEmpty()
            progress.isVisible = it.isEmpty()
            placeLottie.root.isVisible = it.isEmpty()
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.notesHomeMenuData }.distinctUntilChanged().onEach {
            tvUserName.text = it.menuTitle.capitalise()
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
