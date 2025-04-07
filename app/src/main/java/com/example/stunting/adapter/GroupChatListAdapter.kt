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
import com.example.stunting.database.with_api.response.DataUserGroupByUserIdItem
import com.example.stunting.databinding.ItemGroupChatListAdapterBinding
import com.example.stunting.ui.group_chat.GroupChatActivity
import com.example.stunting.ui.group_chat.GroupChatActivity.Companion.EXTRA_GROUP_ID_TO_GROUP_CHAT
import com.example.stunting.ui.group_chat.GroupChatActivity.Companion.EXTRA_NAMA_TO_GROUP_CHAT
import com.example.stunting.ui.group_chat.GroupChatActivity.Companion.EXTRA_USER_ID_TO_GROUP_CHAT

class GroupChatListAdapter: ListAdapter<DataUserGroupByUserIdItem, GroupChatListAdapter.MyViewHolder>(DIFF_CALLBACK){

    class MyViewHolder(private val binding: ItemGroupChatListAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataUserGroupByUserIdItem) {
            binding.tvTitleGroup.text = item.groups?.namaGroup
            // Format 2025-04-05T12:41:07
            val dateTime = item.groups?.createdAt
            binding.tvDateTime.text = dateTime?.substringBefore("T")

            binding.tvCreatedBy.text = item.createdBy
//            Glide.with(itemView.context)
//                .load(item.mediaCover)
//                .into(binding.ivFinished)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, GroupChatActivity::class.java)
                intent.putExtra(EXTRA_USER_ID_TO_GROUP_CHAT, item.userProfileId)
                intent.putExtra(EXTRA_GROUP_ID_TO_GROUP_CHAT, item.groupId)
                intent.putExtra(EXTRA_NAMA_TO_GROUP_CHAT, item.groups?.namaGroup)
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
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataUserGroupByUserIdItem> =
            object : DiffUtil.ItemCallback<DataUserGroupByUserIdItem>() {
                override fun areItemsTheSame(oldItem: DataUserGroupByUserIdItem, newItem: DataUserGroupByUserIdItem): Boolean {
                    return oldItem.groups?.id == newItem.groups?.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: DataUserGroupByUserIdItem, newItem: DataUserGroupByUserIdItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}