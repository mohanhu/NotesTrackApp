package com.example.notestrack.task.util.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Space
import androidx.annotation.Px
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.notestrack.databinding.LoadStateFooterViewItemBinding
import timber.log.Timber

data class Spacer(
    @Orientation val orientation: Int,
    @Px val horizontalSpace: Int = 0,
    @Px val verticalSpace: Int = 0,
)

class SpacerAdapter : ListAdapter<Spacer, SpacerAdapter.SpacerViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpacerViewHolder {
        return SpacerViewHolder(Space(parent.context))
    }

    override fun onBindViewHolder(holder: SpacerViewHolder, position: Int) {
        val spacer = getItem(position)
        Timber.d("onBindViewHolder() called spacer=$spacer")
        holder.bind(spacer)
    }

    class SpacerViewHolder(val space: Space) : ViewHolder(space) {
        fun bind(spacer: Spacer) {
            val lp: ViewGroup.LayoutParams = if (spacer.orientation == RecyclerView.HORIZONTAL) {
                ViewGroup.LayoutParams(
                    spacer.horizontalSpace,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            } else {
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    spacer.verticalSpace
                )
            }
            space.layoutParams = lp
        }
    }

    companion object {
        private val DiffCallback = object : ItemCallback<Spacer>() {
            override fun areItemsTheSame(oldItem: Spacer, newItem: Spacer): Boolean {
                return true
            }

            override fun areContentsTheSame(oldItem: Spacer, newItem: Spacer): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class LoadStateAdapter : ListAdapter<String, LoadStateAdapter.LoadStateViewHolder>(DiffCallbacks) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadStateViewHolder {
        return LoadStateViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, position: Int) {
        val spacer = getItem(position)
        Timber.d("onBindViewHolder() called spacer=$spacer")
        holder.bind(spacer)
    }

    class LoadStateViewHolder(val binding: LoadStateFooterViewItemBinding) : ViewHolder(binding.root) {
        fun bind(spacer: String) {

        }

        companion object{
            fun create(parent: ViewGroup):LoadStateViewHolder =

                LoadStateViewHolder(binding = LoadStateFooterViewItemBinding.inflate(
                    LayoutInflater.from(parent.context),parent,false
                )
                )
        }
    }

    companion object {
        private val DiffCallbacks = object : ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return true
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}