package com.example.stunting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.databinding.ItemCegahStuntingBinding
import com.example.stunting.resouce_data.CegahStuntingData

class ListCegahStuntingAdapter(val items: ArrayList<CegahStuntingData>) :
    RecyclerView.Adapter<ListCegahStuntingAdapter.ListViewHolder>() {

    class ListViewHolder(val binding: ItemCegahStuntingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemCegahStuntingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (title, image) = items[position]
        holder.binding.tvTitleCegahStunting.text = title
        holder.binding.ivCegahStunting.setImageResource(image)
    }
}