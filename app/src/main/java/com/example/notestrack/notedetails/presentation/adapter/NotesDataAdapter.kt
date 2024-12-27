package com.example.notestrack.notedetails.presentation.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notestrack.R
import com.example.notestrack.databinding.NotesTemplateBinding
import com.example.notestrack.notedetails.data.model.NotesData
import com.example.notestrack.richlib.MarkDownCallBack
import com.example.notestrack.richlib.NeedPatternList
import com.example.notestrack.richlib.PATTERN_TYPE
import com.example.notestrack.richlib.spanrichlib.BlockExport.blockJsonToExportDataTextView
import com.example.notestrack.utils.convertMsToDateFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesDataAdapter(
    private val onLongClickListener:(NotesData,View)->Unit,
    private val onClickMessage:(String,PATTERN_TYPE)->Unit,
    private val pinClickListener:(NotesData)->Unit
):ListAdapter<NotesData,NotesDataAdapter.ViewHolder>(DifferCardNotes), MarkDownCallBack {

    inner class ViewHolder(val binding:NotesTemplateBinding):RecyclerView.ViewHolder(binding.root){

        private lateinit var dataLocal:NotesData
        fun bindItem(data: NotesData){

            dataLocal = data

            binding.apply {
                tvTitleMenu.text = data.notesName
                tvDesc.text = data.notesDesc
                val blockList =  tvDesc.blockJsonToExportDataTextView(blockJson = data.notesBlock)
                CoroutineScope(Dispatchers.Main).launch {
                    val needPatternList = listOf(NeedPatternList(neededType = PATTERN_TYPE.URL_PATTERN, color = R.color.deep_sky_blue),)
                    tvDesc.getInstance(this@NotesDataAdapter)
                    tvDesc.startPatternRecognition(markDown = false, needPatternList = needPatternList, mentionList = blockList)
                }
                tvCreatedAt.text = convertMsToDateFormat(data.date)
                itemView.setOnLongClickListener {
                    onLongClickListener.invoke(data,root)
                    true
                }
                ivPinned.setOnClickListener {
                    pinClickListener.invoke(data)
                }
                bindPinStatus(data.pinnedStatus)
            }
        }

        fun bindPinStatus(pinStatus:Boolean){
            dataLocal.pinnedStatus = pinStatus
            binding.ivPinned.isSelected = pinStatus
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(NotesTemplateBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    override fun commonOnClick(patternType: PATTERN_TYPE, clickedString: String) {
        onClickMessage.invoke(clickedString,patternType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()){
             super.onBindViewHolder(holder, position, payloads)
        }
        val bundle = (payloads.firstOrNull() as? Bundle)?:kotlin.run { super.onBindViewHolder(holder, position, payloads) ;return }

        if (bundle.containsKey("pinnedStatus")){
            holder.bindPinStatus(bundle?.getBoolean("pinnedStatus")?:false)
        }
    }
}

object DifferCardNotes : DiffUtil.ItemCallback<NotesData>(){
    override fun areItemsTheSame(oldItem: NotesData, newItem: NotesData): Boolean {
        return oldItem.notesId == newItem.notesId
    }

    override fun areContentsTheSame(oldItem: NotesData, newItem: NotesData): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: NotesData, newItem: NotesData): Any? {
        val bundle = bundleOf()
        if (oldItem.pinnedStatus!=newItem.pinnedStatus){
            bundle.putBoolean("pinnedStatus",newItem.pinnedStatus)
        }
        return bundle
    }
}
