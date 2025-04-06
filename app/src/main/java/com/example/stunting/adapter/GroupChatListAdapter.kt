package com.example.stunting.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stunting.database.with_api.response.DataUserGroupByUserIdItem
import com.example.stunting.databinding.ItemGroupChatListAdapterBinding

class GroupChatListAdapter: ListAdapter<DataUserGroupByUserIdItem, GroupChatListAdapter.MyViewHolder>(DIFF_CALLBACK){

    class MyViewHolder(private val binding: ItemGroupChatListAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataUserGroupByUserIdItem) {
            binding.tvTitleGroup.text = item.groups?.namaGroup
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