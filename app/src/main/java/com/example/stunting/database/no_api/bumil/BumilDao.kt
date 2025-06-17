package com.example.stunting.database.no_api.bumil

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BumilDao {

    @Insert
    suspend fun insertBumil(bumilEntity: BumilEntity)

    @Query("DELETE FROM `bumil-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `bumil-table`")
    fun fetchAllBumil(): Flow<List<BumilEntity>>
}