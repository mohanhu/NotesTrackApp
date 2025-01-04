package com.example.notestrack.task.util.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.notestrack.R
import com.example.notestrack.databinding.ItemEmptyProjectsBinding
import timber.log.Timber

data class PlaceHolderData(
    val id: Int,
    val cornerRadiusPx: Int = 0,
    val colorRes: Int = -1,
)

class PlaceholderAdapter(
    private val cornerRadiusPx: Int,
    private val colorRes: Int,
) : ListAdapter<PlaceHolderData, PlaceholderAdapter.ItemViewHolder>(
    DiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent, colorRes, cornerRadiusPx)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        Timber.d("onBindViewHolder() called with: holder = [$holder], position = [$position]")
        // Noop.
    }

    class ItemViewHolder private constructor(imageView: ImageView) : ViewHolder(imageView) {

        companion object {
            fun from(parent: ViewGroup, colorRes: Int, cornerRadiusPx: Int = 0): ItemViewHolder {
                val color = ResourcesCompat.getColor(
                    parent.resources,
                    colorRes,
                    parent.context.theme
                )
                val holder = ImageView(parent.context).apply {
                    id = View.generateViewId()
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
//                    setImageDrawable(
//                        getRoundedDrawable(
//                            radius = cornerRadiusPx,
//                            bgColor = color
//                        )
//                    )
                }
                return ItemViewHolder(holder)
            }
        }
    }

    companion object {
        private val DiffCallback = object : ItemCallback<PlaceHolderData>() {
            override fun areItemsTheSame(
                oldItem: PlaceHolderData,
                newItem: PlaceHolderData,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PlaceHolderData,
                newItem: PlaceHolderData,
            ): Boolean {
                return true
            }
        }
    }
}

class EmptyListViewHolder(
    private val binding: ItemEmptyProjectsBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        titleText: String="Empty",
        onActionClick: () -> Unit
    ) = with(binding) {
        title.text = titleText

        actionButton.setOnClickListener { onActionClick() }
    }

    companion object {
        fun from(parent: ViewGroup): EmptyListViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_empty_projects, parent, false)
            val binding = ItemEmptyProjectsBinding.bind(itemView)
            return EmptyListViewHolder(binding)
        }
    }
}