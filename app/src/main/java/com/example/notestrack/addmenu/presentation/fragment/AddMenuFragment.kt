package com.example.notestrack.addmenu.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.notestrack.R
import com.example.notestrack.addmenu.domain.model.Photo
import com.example.notestrack.addmenu.presentation.adapter.AddCardImageAdapter
import com.example.notestrack.addmenu.presentation.viewmodel.AddCategoryUiAction
import com.example.notestrack.addmenu.presentation.viewmodel.AddCategoryUiEvent
import com.example.notestrack.addmenu.presentation.viewmodel.AddCategoryUiState
import com.example.notestrack.addmenu.presentation.viewmodel.AddCategoryViewModel
import com.example.notestrack.databinding.FragmentAddMenuBinding
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.utils.ViewExtentions.showKeyBoard
import com.example.notestrack.utils.convertMsToDateFormat
import com.google.android.material.snackbar.Snackbar
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.Instant

@AndroidEntryPoint
class AddMenuFragment : Fragment() {

    private val binding: FragmentAddMenuBinding by lazy { FragmentAddMenuBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<AddCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState)

        binding.apply {
            bindState(
                viewModel.imageListPaging,
                viewModel.accept,
                viewModel.uiState,
                viewModel.uiEvent
            )
        }
    }

    private fun FragmentAddMenuBinding.bindState(
        imageListPaging: Flow<PagingData<Photo>>,
        accept: (AddCategoryUiAction) -> Unit,
        uiState: StateFlow<AddCategoryUiState>,
        uiEvent: SharedFlow<AddCategoryUiEvent>
    ) {

        bundleExtraction()

        bindList(imageListPaging,accept)

        onClickListener(accept)

        observerCardList(uiState)

        eventListener(uiEvent)
    }

    private fun bundleExtraction() {
        if (arguments?.containsKey("addMenuFragment") == true){
            val bundle = arguments?.getParcelable<NotesHomeMenuData>("addMenuFragment")?: NotesHomeMenuData()
            binding.apply {
                evTitle.setText(bundle.menuTitle)
                evTitle.showKeyBoard()
                tvUserName.text = ContextCompat.getString(requireContext(), R.string.edit_category)
            }
            viewModel.accept.invoke(AddCategoryUiAction.EditNotesHomeMenuData(bundle))
        }
    }

    private fun eventListener(uiEvent: SharedFlow<AddCategoryUiEvent>) {
        uiEvent.onEach {
            when(it){
                AddCategoryUiEvent.BackStack -> findNavController().popBackStack()
                is AddCategoryUiEvent.ShowSnackBar -> {
                    Snackbar.make(binding.root,it.message,Snackbar.LENGTH_SHORT)
                        .setTextColor(Color.BLACK)
                        .setBackgroundTint(Color.WHITE).show()
                }

                else -> {}
            }
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun FragmentAddMenuBinding.observerCardList(uiState: StateFlow<AddCategoryUiState>) {
        uiState.map { it.categoryImage }.distinctUntilChanged().onEach {
            Glide.with(requireView()).load(it).diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.centerCropTransform()).into(overviewCard.ivThumbNail)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.categoryTitle }.distinctUntilChanged().onEach {
           overviewCard.tvTitleOfNotes.text = it
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.color }.distinctUntilChanged().onEach {
            if(it.isNotEmpty()){
                overviewCard.cvCardColor.strokeColor = Color.parseColor(it)
                cardPickColor.setCardBackgroundColor(Color.parseColor(it))
            }
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.buttonEnable }.distinctUntilChanged().onEach {
            btnConfirmOuter.isVisible = it
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        overviewCard.tvCreatedAt.text = convertMsToDateFormat(Instant.now().toEpochMilli())
    }

    private fun FragmentAddMenuBinding.bindList(
        imageListPaging: Flow<PagingData<Photo>>,
        accept: (AddCategoryUiAction) -> Unit
    ) {
        val adapter = AddCardImageAdapter(
            onImageClick = {
                accept.invoke(AddCategoryUiAction.ChooseIcon(it))
            }
        )
        rvImageChoosen.adapter = adapter
        rvImageChoosen.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        imageListPaging.onEach {
            adapter.submitData(it)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.uiState.map { it.searchQuery }.distinctUntilChanged().onEach {
            adapter.refresh()
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun FragmentAddMenuBinding.onClickListener(accept: (AddCategoryUiAction) -> Unit) {

        overviewCard.tvTitleOfNotes.hint = "Here your title ..."
        ivBack.setOnClickListener { findNavController().popBackStack() }

        evTitle.doAfterTextChanged {
            accept.invoke(AddCategoryUiAction.TypingTitle(it.toString()))
        }

        evCardName.addTextChangedListener(object :TextWatcher{
            lateinit var countDownTimer: CountDownTimer
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {

                countDownTimer = object :CountDownTimer(100,200){
                    override fun onTick(p0: Long) {}
                    override fun onFinish() {
                        accept.invoke(AddCategoryUiAction.TypingBackGround(s.toString()))
                    }
                }.start()
            }
        })

        cardPickColor.setOnClickListener {
            ColorPickerDialog.Builder(requireContext())
                .setTitle("Pick color")
                .setPreferenceName("MyColorPickerDialog")
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(false)
                .setBottomSpace(12)
                .setPositiveButton("Choose",object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        val colorString = "#${envelope?.hexCode}"
                        if (fromUser) {
                            accept.invoke(AddCategoryUiAction.ChooseColorCardStroke(colorString))
                        }
                    }
                })
                .setNegativeButton("Cancel"){d,_-> d.dismiss()}
                .show()

        }

        btnConfirmOuter.setOnClickListener {
            accept.invoke(AddCategoryUiAction.SubmitMenuCategory)
        }
    }
}