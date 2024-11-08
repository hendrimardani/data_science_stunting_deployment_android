package com.example.stunting.database.messages

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "message-table")
data class MessageEntity(
    @PrimaryKey
    val date: String,
    val text: String,
    val isSent: Boolean
)