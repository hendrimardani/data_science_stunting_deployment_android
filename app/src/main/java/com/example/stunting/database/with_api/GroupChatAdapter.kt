package com.example.stunting.database.with_api

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.R
import com.example.stunting.database.with_api.entities.messages.MessagesRelation
import com.example.stunting.databinding.ItemReceiverAdapterBinding
import com.example.stunting.databinding.ItemSenderAdapterBinding
import com.example.stunting.utils.Functions.formatToHourMinute

class GroupChatAdapter(private val currentUserId: Int) :
    ListAdapter<MessagesRelation, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

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
        return if (item.userProfileEntity.userId == currentUserId) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)

        val isFirstMessageFromSender = if (position == 0) {
            true
        } else {
            val previousMessage = getItem(position - 1)
            previousMessage.userProfileEntity.userId != message.userProfileEntity.userId
        }

        when (holder) {
            is SenderViewHolder -> holder.bind(message, isFirstMessageFromSender)
            is ReceiverViewHolder -> holder.bind(message, isFirstMessageFromSender)
        }
    }

    class SenderViewHolder(private val binding: ItemSenderAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(messagesRelation: MessagesRelation, isFirstMessageFromSender: Boolean) {
            val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_person_40)
            val date = messagesRelation.messagesEntity.createdAt.toString()

            binding.tvIsiPesan.text = messagesRelation.messagesEntity.isi_pesan
            binding.tvPukul.text = formatToHourMinute(date)

            if (isFirstMessageFromSender) {
                binding.ivProfile.visibility = View.VISIBLE
            } else {
                binding.ivProfile.visibility = View.GONE
            }
            binding.ivProfile.setImageDrawable(drawable)
        }
    }

    class ReceiverViewHolder(private val binding: ItemReceiverAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(messagesRelation: MessagesRelation, isFirstMessageFromSender: Boolean) {
            val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_person_40)
            val date = messagesRelation.messagesEntity.createdAt.toString()

            binding.tvIsiPesan.text = messagesRelation.messagesEntity.isi_pesan
            binding.tvPukul.text = formatToHourMinute(date)

            if (isFirstMessageFromSender) {
                binding.ivProfile.visibility = View.VISIBLE
                binding.tvNama.visibility = View.VISIBLE
                binding.tvNama.text = messagesRelation.userProfileEntity.nama
            } else {
                binding.ivProfile.visibility = View.GONE
                binding.tvNama.visibility = View.GONE
            }
            binding.ivProfile.setImageDrawable(drawable)
        }
    }

    companion object {
        private const val VIEW_TYPE_SENDER = 1
        private const val VIEW_TYPE_RECEIVER = 2

        val DIFF_CALLBACK: DiffUtil.ItemCallback<MessagesRelation> =
            object : DiffUtil.ItemCallback<MessagesRelation>() {
                override fun areItemsTheSame(
                    oldItem: MessagesRelation,
                    newItem: MessagesRelation
                ): Boolean {
                    return oldItem.messagesEntity.user_id == newItem.messagesEntity.user_id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: MessagesRelation,
                    newItem: MessagesRelation
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
