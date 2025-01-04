package com.example.notestrack.home.presentation.fragment

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
import com.example.notestrack.databinding.FragmentHomeBinding
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.home.presentation.adapter.NotesHomeAdapter
import com.example.notestrack.home.presentation.viewmodel.HomeNoteUiAction
import com.example.notestrack.home.presentation.viewmodel.HomeNoteUiState
import com.example.notestrack.home.presentation.viewmodel.HomeNotesViewModel
import com.example.notestrack.home.presentation.viewmodel.LoadState
import com.example.notestrack.utils.convertMsToDateFormat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
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

        onClickListener(accept,uiState)

        bindUiDetails(uiState)

    }

    private fun FragmentHomeBinding.onClickListener(accept: (HomeNoteUiAction) -> Unit, uiState: StateFlow<HomeNoteUiState>) {

        calender.setOnClickListener {
            val today = Calendar.getInstance().apply {
                timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
            }

            val currentInMs = uiState.value.selectedPickedDate.takeIf { it>0 }?:Instant.now().toEpochMilli()

            val calendarConstraints = CalendarConstraints.Builder()
            calendarConstraints.setValidator(DateValidatorPointBackward.before(today.timeInMillis))
//            calendarConstraints.setStart(today.timeInMillis)
//            today.add(Calendar.YEAR,1)
//            today.set(Calendar.MONTH,11)
            calendarConstraints.setEnd(today.timeInMillis)

            MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .setTitleText("Pick Date 📆")
                .setSelection(currentInMs)
                .setCalendarConstraints(calendarConstraints.build())
                .build().also { picker->
                    picker.show(childFragmentManager,"MaterialDatePicker")
                }
                .also {
                    it.addOnPositiveButtonClickListener { item->
                        etSearch.setText("")
                        accept.invoke(HomeNoteUiAction.OnTypeToSearch("IDLE"))
                        accept.invoke(HomeNoteUiAction.DatePickerFilter(item))
                    }
                }
        }

        tvDateOfSelection.setOnClickListener {
            etSearch.setText("")
            accept.invoke(HomeNoteUiAction.OnTypeToSearch("IDLE"))
            accept.invoke(HomeNoteUiAction.DatePickerFilter(0))
        }

        uiState.map { it.selectedPickedDate }.onEach {
           tvDateOfSelection.isVisible = it!=0L
            tvDateOfSelection.text = convertMsToDateFormat(it)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        etSearch.addTextChangedListener(object :TextWatcher{
            lateinit var countDownTimer: CountDownTimer
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                countDownTimer = object :CountDownTimer(100,200){
                    override fun onTick(p0: Long) {
                        accept.invoke(HomeNoteUiAction.OnTypeToSearch(s.toString()))
                    }
                    override fun onFinish() {
                    }
                }.start()
            }
        })
    }

    private fun FragmentHomeBinding.bindUiDetails(user: StateFlow<HomeNoteUiState>) {
        user.map { it.userDetailEntity }.onEach {
            tvUserName.text = getString(R.string.hey_s,it.userName)
            ivUserImage.text = it.userImage
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        user.map { it.statusOfToolHead }.distinctUntilChanged().onEach {
            tvStatusOfNow.text = it
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        user.map { it.loadState }.distinctUntilChanged().onEach {
            println("user.map { it.loadState } >>> filter >>$it")
            when(it){
                LoadState.IDLE -> Unit
                LoadState.EMPTY,
                LoadState.LOAD -> {
                    rvNotesTitle.isVisible = false
                    placeLottie.root.isVisible = true
                }
                LoadState.NOT_LOAD -> {
                    rvNotesTitle.isVisible = true
                    placeLottie.root.isVisible = false
                }

                else -> {}
            }
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
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}