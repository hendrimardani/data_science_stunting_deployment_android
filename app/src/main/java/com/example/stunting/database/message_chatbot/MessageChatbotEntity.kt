package com.example.stunting.database.message_chatbot

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "message-table")
data class MessageChatbotEntity(
    @PrimaryKey
    val date: String,
    val text: String,
    val isSent: Boolean
)