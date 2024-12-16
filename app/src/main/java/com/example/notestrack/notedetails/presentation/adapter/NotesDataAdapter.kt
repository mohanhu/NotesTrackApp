package com.example.notestrack.notedetails.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notestrack.databinding.NotesTemplateBinding
import com.example.notestrack.notedetails.data.model.NotesData

class NotesDataAdapter:ListAdapter<NotesData,NotesDataAdapter.ViewHolder>(DifferCardNotes) {

    inner class ViewHolder(val binding:NotesTemplateBinding):RecyclerView.ViewHolder(binding.root){
        fun bindItem(data: NotesData){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(NotesTemplateBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

}

object DifferCardNotes : DiffUtil.ItemCallback<NotesData>(){
    override fun areItemsTheSame(oldItem: NotesData, newItem: NotesData): Boolean {
        return oldItem.notesId == newItem.notesId
    }

    override fun areContentsTheSame(oldItem: NotesData, newItem: NotesData): Boolean {
        return oldItem == newItem
    }

}
