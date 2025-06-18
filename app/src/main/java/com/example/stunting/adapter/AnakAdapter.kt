package com.example.stunting.adapter

import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.database.no_api.anak.AnakEntity
import com.example.stunting.R
import com.example.stunting.database.with_api.entities.checks.ChecksRelation
import com.example.stunting.databinding.ItemAnakAdapterBinding

class AnakAdapter(val items: List<ChecksRelation>) : RecyclerView.Adapter<AnakAdapter.ViewHolder>() {

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
        val childrenPatientEntity = item.childrenPatientEntity
        val childServiceEntity = item.childServiceEntity
        val checkEntity = item.checksEntity

        holder.tglAnak.text = checkEntity.tglPemeriksaan
        holder.namaAnak.text = childrenPatientEntity.namaAnak
        holder.klasifikasi.text = childServiceEntity?.hasilPemeriksaan

        // Change color text classification display
        if (childServiceEntity?.hasilPemeriksaan == ContextCompat.getString(holder.itemView.context, R.string.classification_normal)) {
            holder.klasifikasi.setTextColor(Color.GREEN)
        } else holder.klasifikasi.setTextColor(Color.RED)

        // Inside the RecyclerView Adapter's onBindViewHolder method
        val context = holder.itemView.context
        val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            // Mode Gelap: Ganjil merah, Genap putih
            if (position % 2 == 0) {
                holder.llItemAnak.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            } else {
                holder.llItemAnak.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
            }
        } else {
            // Mode Terang: Ganjil putih, Genap merah
            if (position % 2 == 0) {
                holder.llItemAnak.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.llItemAnak.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
            }
        }
    }
}