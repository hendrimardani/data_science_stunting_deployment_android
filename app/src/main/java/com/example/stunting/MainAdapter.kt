package com.example.stunting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.Database.BabyEntity
import com.example.stunting.databinding.ItemAdapterBinding

class MainAdapter(val items: ArrayList<BabyEntity>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        val llItem = binding.llItem
        val tvUmurItem = binding.tvItemUmur
        val tvJkItem = binding.tvItemJk
        val tvTinggiItem = binding.tvItemTinggi
        val tvKlasifikasiItem = binding.tvItemKlasifikasi
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        holder.tvUmurItem.text = item.umur
        holder.tvJkItem.text = item.jenisKelamin
        holder.tvTinggiItem.text = item.tinggi
        holder.tvKlasifikasiItem.text = item.klasifikasi

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
        } else {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }
}