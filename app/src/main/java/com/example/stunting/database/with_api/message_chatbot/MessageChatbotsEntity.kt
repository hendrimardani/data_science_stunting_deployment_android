package com.example.stunting.database.with_api.message_chatbot

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "message-table")
data class MessageChatbotsEntity(
    @PrimaryKey
    val date: String,
    val text: String,
    val isSender: Boolean
)