package com.example.stunting.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stunting.database.with_api.groups.GroupsEntity
import com.example.stunting.databinding.ItemGroupChatListAdapterBinding

class GroupChatListAdapter: ListAdapter<GroupsEntity, GroupChatListAdapter.MyViewHolder>(DIFF_CALLBACK){

    class MyViewHolder(private val binding: ItemGroupChatListAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupsEntity) {
            binding.tvTitleGroup.text = item.namaGroup
//            Glide.with(itemView.context)
//                .load(item.mediaCover)
//                .into(binding.ivFinished)
//            itemView.setOnClickListener {
//                val intent = Intent(itemView.context, DetailActivity::class.java)
//                intent.putExtra(EXTRA_ACTIVITY, EVENT_ADAPTER)
//                intent.putExtra(EXTRA_ID, item.id)
//                itemView.context.startActivity(intent)
//            }
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

        val DIFF_CALLBACK: DiffUtil.ItemCallback<GroupsEntity> =
            object : DiffUtil.ItemCallback<GroupsEntity>() {
                override fun areItemsTheSame(oldItem: GroupsEntity, newItem: GroupsEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: GroupsEntity, newItem: GroupsEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}