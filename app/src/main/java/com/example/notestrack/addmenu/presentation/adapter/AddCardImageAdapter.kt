package com.example.notestrack.addmenu.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.notestrack.R
import com.example.notestrack.addmenu.data.model.PhotoDto
import com.example.notestrack.databinding.ImageChoosenLayOutBinding

class AddCardImageAdapter(
    private val onImageClick:(String)->Unit
):PagingDataAdapter<PhotoDto,AddCardImageAdapter.ViewHolder>(DifferCard) {

    inner class ViewHolder(val binding: ImageChoosenLayOutBinding):RecyclerView.ViewHolder(binding.root){
        fun bindItem(data:PhotoDto){
            println("AddCardImageAdapter >>> ${data.src}")
            Glide.with(itemView.context).load(data.src.original).apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.ev_search_back_ground) // optional
                .error(R.drawable.ev_search_back_ground) // optional
                .into(binding.ivImage)

            itemView.setOnClickListener {
                onImageClick.invoke(data.src.original)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bindItem(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(binding = ImageChoosenLayOutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}

object DifferCard : DiffUtil.ItemCallback<PhotoDto>() {
    override fun areItemsTheSame(oldItem: PhotoDto, newItem: PhotoDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotoDto, newItem: PhotoDto): Boolean {
        return oldItem == newItem
    }

}
