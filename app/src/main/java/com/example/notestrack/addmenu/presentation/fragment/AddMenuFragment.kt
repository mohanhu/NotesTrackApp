package com.example.notestrack.addmenu.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.notestrack.addmenu.data.model.PhotoDto
import com.example.notestrack.addmenu.presentation.adapter.AddCardImageAdapter
import com.example.notestrack.addmenu.presentation.viewmodel.AddCategoryUiAction
import com.example.notestrack.addmenu.presentation.viewmodel.AddCategoryUiState
import com.example.notestrack.addmenu.presentation.viewmodel.AddCategoryViewModel
import com.example.notestrack.databinding.FragmentAddMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bindState(
            viewModel.imageListPaging,
            viewModel.accept,
            viewModel.uiState
        )
    }

    private fun FragmentAddMenuBinding.bindState(
        imageListPaging: Flow<PagingData<PhotoDto>>,
        accept: (AddCategoryUiAction) -> Unit,
        uiState: StateFlow<AddCategoryUiState>
    ) {

        bindList(imageListPaging,accept)

        onClickListener(accept)

        observerCardList(uiState)
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
    }

    private fun FragmentAddMenuBinding.bindList(
        imageListPaging: Flow<PagingData<PhotoDto>>,
        accept: (AddCategoryUiAction) -> Unit
    ) {
        val adapter = AddCardImageAdapter(
            onImageClick = {
                accept.invoke(AddCategoryUiAction.ChooseIcon(it))
            }
        )
        rvImageChoosen.adapter = adapter
        imageListPaging.onEach {
            adapter.submitData(it)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun FragmentAddMenuBinding.onClickListener(accept: (AddCategoryUiAction) -> Unit) {

        overviewCard.tvTitleOfNotes.hint = "Here your title ..."
        ivBack.setOnClickListener { findNavController().popBackStack() }

        evTitle.doAfterTextChanged {
            accept.invoke(AddCategoryUiAction.TypingTitle(it.toString()))
        }

    }
}