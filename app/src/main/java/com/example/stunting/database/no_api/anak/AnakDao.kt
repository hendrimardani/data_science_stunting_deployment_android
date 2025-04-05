package com.example.stunting.database.no_api.anak

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnakDao {

    @Insert
    suspend fun insert(anakEntity: AnakEntity)

    @Query("DELETE FROM `anak-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `anak-table`")
    fun fetchAllAnak(): Flow<List<AnakEntity>>
}