package com.example.notestrack.home.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.notestrack.databinding.HomeNotesListViewBinding
import com.example.notestrack.home.domain.model.NotesHomeMenuData
import com.example.notestrack.utils.StringFormation.capitalise
import com.example.notestrack.utils.convertMsToDateFormat

class NotesHomeAdapter(
    private val pick:(NotesHomeMenuData)->Unit,
    private val longClick:(NotesHomeMenuData, View)->Unit
) :
    ListAdapter<NotesHomeMenuData, NotesHomeAdapter.ViewHolderOne>(DifferNotes) {

    inner class ViewHolderOne(private val binding: HomeNotesListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: NotesHomeMenuData) {
            binding.apply {
                cvCardColor.strokeColor = Color.parseColor(data.pickedColor.ifEmpty { "#800080" })
                tvTitleOfNotes.text = data.menuTitle.capitalise()
                Glide.with(itemView.context).load(data.thumbNail.ifEmpty { "https://projectsly.com/images/blog/task-management-strategies.png?v=1686553999071005322" })
                    .apply(RequestOptions.centerCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivThumbNail)
                tvTotalNotes.text = data.count.toString()
                tvCreatedAt.text = convertMsToDateFormat(data.createdAt)
            }
            itemView.setOnClickListener {
                pick.invoke(data)
            }
            itemView.setOnLongClickListener {
                longClick.invoke(data,binding.root)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOne {
        return ViewHolderOne(
            binding = HomeNotesListViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolderOne, position: Int) {
        holder.bindItem(getItem(position))
    }
}

object DifferNotes : DiffUtil.ItemCallback<NotesHomeMenuData>() {
    override fun areItemsTheSame(oldItem: NotesHomeMenuData, newItem: NotesHomeMenuData): Boolean {
        return oldItem.menuNotesId == newItem.menuNotesId
    }

    override fun areContentsTheSame(
        oldItem: NotesHomeMenuData,
        newItem: NotesHomeMenuData
    ): Boolean {
        return oldItem == newItem
    }
}
