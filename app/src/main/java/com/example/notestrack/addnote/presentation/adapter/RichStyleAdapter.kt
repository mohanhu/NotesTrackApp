package com.example.notestrack.addnote.presentation.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.notestrack.R
import com.example.notestrack.databinding.RichStyleCurrentTypeBinding
import com.example.notestrack.richlib.RichEditDataClass

class RichStyleAdapter(
    val richRefreshStyle:(RichEditDataClass)->Unit
) : ListAdapter<RichEditDataClass,RichStyleAdapter.ViewHolder>(DifferStyle) {

    inner class ViewHolder(private val binding: RichStyleCurrentTypeBinding):RecyclerView.ViewHolder(binding.root){
        fun bindItem(dataClass: RichEditDataClass) {
            Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context,dataClass.moreOptionImage)).diskCacheStrategy(
                DiskCacheStrategy.ALL).into(binding.iconDrawable)

            itemView.setOnClickListener { richRefreshStyle(dataClass) }

            setRichBackground(dataClass.moreOptionImage,dataClass.isSelect)
        }

        fun setRichBackground(moreOptionImage: Int, select: Boolean) {
            if (select){
                binding.backGround.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.primary_dark))
            }
            else{
                binding.backGround.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.primary))
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context,moreOptionImage)).diskCacheStrategy(
                    DiskCacheStrategy.ALL).into(binding.iconDrawable)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(binding = RichStyleCurrentTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()){
            return super.onBindViewHolder(holder, position, payloads)
        }
        else{
            val bundle = (payloads.firstOrNull() as? Bundle) ?: kotlin.run { super.onBindViewHolder(holder, position, payloads) ; return }
            if (bundle.containsKey("RichEditDataClass")){
                getItem(position)?.let {
                    holder.setRichBackground(it.moreOptionImage,bundle.getBoolean("RichEditDataClass")?:false)
                }
            }
        }
    }
}

object DifferStyle : DiffUtil.ItemCallback<RichEditDataClass>() {
    override fun areItemsTheSame(oldItem: RichEditDataClass, newItem: RichEditDataClass): Boolean {
        return oldItem.richEditId == newItem.richEditId
    }

    override fun areContentsTheSame(
        oldItem: RichEditDataClass,
        newItem: RichEditDataClass
    ): Boolean {
        return oldItem == newItem
    }

}
