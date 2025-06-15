package com.example.stunting.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.databinding.ItemCardStackViewBinding
import com.example.stunting.resouce_data.Services

class CardStackViewAdapter: ListAdapter<Services, CardStackViewAdapter.MyViewHolder>(DIFF_CALLBACK){

    class MyViewHolder(private val binding: ItemCardStackViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(services: Services) {
            val name = services.name
            val image = services.image

            binding.tvName.text = name
            binding.imageView.setImageResource(image)
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Terklik", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardStackViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    companion object {
        private val TAG = CardStackViewAdapter::class.java.simpleName
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Services> =
            object : DiffUtil.ItemCallback<Services>() {
                override fun areItemsTheSame(oldItem: Services, newItem: Services): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: Services, newItem: Services): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
