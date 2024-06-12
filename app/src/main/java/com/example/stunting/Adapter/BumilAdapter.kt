package com.example.stunting.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.Database.Bumil.BumilEntity
import com.example.stunting.R
import com.example.stunting.databinding.ItemBumilAdapterBinding

class BumilAdapter(val items: ArrayList<BumilEntity>): RecyclerView.Adapter<BumilAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemBumilAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        val llItem = binding.llItem
        val tvTanggal = binding.tvItemTanggalBumil
        val tvNik = binding.tvItemNikBumil
        val tvNama = binding.tvItemNamaBumil
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBumilAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        holder.tvTanggal.text = item.tglLahirBumil
        holder.tvNik.text = item.nikBumil
        holder.tvNama.text = item.namaBumil

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
        } else {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }
}