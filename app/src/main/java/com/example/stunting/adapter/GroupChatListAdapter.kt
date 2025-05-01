package com.example.stunting.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stunting.R
import com.example.stunting.database.with_api.entities.user_group.UserGroupRelation
import com.example.stunting.databinding.ItemGroupChatListAdapterBinding
import com.example.stunting.ui.group_chat.GroupChatActivity
import com.example.stunting.ui.group_chat.GroupChatActivity.Companion.EXTRA_GROUP_ID_TO_GROUP_CHAT
import com.example.stunting.ui.group_chat.GroupChatActivity.Companion.EXTRA_NAMA_TO_GROUP_CHAT
import com.example.stunting.ui.group_chat.GroupChatActivity.Companion.EXTRA_USER_ID_TO_GROUP_CHAT

class GroupChatListAdapter: ListAdapter<UserGroupRelation, GroupChatListAdapter.MyViewHolder>(DIFF_CALLBACK){

    class MyViewHolder(private val binding: ItemGroupChatListAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: UserGroupRelation) {
            binding.tvNamaGroup.text = item.groupsEntity.namaGroup
            binding.tvDeskripsi.text = item.groupsEntity.deskripsi
            val gambarProfile = item.groupsEntity.gambarProfile
            // 2025-04-05T12:41:07
            val dateTime = item.groupsEntity.createdAt
            binding.tvTanggal.text = dateTime?.substringBefore("T")

            binding.tvCreatedBy.text = item.userGroupEntity.createdBy
            if (gambarProfile != null) {
                Glide.with(itemView.context)
                    .load(gambarProfile)
                    .into(binding.civProfile)
            } else {
                Glide.with(itemView.context)
                    .load(itemView.context.getDrawable(R.drawable.ic_avatar_group_chat))
                    .into(binding.civProfile)
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, GroupChatActivity::class.java)
                intent.putExtra(EXTRA_USER_ID_TO_GROUP_CHAT, item.userGroupEntity.user_id)
                intent.putExtra(EXTRA_GROUP_ID_TO_GROUP_CHAT, item.userGroupEntity.group_id)
                intent.putExtra(EXTRA_NAMA_TO_GROUP_CHAT, item.groupsEntity.namaGroup)
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemGroupChatListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    companion object {
        private val TAG = GroupChatListAdapter::class.java.simpleName
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UserGroupRelation> =
            object : DiffUtil.ItemCallback<UserGroupRelation>() {
                override fun areItemsTheSame(oldItem: UserGroupRelation, newItem: UserGroupRelation): Boolean {
                    return oldItem.userGroupEntity.group_id == newItem.userGroupEntity.group_id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: UserGroupRelation, newItem: UserGroupRelation): Boolean {
                    return oldItem == newItem
                }
            }
    }
}