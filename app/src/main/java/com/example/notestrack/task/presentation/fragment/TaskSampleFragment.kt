package com.example.notestrack.task.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notestrack.R
import com.example.notestrack.addmenu.domain.model.MediaType
import com.example.notestrack.addmenu.domain.model.Photo
import com.example.notestrack.databinding.FragmentTaskSampleBinding
import com.example.notestrack.databinding.ImageChoosenLayOutBinding
import com.example.notestrack.home.presentation.viewmodel.LoadState
import com.example.notestrack.task.presentation.fragment.DifferTask.VIEW_TYPE_FOOTER
import com.example.notestrack.task.presentation.fragment.DifferTask.VIEW_TYPE_HEADER
import com.example.notestrack.task.presentation.fragment.DifferTask.VIEW_TYPE_ITEM
import com.example.notestrack.task.presentation.fragment.DifferTask.VIEW_TYPE_PLACEHOLDER
import com.example.notestrack.task.presentation.fragment.DifferTask.VIEW_TYPE_SPACER
import com.example.notestrack.task.presentation.viewmodel.TaskSampleUiState
import com.example.notestrack.task.presentation.viewmodel.TaskSampleViewModel
import com.example.notestrack.task.presentation.viewmodel.TaskUiAction
import com.example.notestrack.task.presentation.viewmodel.TaskUiEvent
import com.example.notestrack.task.presentation.viewmodel.TaskUiModel
import com.example.notestrack.task.util.adapter.EmptyListViewHolder
import com.example.notestrack.task.util.adapter.LoadStateAdapter
import com.example.notestrack.task.util.adapter.Spacer
import com.example.notestrack.task.util.adapter.SpacerAdapter
import com.example.notestrack.utils.DragSwipeGestureDeduct
import com.example.notestrack.utils.FRAGMENT_RESULT
import com.example.notestrack.utils.RESULT_OK
import com.example.notestrack.utils.clearNavigationResult
import com.example.notestrack.utils.getNavigationResultFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Collections

@AndroidEntryPoint
class TaskSampleFragment : Fragment() {

    private val binding: FragmentTaskSampleBinding by lazy {
        FragmentTaskSampleBinding.inflate(
            layoutInflater
        )
    }

    private var fragmentResultKey: String? = null

    private val viewModel by viewModels<TaskSampleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bindState(
            uiState = viewModel.uiState,
            accept = viewModel.accept,
            uiEvent = viewModel.uiEvent,
            scrollToTopSignal = viewModel.scrollToTopSignal,
            loadState = viewModel.uiState.map { it.loadState }.distinctUntilChanged(),
            onRetry = {

            }
        )

    }

    private fun FragmentTaskSampleBinding.bindState(
        uiState: StateFlow<TaskSampleUiState>,
        accept: (TaskUiAction) -> Unit,
        uiEvent: SharedFlow<TaskUiEvent>,
        scrollToTopSignal: SharedFlow<Boolean>,
        loadState: Flow<LoadState>,
        onRetry: () -> Unit
    ) {

        evSearch.setOnClickListener {
            fragmentResultKey = "Sample"
            val bundle = bundleOf("EXTRA" to "popup")
            findNavController().navigate(R.id.sampleBackFragment,bundle)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
//                getNavigationResultFlow<Int>(FRAGMENT_RESULT)?.collectLatest { result->
//                    println("findNavController().currentBackStackEntry?.savedStateHandle $result")
//
//                    if (result== RESULT_OK && fragmentResultKey=="Sample"){
//                        Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT).show()
//                    }
//
//                    if (result!=null){
//                        clearNavigationResult<Int>(FRAGMENT_RESULT)
//                    }
//                }
                fragmentResultKey = null
            }
        }

        uiEvent.onEach { event ->
            when (event) {
                else -> {}
            }
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        loadState.distinctUntilChanged().onEach { load ->
            /*Handle Load state anim and error*/
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        scrollToTopSignal.distinctUntilChanged().onEach { scroll ->
            if (scroll) {
                try {
                    val layoutManager = rvList.layoutManager as LinearLayoutManager
                    if (layoutManager.findFirstVisibleItemPosition() < SCROLL_ANIMATION_THRESHOLD) {
                        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
                            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
                        }
                        smoothScroller.targetPosition = 0
                        layoutManager.startSmoothScroll(smoothScroller)
                    } else {
                        layoutManager.scrollToPositionWithOffset(0, 0)
                    }
                } catch (ignore: Exception) {
                }
            }
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)


        val adapter = TaskAdapter(
            context = requireContext(),
            retry = {

            }
        )

        val dragSwipeGestureDeduct = DragSwipeGestureDeduct(
            isApplySwipe = { position: Int ->
                when (adapter.getItemViewType(position)){
                    MediaType.IMAGE.key -> true
                    else-> false
                }
            },
            onItemMove = { fromPosition,toPosition->
               try {
                   val list = adapter.currentList.toMutableList()
                   if (fromPosition < toPosition) {
                       for (i in fromPosition..<toPosition) {
                           Collections.swap(list, i, i + 1)
                       }
                   } else {
                       for (i in fromPosition downTo toPosition) {
                           Collections.swap(list, i, i - 1)
                       }
                   }
                   adapter.submitList(list)
               }
               catch (e:Exception){ }
                true
            },
            swiped = { position, direction ->
                val list = adapter.currentList.toMutableList()
                list.filterIndexed { index, taskUiModel ->
                    index!=position
                }.also {
                    adapter.submitList(it)
                    adapter.notifyItemChanged(position)
                }
            },
            itemClear = {

            },
            itemSelect = {

            }

        )
        val touchHelper = ItemTouchHelper(dragSwipeGestureDeduct)
        touchHelper.attachToRecyclerView(rvList)
        rvList.adapter = adapter
        rvList.setHasFixedSize(true)

        bindList(
            adapter = adapter,
            uiState = uiState,
            accept = accept
        )

        bindInput(
            uiState = uiState,
            accept = accept,
        )
    }

    private fun FragmentTaskSampleBinding.bindInput(
        uiState: StateFlow<TaskSampleUiState>,
        accept: (TaskUiAction) -> Unit
    ) {

        evSearch.doAfterTextChanged { s ->

        }
    }

    private fun FragmentTaskSampleBinding.bindList(
        adapter: TaskAdapter,
        uiState: StateFlow<TaskSampleUiState>,
        accept: (TaskUiAction) -> Unit
    ) {
        val emptyList = uiState.map { it.taskUiModel }
            .map { it.filterIsInstance<TaskUiModel.Item>().isEmpty() }.distinctUntilChanged()

        val refreshLoadState = uiState.map { it.loadState }
            .distinctUntilChangedBy { it }
            .map { it.name == LoadState.LOAD.name }

        emptyList.onEach {
            fab.isVisible = it
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        combine(
            emptyList,
            refreshLoadState,
            Boolean::and
        ).onEach { load ->
            /*Handle Progress bar*/
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        uiState.map { it.taskUiModel }.onEach {
            println("uiState.map { it.taskUiModel }.onEach >$it")
            adapter.submitList(it)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        setUpScroll(
            uiState = uiState,
            accept = accept
        )
    }

    private fun FragmentTaskSampleBinding.setUpScroll(
        uiState: StateFlow<TaskSampleUiState>,
        accept: (TaskUiAction) -> Unit
    ) {
        val layoutManager = rvList.layoutManager as LinearLayoutManager
        val scroller = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {}

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val totalItemCount = layoutManager.itemCount
                    val visibleItemCount = layoutManager.childCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                }
            }
        }

        val hasItems = uiState.map { it.taskUiModel }
            .map { it.filterIsInstance<TaskUiModel.Item>().isNotEmpty() }
        val endOfPagination =
            uiState.map { it.loadState }.distinctUntilChanged().map { it == LoadState.END_PAGE }
                .distinctUntilChanged()

        combine(
            hasItems,
            endOfPagination,
            ::Pair
        ).onEach { (has, end) ->
            if (has && !end) {
                rvList.addOnScrollListener(scroller)
            } else {
                rvList.removeOnScrollListener(scroller)
            }
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)



        lifecycleScope.launch {
            delay(20000)
            viewModel.updateImageAll()
        }

    }

    companion object {
        private const val SCROLL_ANIMATION_THRESHOLD = 20
    }
}

class TaskAdapter(
    private val context: Context,
    private val retry: () -> Unit,
) : ListAdapter<TaskUiModel, RecyclerView.ViewHolder>(DifferTask) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> TextViewHolder.from(parent)
            MediaType.IMAGE.key-> ImageViewHolder.from(parent)
            MediaType.VIDEO.key-> ImageViewHolder.from(parent)
            MediaType.DOC.key-> ImageViewHolder.from(parent)
            VIEW_TYPE_FOOTER -> LoadStateAdapter.LoadStateViewHolder.create(parent)
            VIEW_TYPE_SPACER-> SpacerAdapter.SpacerViewHolder(Space(parent.context))
            else->{
                EmptyListViewHolder.from(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = getItem(position)
        when (holder) {
            is TextViewHolder -> holder.bind((model as TaskUiModel.Item).projects)
            is ImageViewHolder -> holder.bind((model as TaskUiModel.Item).projects)
            is EmptyListViewHolder -> holder.bind((model as TaskUiModel.PlaceHolder).title) {}
            is SpacerAdapter.SpacerViewHolder-> holder.bind(Spacer(orientation = RecyclerView.VERTICAL))
            is LoadStateAdapter.LoadStateViewHolder -> holder.bind((model as TaskUiModel.Footer).loadState.name)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val model = getItem(position)) {
            is TaskUiModel.Header -> VIEW_TYPE_HEADER
            is TaskUiModel.Item -> { model.projects.mediaType.key }
            is TaskUiModel.PlaceHolder -> VIEW_TYPE_PLACEHOLDER
            is TaskUiModel.Footer -> VIEW_TYPE_FOOTER
            TaskUiModel.Spacer -> VIEW_TYPE_SPACER
            else -> error("Unable to decide a viewType for model $model")
        }
    }

}

object DifferTask : DiffUtil.ItemCallback<TaskUiModel>() {

    const val VIEW_TYPE_ITEM = 0
    const val VIEW_TYPE_HEADER = 1
    const val VIEW_TYPE_DRAG_TARGET = 2
    const val VIEW_TYPE_PLACEHOLDER = 3
    const val VIEW_TYPE_FOOTER = 4
    const val VIEW_TYPE_SHOW_ALL = 5
    const val VIEW_TYPE_SPACER = 100

    override fun areItemsTheSame(oldItem: TaskUiModel, newItem: TaskUiModel): Boolean {
        return when {
            oldItem is TaskUiModel.Header && newItem is TaskUiModel.Header -> {
                oldItem.title == newItem.title
            }

            oldItem is TaskUiModel.Item && newItem is TaskUiModel.Item -> {
                oldItem.projects.id == newItem.projects.id
            }

            else -> true
        }
    }

    override fun areContentsTheSame(oldItem: TaskUiModel, newItem: TaskUiModel): Boolean {
        return when {
            oldItem is TaskUiModel.Header && newItem is TaskUiModel.Header -> {
                oldItem.title == newItem.title
            }
            oldItem is TaskUiModel.Item && newItem is TaskUiModel.Item -> {
                oldItem.selected == newItem.selected && oldItem.selectable == newItem.selectable
            }
            else -> true
        }
    }
}

class ImageViewHolder(val binding: ImageChoosenLayOutBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Photo) {
        Glide.with(itemView.context).load(data.src.original).into(binding.ivImage)
    }
    companion object{
        fun from(parent: ViewGroup):ImageViewHolder{
            return ImageViewHolder(binding = ImageChoosenLayOutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}


class TextViewHolder(val binding: ImageChoosenLayOutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Photo) {
        Glide.with(itemView.context).load(data.src.original).into(binding.ivImage)
    }

    companion object{
        fun from(parent: ViewGroup):TextViewHolder{
            return TextViewHolder(binding = ImageChoosenLayOutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}
