package com.example.notestrack.notedetails.presentation.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
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
import com.example.notestrack.home.presentation.viewmodel.HomeNoteUiAction
import com.example.notestrack.notedetails.presentation.adapter.NotesDataAdapter
import com.example.notestrack.notedetails.presentation.viewmodel.AllNoteUiAction
import com.example.notestrack.notedetails.presentation.viewmodel.AllNoteUiState
import com.example.notestrack.notedetails.presentation.viewmodel.AllNoteViewModel
import com.example.notestrack.utils.StringFormation.capitalise
import com.example.notestrack.utils.convertMsToDateFormat
import com.example.notestrack.utils.redirectBasedOnPattern
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

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

        calender.setOnClickListener {
            val today = Calendar.getInstance().apply {
                timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
            }

            val currentInMs = uiState.value.selectedPickedDate.takeIf { it>0 }?: Instant.now().toEpochMilli()

            val calendarConstraints = CalendarConstraints.Builder()
            calendarConstraints.setValidator(DateValidatorPointBackward.before(today.timeInMillis))
//            calendarConstraints.setStart(today.timeInMillis)
//            today.add(Calendar.YEAR,1)
//            today.set(Calendar.MONTH,11)
            calendarConstraints.setEnd(today.timeInMillis)

            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pick Date 📆")
                .setSelection(currentInMs)
                .setCalendarConstraints(calendarConstraints.build())
                .build().also { picker->
                    picker.show(childFragmentManager,"MaterialDatePicker")
                }
                .also {
                    it.addOnPositiveButtonClickListener { item->
                        etSearch.setText("")
                        accept.invoke(AllNoteUiAction.DatePickerFilter(item))
                    }
                }
        }

        tvDateOfSelection.setOnClickListener {
            accept.invoke(AllNoteUiAction.DatePickerFilter(0))
        }

        ivBack.setOnClickListener { findNavController().popBackStack() }

        floatAdd.setOnClickListener {
            val menuId = uiState.value.currentNoteMenuId
            val bundle = bundleOf("addNotesFragment" to menuId)
            findNavController().navigate(R.id.addNotesFragment,bundle)
        }

        uiState.map { it.selectedPickedDate }.onEach {
            tvDateOfSelection.isVisible = it!=0L
            tvDateOfSelection.text = convertMsToDateFormat(it)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)


        etSearch.addTextChangedListener(object : TextWatcher {
            lateinit var countDownTimer: CountDownTimer
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                countDownTimer = object : CountDownTimer(100,200){
                    override fun onTick(p0: Long) {
                        accept.invoke(AllNoteUiAction.OnTypeToSearch(s.toString()))
                    }
                    override fun onFinish() {
                    }
                }.start()
            }
        })
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
