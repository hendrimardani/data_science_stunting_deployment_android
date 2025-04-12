package com.example.stunting.database.with_api

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.R
import com.example.stunting.database.with_api.response.DataMessagesByGroupIdItem
import com.example.stunting.databinding.ItemReceiverAdapterBinding
import com.example.stunting.databinding.ItemSenderAdapterBinding

class GroupChatAdapter(private val currentUserId: Int) :
    ListAdapter<DataMessagesByGroupIdItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = getItem(position)
            when (holder) {
                is SenderViewHolder -> holder.bind(item)
                is ReceiverViewHolder -> holder.bind(item)
            }
        }

        class SenderViewHolder(private val binding: ItemSenderAdapterBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun bind(item: DataMessagesByGroupIdItem) {
                    val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_person_40)
                    binding.ivProfile.setImageDrawable(drawable)
                    binding.tvSender.text = item.isiPesan.toString()
                }
            }

        class ReceiverViewHolder(private val binding: ItemReceiverAdapterBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun bind(item: DataMessagesByGroupIdItem) {
                    val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_person_40)
                    binding.ivProfile.setImageDrawable(drawable)
                    binding.tvReceiver.text = item.isiPesan.toString()
                }
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                VIEW_TYPE_SENDER -> {
                    val binding = ItemSenderAdapterBinding.inflate(inflater, parent, false)
                    SenderViewHolder(binding)
                }
                else -> {
                    val binding = ItemReceiverAdapterBinding.inflate(inflater, parent, false)
                    ReceiverViewHolder(binding)
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            val item = getItem(position)
            return if (item.userProfileId == currentUserId) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
        }

        companion object {
            private val TAG = GroupChatAdapter::class.java.simpleName
            private const val VIEW_TYPE_SENDER = 1
            private const val VIEW_TYPE_RECEIVER = 2

            val DIFF_CALLBACK: DiffUtil.ItemCallback<DataMessagesByGroupIdItem> =
                object : DiffUtil.ItemCallback<DataMessagesByGroupIdItem>() {
                    override fun areItemsTheSame(
                        oldItem: DataMessagesByGroupIdItem,
                        newItem: DataMessagesByGroupIdItem
                    ): Boolean {
                        return oldItem.groupId == newItem.groupId
                    }

                    @SuppressLint("DiffUtilEquals")
                    override fun areContentsTheSame(
                        oldItem: DataMessagesByGroupIdItem,
                        newItem: DataMessagesByGroupIdItem
                    ): Boolean {
                        return oldItem == newItem
                    }
                }
        }
    }