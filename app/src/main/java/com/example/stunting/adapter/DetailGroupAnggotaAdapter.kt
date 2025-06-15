package com.example.stunting.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stunting.R
import com.example.stunting.database.with_api.entities.user_group.UserGroupRelation
import com.example.stunting.databinding.ItemTambahGroupAdapterBinding

class DetailGroupAnggotaAdapter: ListAdapter<UserGroupRelation, DetailGroupAnggotaAdapter.MyViewHolder>(DIFF_CALLBACK){

    class MyViewHolder(private val binding: ItemTambahGroupAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(userGroupRelation: UserGroupRelation) {
            val userProfile = userGroupRelation.userProfileEntity
            val userGroup = userGroupRelation.userGroupEntity

            if (userGroup.role == "admin") binding.flAdmin.visibility = View.VISIBLE
            else binding.flAdmin.visibility = View.GONE

            if (userProfile.gambarProfile != null) {
                Glide.with(itemView.context)
                    .load(userProfile.gambarProfile)
                    .into(binding.civProfile)
            } else {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_person_40)
                    .into(binding.civProfile)
            }

            binding.tvNama.text = userProfile.nama
            binding.tvAlamat.text = userProfile.alamat
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemTambahGroupAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    companion object {
        private val TAG = DetailGroupAnggotaAdapter::class.java.simpleName
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UserGroupRelation> =
            object : DiffUtil.ItemCallback<UserGroupRelation>() {
                override fun areItemsTheSame(oldItem: UserGroupRelation, newItem: UserGroupRelation): Boolean {
                    return oldItem.userProfileEntity.userId == newItem.userProfileEntity.userId
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: UserGroupRelation, newItem: UserGroupRelation): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
