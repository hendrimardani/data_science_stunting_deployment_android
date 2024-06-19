package com.example.stunting.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.Database.Bumil.BumilEntity
import com.example.stunting.Database.CalonPengantin.CalonPengantinEntity
import com.example.stunting.R
import com.example.stunting.databinding.ItemAllAdapterBinding

class BumilAdapter(val items: ArrayList<BumilEntity>) : RecyclerView.Adapter<BumilAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemAllAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val llItem = binding.llItem
        val tgl = binding.tvItemTanggal
        val nik = binding.tvItemNik
        val nama = binding.tvItemNama
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAllAdapterBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tgl.text = item.tanggal
        holder.nik.text = item.nikBumil
        holder.nama.text = item.namaBumil

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.light_gray))
        } else {
            holder.llItem.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.white))
        }
    }
}

class CalonPengantinAdapter(val items: ArrayList<CalonPengantinEntity>) : RecyclerView.Adapter<CalonPengantinAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemAllAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val llItem = binding.llItem
        val tgl = binding.tvItemTanggal
        val nik = binding.tvItemNik
        val nama = binding.tvItemNama
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAllAdapterBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tgl.text = item.tanggal
        holder.nik.text = item.nikCalonPengantin
        holder.nama.text = item.namaCalonPengantin

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.light_gray))
        } else {
            holder.llItem.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.white))
        }
    }
}