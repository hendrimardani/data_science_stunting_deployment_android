package com.example.stunting.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.Database.Bumil.BumilEntity
import com.example.stunting.R
import com.example.stunting.databinding.ItemBumilAdapterBinding

class BumilAdapter(val items: ArrayList<BumilEntity>) : RecyclerView.Adapter<BumilAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemBumilAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val llItemBumil = binding.llItemBumil
        val tglBumil = binding.tvItemTanggalBumil
        val nikBumil = binding.tvItemNikBumil
        val namaBumil = binding.tvItemNamaBumil
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBumilAdapterBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tglBumil.text = item.tanggal
        holder.nikBumil.text = item.nikBumil
        holder.namaBumil.text = item.namaBumil

        if (position % 2 == 0) {
            holder.llItemBumil.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.light_gray))
        } else {
            holder.llItemBumil.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.white))
        }
    }
}