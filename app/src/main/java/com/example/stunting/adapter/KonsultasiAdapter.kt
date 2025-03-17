package com.example.stunting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stunting.R
import com.example.stunting.database.message_chatbot.MessageChatbotEntity
import com.example.stunting.functions_helper.Functions.parseTextWithStylesAndRemoveSymbols

class KonsultasiAdapter(val messageList: ArrayList<MessageChatbotEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSentMessage = itemView.findViewById<TextView>(R.id.tv_sent_konsultasi)
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvReceivedMessage = itemView.findViewById<TextView>(R.id.tv_received_konsultasi)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_chatbot_adapter, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_konsultasi_adapter, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isSent) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is SentMessageViewHolder) {
            holder.tvSentMessage.text = message.text
        } else if (holder is ReceivedMessageViewHolder) {
            if (IS_LOADING) holder.progressBar.visibility = View.VISIBLE
            else holder.progressBar.visibility = View.GONE

            // Melakukan filtering simbol seperti **, *, _
            val cleanedText = parseTextWithStylesAndRemoveSymbols(message.text)
            holder.tvReceivedMessage.text = cleanedText
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        var IS_LOADING = false
    }
}
