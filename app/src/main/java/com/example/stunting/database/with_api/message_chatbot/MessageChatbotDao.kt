package com.example.stunting.database.with_api.message_chatbot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface MessageChatbotDao {

    @Insert
    suspend fun insert(messageChatbotsEntity: MessageChatbotsEntity)

    @Query("DELETE FROM `message-table` ")
    suspend fun deleteAll()

    @Query("SELECT * FROM `message-table`")
    fun fetchAllMessage(): Flow<List<MessageChatbotsEntity>>
}