package com.example.stunting.adapter

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stunting.R
import com.example.stunting.adapter.Interface.OnItemInteractionListener
import com.example.stunting.database.with_api.entities.user_profile.UserProfileWithSelection
import com.example.stunting.databinding.ItemTambahAnggotaAdapterBinding

class DetailGroupTambahAnggotaAdapter(
    private val listener: OnItemInteractionListener
) : ListAdapter<UserProfileWithSelection, DetailGroupTambahAnggotaAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private val selectedIds = mutableListOf<Int>()

    inner class MyViewHolder(private val binding: ItemTambahAnggotaAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentWatcher: TextWatcher? = null

        fun bind(item: UserProfileWithSelection) {
            val userProfile = item.userProfileWithUserRelation.userProfile

            binding.tvNama.text = userProfile?.nama
            binding.tvAlamat.text = userProfile?.alamat

            Glide.with(itemView.context)
                .load(userProfile?.gambarProfile ?: R.drawable.ic_person_40)
                .into(binding.civProfile)

            itemView.setBackgroundColor(
                if (item.isSelected)
                    ContextCompat.getColor(itemView.context, R.color.blueSecond_500)
                else
                    Color.TRANSPARENT
            )

            // Tambahkan TextWatcher hanya jika dipilih
            if (item.isSelected) {
                currentWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        listener.onTextChangedWhileSelected(true)
                    }

                    override fun afterTextChanged(s: Editable?) {  }
                }
            } else {
                listener.onTextChangedWhileSelected(selectedIds.isNotEmpty())
            }

            itemView.setOnClickListener {
                val currentItem = getItem(adapterPosition)
                val updatedItem = currentItem.copy(isSelected = !currentItem.isSelected)

                val newList = currentList.toMutableList().apply {
                    set(adapterPosition, updatedItem)
                }
                submitList(newList)

                val userId = updatedItem.userProfileWithUserRelation.userProfile?.userId
                if (updatedItem.isSelected) {
                    selectedIds.add(userId!!)
                } else {
                    selectedIds.remove(userId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemTambahAnggotaAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserProfileWithSelection>() {
            override fun areItemsTheSame(
                oldItem: UserProfileWithSelection,
                newItem: UserProfileWithSelection
            ): Boolean {
                return oldItem.userProfileWithUserRelation.users.id ==
                        newItem.userProfileWithUserRelation.users.id
            }

            override fun areContentsTheSame(
                oldItem: UserProfileWithSelection,
                newItem: UserProfileWithSelection
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    fun getSelectedIds(): List<Int> = selectedIds
}

