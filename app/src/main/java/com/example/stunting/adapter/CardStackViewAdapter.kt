package com.example.stunting.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.databinding.ItemCardStackViewBinding
import com.example.stunting.resouce_data.Services
import com.example.stunting.ui.anak.AnakActivity
import com.example.stunting.ui.bumil.BumilActivity
import com.example.stunting.ui.layanan_keluarga.LayananKeluargaActivity
import com.example.stunting.ui.reamaja_putri.RemajaPutriActivity

class CardStackViewAdapter: ListAdapter<Services, CardStackViewAdapter.MyViewHolder>(DIFF_CALLBACK){

    class MyViewHolder(private val binding: ItemCardStackViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(services: Services) {
            val name = services.name
            val image = services.image

            binding.tvName.text = name
            binding.imageView.setImageResource(image)
            itemView.setOnClickListener {
                when (name) {
                    "Layanan Ibu Hamil" -> {
                        val intent = Intent(itemView.context, BumilActivity::class.java)
                        itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
                    }
                    "Layanan Anak" -> {
                        val intent = Intent(itemView.context, AnakActivity::class.java)
                        itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
                    }
                    "Layanan Remaja Putri" -> {
                        val intent = Intent(itemView.context, RemajaPutriActivity::class.java)
                        itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
                    }
                    "Layanan Keluarga" -> {
                        val intent = Intent(itemView.context, LayananKeluargaActivity::class.java)
                        itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
                    }
                    "Layanan Cegah Stunting" -> {

                    }
                }
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
