package com.example.stunting.adapter

import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.database.no_api.calon_pengantin.CalonPengantinEntity
import com.example.stunting.database.no_api.remaja_putri.RemajaPutriEntity
import com.example.stunting.R
import com.example.stunting.database.with_api.entities.checks.ChecksRelation
import com.example.stunting.databinding.ItemAllAdapterBinding

class BumilAdapter(val items: List<ChecksRelation>) : RecyclerView.Adapter<BumilAdapter.ViewHolder>() {

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
        val userProfilePatientEntity = item.userProfilePatientEntity
        val checkEntity = item.checksEntity
        holder.tgl.text = checkEntity.tglPemeriksaan
        holder.nik.text = userProfilePatientEntity.nikBumil
        holder.nama.text = userProfilePatientEntity.namaBumil

        // Inside the RecyclerView Adapter's onBindViewHolder method
        val context = holder.itemView.context
        val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            // Mode Gelap: Ganjil merah, Genap putih
            if (position % 2 == 0) {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            } else {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
            }
        } else {
            // Mode Terang: Ganjil putih, Genap merah
            if (position % 2 == 0) {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
            }
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

        // Inside the RecyclerView Adapter's onBindViewHolder method
        val context = holder.itemView.context
        val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            // Mode Gelap: Ganjil merah, Genap putih
            if (position % 2 == 0) {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            } else {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
            }
        } else {
            // Mode Terang: Ganjil putih, Genap merah
            if (position % 2 == 0) {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
            }
        }
    }
}

class RemajaPutriAdapter(val items: ArrayList<RemajaPutriEntity>) : RecyclerView.Adapter<RemajaPutriAdapter.ViewHolder>() {

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
        holder.nik.text = item.nikRemajaPutri
        holder.nama.text = item.namaRemajaPutri

        // Inside the RecyclerView Adapter's onBindViewHolder method
        val context = holder.itemView.context
        val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        if (isDarkMode) {
            // Mode Gelap: Ganjil merah, Genap putih
            if (position % 2 == 0) {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            } else {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.light_black))
            }
        } else {
            // Mode Terang: Ganjil putih, Genap merah
            if (position % 2 == 0) {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
            }
        }
    }
}

