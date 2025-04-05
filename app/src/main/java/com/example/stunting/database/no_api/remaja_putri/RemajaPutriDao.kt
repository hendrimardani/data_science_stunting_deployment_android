package com.example.stunting.database.no_api.remaja_putri

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RemajaPutriDao {

    @Insert
    suspend fun insert(remajaPutriEntity: RemajaPutriEntity)

    @Query("DELETE FROM `remajaPutri-table`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `remajaPutri-table`")
    fun fetchAllRemajaPutri(): Flow<List<RemajaPutriEntity>>
}