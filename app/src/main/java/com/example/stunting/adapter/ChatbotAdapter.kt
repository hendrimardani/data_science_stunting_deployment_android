package com.example.stunting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.R
import com.example.stunting.database.with_api.entities.message_chatbot.MessageChatbotsEntity
import com.example.stunting.utils.Functions.parseTextWithStylesAndRemoveSymbols

class ChatbotAdapter(val messageList: ArrayList<MessageChatbotsEntity>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivProfileSender = itemView.findViewById<ImageView>(R.id.civ_profile)
            val tvIsiPesanSender = itemView.findViewById<TextView>(R.id.tv_isi_pesan)
        }

        class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivProfileReceiver = itemView.findViewById<ImageView>(R.id.civ_profile)
            val tvIsiPesanReceiver = itemView.findViewById<TextView>(R.id.tv_isi_pesan)
            val progressBarReceiver = itemView.findViewById<ProgressBar>(R.id.progressBar)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_SENDER) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sender_adapter, parent, false)
                SenderViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_receiver_adapter, parent, false)
                ReceiverViewHolder(view)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (messageList[position].isSender) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVED
        }

        override fun getItemCount() = messageList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val message = messageList[position]
            if (holder is SenderViewHolder) {
                val drawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_person_40)
                holder.ivProfileSender.setImageDrawable(drawable)
                holder.tvIsiPesanSender.text = message.text
            } else if (holder is ReceiverViewHolder) {
                if (IS_LOADING) holder.progressBarReceiver.visibility = View.VISIBLE
                else holder.progressBarReceiver.visibility = View.GONE

                val drawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_neural_network)
                holder.ivProfileReceiver.setImageDrawable(drawable)
                // Melakukan filtering simbol seperti **, *, _
                val cleanedText = parseTextWithStylesAndRemoveSymbols(message.text)
                holder.tvIsiPesanReceiver.text = cleanedText
            }
        }

        companion object {
            private const val VIEW_TYPE_SENDER = 1
            private const val VIEW_TYPE_RECEIVED = 2
            var IS_LOADING = false
        }
    }