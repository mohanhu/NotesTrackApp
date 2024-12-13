package com.example.notestrack.addnote.presentation.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import com.example.notestrack.richlib.RichTypeEnum
import com.example.notestrack.richlib.spanrichlib.BulletOrdering.addBulletList
import com.example.notestrack.richlib.spanrichlib.BulletOrdering.bulletFormatForward
import com.example.notestrack.richlib.spanrichlib.NumberOrdering.addNumberList
import com.example.notestrack.richlib.spanrichlib.NumberOrdering.formatNumberForward
import com.example.notestrack.richlib.spanrichlib.NumberOrdering.toFormatNumberBasedOnCursor
import com.example.notestrack.richlib.spanrichlib.RichSpanDownStyle.listenCurrentStyleFormat
import com.example.notestrack.richlib.spanrichlib.RichSpanDownStyle.onTypeStateChange
import com.example.notestrack.richlib.spanrichlib.RichSpanDownStyle.toggleStyle
import com.example.notestrack.richlib.spanrichlib.Styles
import com.example.notestrack.utils.ViewExtentions.showKeyBoard
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

    private var LAST_SPAN_RICH_EDITOR_CURSOR_POSITION = 2
    private var CURRENT_RICH_STYLE_FORMAT = Styles.PLAIN

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

        onClick(accept)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun FragmentAddNotesBinding.onClick(accept: (NotesUiAction) -> Unit) {

        evTitle.showKeyBoard()

        evDesc.setOnFocusChangeListener { _, b ->
            rvStyleMark.isVisible = b
        }

        evDesc.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.post {
                    evDesc.listenCurrentStyleFormat{ styles: Styles ->
                        CURRENT_RICH_STYLE_FORMAT = styles
                        LAST_SPAN_RICH_EDITOR_CURSOR_POSITION = evDesc.selectionStart
                        observeStyleType(styles)
                    }
                    println("FragmentAddNotesBinding ACTION_UP >>1>$CURRENT_RICH_STYLE_FORMAT")
                }
            }
            false
        }

        evDesc.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val cursorPos = evDesc.selectionStart?.takeIf { it>=0 }?:0
                println("EditText.onTypeStateChange >>>> $cursorPos >>$count")
                if (LAST_SPAN_RICH_EDITOR_CURSOR_POSITION < cursorPos) {
                    evDesc.apply {
                        onTypeStateChange(cursorPos,count, CURRENT_RICH_STYLE_FORMAT)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
                val cursorPos = evDesc.selectionStart?.takeIf { it>=0 }?:0
                if (LAST_SPAN_RICH_EDITOR_CURSOR_POSITION < cursorPos) {
                    evDesc.bulletFormatForward(cursor = cursorPos)
                }
                if (LAST_SPAN_RICH_EDITOR_CURSOR_POSITION<cursorPos){
                    evDesc.formatNumberForward(cursorPos)
                }
                evDesc.toFormatNumberBasedOnCursor(cursorPos)
                LAST_SPAN_RICH_EDITOR_CURSOR_POSITION = evDesc.selectionStart
//                listenerMentionTag(s.toString())
//                listenerBlockOfData()
            }
        })
    }

    private fun FragmentAddNotesBinding.toggleWithUpdateStyle(richTypeEnum: RichTypeEnum, styles: Styles){
        CURRENT_RICH_STYLE_FORMAT = if (CURRENT_RICH_STYLE_FORMAT == styles){
            evDesc.toggleStyle(Styles.PLAIN)
            Styles.PLAIN
        }
        else{
            evDesc.toggleStyle(styles)
            styles
        }
        updateSelectStatus(richTypeEnum)
    }

    private fun FragmentAddNotesBinding.bindList(
        uiState: StateFlow<NotesUiState>,
        accept: (NotesUiAction) -> Unit
    ) {
        val adapter = RichStyleAdapter(
            richRefreshStyle = {
                when(it.richType){
                    RichTypeEnum.NORMAL -> Unit
                    RichTypeEnum.BOLD -> {
                        toggleWithUpdateStyle(it.richType,Styles.BOLD)
                    }
                    RichTypeEnum.ITALIC -> {
                        toggleWithUpdateStyle(it.richType,Styles.ITALIC)
                    }
                    RichTypeEnum.STRIKE -> {
                        toggleWithUpdateStyle(it.richType,Styles.STRIKE)
                    }
                    RichTypeEnum.UNDER_LINE -> {
                        toggleWithUpdateStyle(it.richType,Styles.UNDER_LINE)
                    }
                    RichTypeEnum.ADD_LINK -> Unit
                    RichTypeEnum.ALIGN_LEFT -> Unit
                    RichTypeEnum.ALIGN_RIGHT -> Unit
                    RichTypeEnum.ALIGN_CENTER -> Unit
                    RichTypeEnum.LIST_BULLET ->{
                        updateSelectStatus(RichTypeEnum.NONE)
                        evDesc.addBulletList()
                    }
                    RichTypeEnum.LIST_NUMBER ->{
                        updateSelectStatus(RichTypeEnum.NONE)
                        evDesc.addNumberList()
                    }
                    RichTypeEnum.CODE_SYMBOL -> Unit
                    RichTypeEnum.CODE_BLOCK -> Unit
                    RichTypeEnum.NONE -> Unit
                }
            }
        )
        rvStyleMark.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        rvStyleMark.adapter = adapter
        uiState.map { it.richStyle }.distinctUntilChanged().onEach {
            adapter.submitList(it)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun observeStyleType(styles: Styles) {
        when(styles){
            Styles.BOLD -> updateSelectStatus(RichTypeEnum.BOLD, selectNow = true)
            Styles.ITALIC -> updateSelectStatus(RichTypeEnum.ITALIC, selectNow = true)
            Styles.STRIKE -> updateSelectStatus(RichTypeEnum.STRIKE, selectNow = true)
            Styles.UNDER_LINE -> updateSelectStatus(RichTypeEnum.UNDER_LINE, selectNow = true)
            else-> updateSelectStatus(RichTypeEnum.NONE)
        }
    }

    private fun updateSelectStatus(richTypeEnum: RichTypeEnum,selectNow:Boolean = false) {
        viewModel.accept.invoke(NotesUiAction.SelectRichStyle(richEditDataClass = richTypeEnum,selectNow))
    }
}