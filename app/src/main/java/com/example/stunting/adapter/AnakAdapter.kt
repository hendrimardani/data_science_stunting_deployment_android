package com.example.stunting.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.database.Child.AnakEntity
import com.example.stunting.R
import com.example.stunting.databinding.ItemAnakAdapterBinding

class AnakAdapter(val items: ArrayList<AnakEntity>) : RecyclerView.Adapter<AnakAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemAnakAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val llItemAnak = binding.llItemAnak
        val tglAnak = binding.tvItemTanggalAnak
        val namaAnak = binding.tvItemNamaAnak
        val klasifikasi = binding.tvItemKlasifikasiAnak
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAnakAdapterBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tglAnak.text = item.tanggal
        holder.namaAnak.text = item.namaAnak
        holder.klasifikasi.text = item.klasifikasiAnak

        // Change color text classification display
        if (item.klasifikasiAnak == ContextCompat.getString(holder.itemView.context, R.string.classification_normal)) {
            holder.klasifikasi.setTextColor(Color.GREEN)
        } else holder.klasifikasi.setTextColor(Color.RED)

        if (position % 2 == 0) {
            holder.llItemAnak.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.light_gray))
        } else {
            holder.llItemAnak.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.white))
        }
    }
}