package com.example.stunting.database.messages

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface MessageDao {

    @Insert
    suspend fun insert(messageEntity: MessageEntity)

    @Query("DELETE FROM `message-table` ")
    suspend fun deleteAll()

    @Query("SELECT * FROM `message-table`")
    fun fetchAllMessage(): Flow<List<MessageEntity>>
}